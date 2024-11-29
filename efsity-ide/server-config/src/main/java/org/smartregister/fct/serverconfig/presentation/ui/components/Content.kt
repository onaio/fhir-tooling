package org.smartregister.fct.serverconfig.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import fct.server_config.generated.resources.Res
import fct.server_config.generated.resources.auth_token
import fct.server_config.generated.resources.client_id
import fct.server_config.generated.resources.client_secret
import fct.server_config.generated.resources.config_verified
import fct.server_config.generated.resources.description
import fct.server_config.generated.resources.error
import fct.server_config.generated.resources.fhir_base_url
import fct.server_config.generated.resources.oauth_url
import fct.server_config.generated.resources.password
import fct.server_config.generated.resources.save
import fct.server_config.generated.resources.setting_saved
import fct.server_config.generated.resources.username
import fct.server_config.generated.resources.verify
import org.smartregister.fct.apiclient.domain.model.AuthResponse
import org.smartregister.fct.aurora.presentation.ui.components.Button
import org.smartregister.fct.aurora.presentation.ui.components.OutlinedButton
import org.smartregister.fct.aurora.presentation.ui.components.TextField
import org.smartregister.fct.common.data.locals.AuroraLocal
import org.smartregister.fct.common.domain.model.Message
import org.smartregister.fct.serverconfig.domain.model.VerifyConfigState
import org.smartregister.fct.serverconfig.presentation.components.ServerConfigComponent
import org.smartregister.fct.serverconfig.util.asString

context (ServerConfigComponent)
@Composable
internal fun ImportExportContent() {

    val verifyConfigState by verifyConfigState.subscribeAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(Modifier.fillMaxWidth()) {
                Box(Modifier.weight(1f)) {
                    TextFieldItem(
                        text = username.subscribeAsState(),
                        label = Res.string.username.asString(),
                        onValueChange = ::setUsername,
                        readOnly = verifyConfigState is VerifyConfigState.Authenticating
                    )
                }
                Spacer(Modifier.width(12.dp))
                Box(Modifier.weight(1f)) {
                    TextFieldItem(
                        text = password.subscribeAsState(),
                        label = Res.string.password.asString(),
                        onValueChange = ::setPassword,
                        readOnly = verifyConfigState is VerifyConfigState.Authenticating
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            TextFieldItem(
                text = fhirBaseUrl.subscribeAsState(),
                label = Res.string.fhir_base_url.asString(),
                onValueChange = ::setFhirBaseUrl,
                readOnly = verifyConfigState is VerifyConfigState.Authenticating
            )
            Spacer(Modifier.height(12.dp))
            TextFieldItem(
                text = oAuthUrl.subscribeAsState(),
                label = Res.string.oauth_url.asString(),
                onValueChange = ::setOAuthUrl,
                readOnly = verifyConfigState is VerifyConfigState.Authenticating
            )
            Spacer(Modifier.height(12.dp))
            TextFieldItem(
                text = clientId.subscribeAsState(),
                label = Res.string.client_id.asString(),
                onValueChange = ::setClientId,
                readOnly = verifyConfigState is VerifyConfigState.Authenticating
            )
            Spacer(Modifier.height(12.dp))
            TextFieldItem(
                text = clientSecret.subscribeAsState(),
                label = Res.string.client_secret.asString(),
                onValueChange = ::setClientSecret,
                readOnly = verifyConfigState is VerifyConfigState.Authenticating
            )
            Spacer(Modifier.height(12.dp))
            TextFieldItem(
                text = authToken.subscribeAsState(),
                label = Res.string.auth_token.asString(),
                readOnly = true,
                onValueChange = {}
            )
            Spacer(Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { save() },
                    label = Res.string.save.asString(),
                    enable = verifyConfigState !is VerifyConfigState.Authenticating
                )
                Spacer(Modifier.width(12.dp))

                when (val state = verifyConfigState) {
                    is VerifyConfigState.Idle -> {
                        OutlinedButton(
                            label = Res.string.verify.asString(),
                            onClick = ::verifyAuthConfig
                        )
                    }

                    is VerifyConfigState.Authenticating -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    is VerifyConfigState.Result -> {
                        ShowAuthResponseSnackbar(state.authResponse)
                    }
                }
            }
        }

        val aurora = AuroraLocal.current
        if (settingSaved.subscribeAsState().value) {
            aurora?.showSnackbar(Res.string.setting_saved.asString())
            settingSaved.value = false
        }
    }
}

@Composable
internal fun TextFieldItem(
    modifier: Modifier = Modifier,
    text: State<String>,
    label: String,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit
) {

    Row {
        TextField(
            modifier = modifier.fillMaxWidth(),
            value = text.value,
            onValueChange = onValueChange,
            label = label,
            readOnly = readOnly,
        )
    }
}

context (ServerConfigComponent)
@Composable
fun ShowAuthResponseSnackbar(authResponse: AuthResponse) {

    val aurora = AuroraLocal.current

    if (authResponse is AuthResponse.Success) {
        aurora?.showSnackbar(Res.string.config_verified.asString().format(serverConfig.title))
    } else if (authResponse is AuthResponse.Failed) {
        val errorDetail = "${Res.string.error.asString()}: ${authResponse.error}".let { e ->
            authResponse
                .description
                ?.let {
                    "$e\n${Res.string.description.asString()}: $it"
                } ?: e
        }
        aurora?.showSnackbar(Message.Error(errorDetail)) {
            verifyConfigState.value = VerifyConfigState.Idle
        }
    }
}