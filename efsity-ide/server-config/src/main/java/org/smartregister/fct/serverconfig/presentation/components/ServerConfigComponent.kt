package org.smartregister.fct.serverconfig.presentation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.apiclient.domain.model.AuthRequest
import org.smartregister.fct.apiclient.domain.model.AuthResponse
import org.smartregister.fct.apiclient.domain.usecase.AuthenticateClient
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.domain.model.ServerConfig
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.serverconfig.domain.model.VerifyConfigState

internal class ServerConfigComponent(
    componentContext: ComponentContext,
    val serverConfig: ServerConfig,
) : KoinComponent, ComponentContext by componentContext {

    private val authenticateClient: AuthenticateClient by inject()
    private val appSettingManager: AppSettingManager by inject()
    private val appSetting = appSettingManager.appSetting

    private val _fhirBaseUrl = MutableValue(serverConfig.fhirBaseUrl)
    val fhirBaseUrl: Value<String> = _fhirBaseUrl

    private val _oAuthUrl = MutableValue(serverConfig.oAuthUrl)
    val oAuthUrl: Value<String> = _oAuthUrl

    private val _clientId = MutableValue(serverConfig.clientId)
    val clientId: Value<String> = _clientId

    private val _clientSecret = MutableValue(serverConfig.clientSecret)
    val clientSecret: Value<String> = _clientSecret

    private val _username = MutableValue(serverConfig.username)
    val username: Value<String> = _username

    private val _password = MutableValue(serverConfig.password)
    val password: Value<String> = _password

    private val _authToken = MutableValue(serverConfig.authToken)
    val authToken: Value<String> = _authToken

    val settingSaved = MutableValue(false)
    val verifyConfigState = MutableValue<VerifyConfigState>(VerifyConfigState.Idle)

    fun setFhirBaseUrl(text: String) {
        _fhirBaseUrl.value = text
    }

    fun setOAuthUrl(text: String) {
        _oAuthUrl.value = text
    }

    fun setClientId(text: String) {
        _clientId.value = text
    }

    fun setClientSecret(text: String) {
        _clientSecret.value = text
    }

    fun setUsername(text: String) {
        _username.value = text
    }

    fun setPassword(text: String) {
        _password.value = text
    }

    fun save(showSnackbar: Boolean = true) {
        componentScope.launch {
            val updatedConfig = serverConfig.copy(
                fhirBaseUrl = _fhirBaseUrl.value,
                oAuthUrl = _oAuthUrl.value,
                clientId = _clientId.value,
                clientSecret = _clientSecret.value,
                username = _username.value,
                password = _password.value,
                authToken = _authToken.value
            )

            appSetting
                .serverConfigs
                .map {
                    if (it.id == updatedConfig.id) updatedConfig
                    else it
                }.run {
                    appSetting.updateServerConfigs(this)
                    appSettingManager.update()
                }.also {
                    if (showSnackbar) settingSaved.value = true
                }
        }
    }

    fun verifyAuthConfig() {
        componentScope.launch {

            verifyConfigState.value = VerifyConfigState.Authenticating

            val response = authenticateClient(
                AuthRequest(
                    oAuthUrl = oAuthUrl.value,
                    clientId = clientId.value,
                    clientSecret = clientSecret.value,
                    username = username.value,
                    password = password.value
                )
            )

            if (response is AuthResponse.Success) {
                _authToken.value = response.accessToken
                save(showSnackbar = false)
            }

            verifyConfigState.value = VerifyConfigState.Result(response)
        }
    }
}