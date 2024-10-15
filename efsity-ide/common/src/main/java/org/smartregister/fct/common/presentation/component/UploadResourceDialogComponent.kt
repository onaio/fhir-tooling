package org.smartregister.fct.common.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.apiclient.domain.model.AuthRequest
import org.smartregister.fct.apiclient.domain.model.AuthResponse
import org.smartregister.fct.apiclient.domain.model.UploadResourceRequest
import org.smartregister.fct.apiclient.domain.model.UploadResponse
import org.smartregister.fct.apiclient.domain.usecase.AuthenticateClient
import org.smartregister.fct.apiclient.domain.usecase.UploadResource
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.domain.model.ServerConfig
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.decodeResourceFromString
import org.smartregister.fct.engine.util.logicalId
import org.smartregister.fct.logger.FCTLogger

internal class UploadResourceDialogComponent(
    componentContext: ComponentContext,
    private val resourceJson: String
) : KoinComponent, ComponentContext by componentContext {

    private val appSettingManager: AppSettingManager by inject()
    private val uploadResource: UploadResource by inject()
    private val authenticateClient: AuthenticateClient by inject()

    private val _configs = MutableValue<List<ServerConfig>>(listOf())
    val configs: Value<List<ServerConfig>> = _configs

    private val _selectedConfig = MutableStateFlow<ServerConfig?>(null)
    val selectedConfig: StateFlow<ServerConfig?> = _selectedConfig

    val defaultResourceId = MutableValue("")
    var resourceType: String? = null
        private set

    val uploading = MutableValue(false)
    val uploadError = MutableStateFlow<String?>(null)
    val uploadSuccess = MutableStateFlow<String?>(null)

    init {
        loadConfigs()
        loadResource()
    }

    private fun loadConfigs() {
        componentScope.launch {
            appSettingManager.appSetting.getServerConfigsAsFlow().collectLatest {
                _configs.value = it
            }
        }
    }

    private fun loadResource() {
        componentScope.launch {
            try {
                val content = JSONObject(resourceJson)
                if (resourceIsConfig(content)) {
                    resourceType = ResourceType.Binary.name
                } else {
                    val resource = resourceJson.decodeResourceFromString<Resource>()
                    defaultResourceId.value = resource.logicalId
                    resourceType = resource.resourceType.name
                }

            } catch (ex: Exception) {
                FCTLogger.e(ex)
                resourceType = ResourceType.Binary.name
            }
        }
    }

    fun selectConfig(config: ServerConfig) {
        componentScope.launch {
            _selectedConfig.emit(config)
        }
    }

    fun upload() {
        resetError()
        uploading.value = true
        componentScope.launch {
            if (selectedConfig.value != null && defaultResourceId.value.trim().isNotEmpty() && resourceType != null) {
                val response = uploadResource(
                    request = UploadResourceRequest(
                        accessToken = selectedConfig.value!!.authToken,
                        url = selectedConfig.value!!.fhirBaseUrl,
                        resourceType = resourceType!!,
                        id = defaultResourceId.value,
                        body = resourceJson
                    )
                )
                handleUploadResponse(response)
            }
        }
    }

    private fun handleUploadResponse(response: UploadResponse) {

        componentScope.launch {
            when (response) {
                is UploadResponse.Success -> {
                    uploading.value = false
                    uploadSuccess.emit("$resourceType successfully uploaded")
                }
                is UploadResponse.UnAuthorized -> {
                    FCTLogger.w("UnAuthorized")
                    FCTLogger.d("Re Authenticating")
                    authenticate()
                }
                is UploadResponse.Failed -> {
                    uploading.value = false
                    response.outcome.issue.firstOrNull()?.diagnostics?.let(::showError)
                }
            }
        }
    }

    private fun authenticate() {
        componentScope.launch {
            val config = selectedConfig.value!!
            val response = authenticateClient(
                AuthRequest(
                    oAuthUrl = config.oAuthUrl,
                    clientId = config.clientId,
                    clientSecret = config.clientSecret,
                    username = config.username,
                    password = config.password
                )
            )

            uploading.value = false
            if (response is AuthResponse.Success) {
                val updatedConfig = config.copy(
                    authToken = response.accessToken
                )
                updateSetting(updatedConfig)
            } else if (response is AuthResponse.Failed) {
                val description = response.description?.let { "\n${it}" }
                showError("${response.error}$description")
            }
        }
    }

    private suspend fun updateSetting(updatedConfig: ServerConfig) {
        _configs.value.map {
            if (it.id == updatedConfig.id) {
                _selectedConfig.value = updatedConfig
                updatedConfig
            } else {
                it
            }
        }.let {
            appSettingManager.appSetting.updateServerConfigs(it)
            appSettingManager.update()
        }.also {
            _selectedConfig.value?.authToken?.trim()?.isNotEmpty()?.run {
                upload()
            }
        }
    }

    private fun resourceIsConfig(jsonObject: JSONObject): Boolean {
        return jsonObject.has("appId") &&
                jsonObject.optString("configType", "") in listOf(
            "application",
            "sync",
            "navigation",
            "register",
            "measureReport",
            "profile",
            "geoWidget",
            "dataMigration",
        )
    }

    private fun resetError() {
        componentScope.launch {
            uploadError.emit(null)
        }
    }

    private fun showError(error: String) {
        componentScope.launch {
            uploadError.emit(error)
        }
    }
}