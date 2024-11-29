package org.smartregister.fct.apiclient.data.datasource

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.serialization.gson.gson
import io.ktor.serialization.kotlinx.json.json
import org.hl7.fhir.r4.model.OperationOutcome
import org.smartregister.fct.apiclient.data.deserializer.OperationOutcomeTypeAdapter
import org.smartregister.fct.apiclient.domain.datasource.ApiClientDataSource
import org.smartregister.fct.apiclient.domain.model.AuthRequest
import org.smartregister.fct.apiclient.domain.model.AuthResponse
import org.smartregister.fct.apiclient.domain.model.Request
import org.smartregister.fct.apiclient.domain.model.Response
import org.smartregister.fct.apiclient.domain.model.UploadResourceRequest
import org.smartregister.fct.apiclient.domain.model.UploadResponse
import org.smartregister.fct.apiclient.util.asOperationOutcome
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.domain.model.HttpMethodType
import org.smartregister.fct.engine.domain.model.ServerConfig
import org.smartregister.fct.engine.util.getKoinInstance
import org.smartregister.fct.logger.FCTLogger

internal class KtorApiClientDataSource(private val gson: Gson) : ApiClientDataSource {

    override suspend fun auth(request: AuthRequest): AuthResponse {

        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        return try {
            val response = client.submitForm(
                url = request.oAuthUrl.also { FCTLogger.d("oAuthUrl = ${request.oAuthUrl}") },
                formParameters = parameters {
                    append("grant_type", "password")
                    append("scope", "openid")
                    append("username", request.username)
                    append("password", request.password)
                    append("client_id", request.clientId)
                    append("client_secret", request.clientSecret)
                }.also {
                    FCTLogger.d(it.toString())
                }
            )

            if (response.status == HttpStatusCode.OK) {
                val success: AuthResponse.Success = response.body()
                success.httpStatusCode = response.status.value
                success.httpStatus = HttpStatusCode.fromValue(success.httpStatusCode).description
                FCTLogger.i(success.asPrettyJson())
                success
            } else {
                val failed: AuthResponse.Failed = response.body()
                failed.httpStatusCode = response.status.value
                failed.httpStatus = HttpStatusCode.fromValue(failed.httpStatusCode).description
                FCTLogger.e(failed.asPrettyJson())
                failed
            }

        } catch (ex: Exception) {
            FCTLogger.e(ex)
            AuthResponse.Failed(
                error = ex.message ?: "Unknown Error"
            )
        } finally {
            client.close()
        }
    }

    override suspend fun upload(
        request: UploadResourceRequest
    ): UploadResponse {
        val client = HttpClient(CIO) {

            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(request.accessToken, "").also {
                            FCTLogger.d("AuthToken -> ${it.accessToken}")
                        }
                    }
                }
            }

            install(ContentNegotiation) {
                gson {
                    registerTypeAdapter(
                        OperationOutcome::class.java,
                        OperationOutcomeTypeAdapter()
                    )
                }
            }
        }

        return try {
            val response = client.request(request.url) {
                method = HttpMethod.Put
                url {
                    appendPathSegments(request.resourceType, request.id).also {
                        FCTLogger.d("PUT -> $it")
                    }
                }
                contentType(ContentType.Application.Json)
                setBody(request.body)
            }

            when (response.status) {
                HttpStatusCode.OK -> UploadResponse.Success
                HttpStatusCode.Unauthorized -> UploadResponse.UnAuthorized
                else -> {
                    val outcome: OperationOutcome = response.body()
                    FCTLogger.e(response.bodyAsText())
                    UploadResponse.Failed(outcome)
                }
            }
        } catch (ex: Exception) {
            FCTLogger.e(ex)
            UploadResponse.Failed(ex.asOperationOutcome())

        } finally {
            client.close()
        }

    }

    override suspend fun request(request: Request): Response {
        val client = HttpClient(CIO) {

            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(request.config.authToken, "").also {
                            FCTLogger.d("AuthToken -> ${it.accessToken}")
                        }
                    }
                }
            }

            install(ContentNegotiation) {
                gson {
                    registerTypeAdapter(
                        OperationOutcome::class.java,
                        OperationOutcomeTypeAdapter()
                    )
                }
            }
        }

        return try {
            val response = client.request(request.config.fhirBaseUrl) {
                method = resolveMethod(request.methodType)

                url {
                    appendPathSegments(request.resourceType, request.resourceId).also {
                        FCTLogger.d("${request.methodType.name} -> $it")
                    }
                }

                if (method == HttpMethod.Post || method == HttpMethod.Put) {
                    contentType(ContentType.Application.Json)
                    setBody(request.body)
                }
            }

            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created -> {
                    Response.Success(
                        httpStatusCode = response.status.value,
                        httpStatus = HttpStatusCode.fromValue(response.status.value).description,
                        response = response.bodyAsText(),
                    )
                }

                HttpStatusCode.Unauthorized -> {

                    when(val authResponse = auth(createAuthRequest(request.config))) {
                        is AuthResponse.Success -> {
                            request.config.authToken = authResponse.accessToken
                            updateServerConfig(request.config)
                            request(request)
                        }
                        is AuthResponse.Failed -> {
                            createFailedResponse(HttpStatusCode.fromValue(authResponse.httpStatusCode), authResponse.asOperationOutcome())
                        }
                    }
                }

                else -> {
                    val outcome: OperationOutcome = response.body()
                    FCTLogger.e(response.bodyAsText())
                    createFailedResponse(response.status, outcome)
                }
            }
        } catch (ex: Exception) {
            FCTLogger.e(ex)
            createFailedResponse(
                status = HttpStatusCode.fromValue(0),
                ex.asOperationOutcome()
            )

        } finally {
            client.close()
        }
    }

    private fun createFailedResponse(status: HttpStatusCode, outcome: OperationOutcome): Response {
        return Response.Failed(
            httpStatusCode = status.value,
            httpStatus = HttpStatusCode.fromValue(status.value).description,
            outcome = outcome
        )
    }

    private fun resolveMethod(httpMethodType: HttpMethodType) : HttpMethod = when (httpMethodType) {
        is HttpMethodType.Get -> HttpMethod.Get
        is HttpMethodType.Post -> HttpMethod.Post
        is HttpMethodType.Put -> HttpMethod.Put
        is HttpMethodType.Delete -> HttpMethod.Delete
    }

    private fun createAuthRequest(config: ServerConfig) = AuthRequest (
        oAuthUrl = config.oAuthUrl,
        clientId = config.clientId,
        clientSecret = config.clientSecret,
        username = config.username,
        password = config.password
    )

    private suspend fun updateServerConfig(serverConfig: ServerConfig) {
        val appSettingManager = getKoinInstance<AppSettingManager>()
        val appSetting = appSettingManager.appSetting

        val configs = appSetting.serverConfigs.map {
            if (it.id == serverConfig.id) serverConfig else it
        }

        appSetting.updateServerConfigs(configs)
        appSettingManager.update()
    }

    private fun <T> T.asPrettyJson(): String {
        return gson.toJson(this)
    }
}