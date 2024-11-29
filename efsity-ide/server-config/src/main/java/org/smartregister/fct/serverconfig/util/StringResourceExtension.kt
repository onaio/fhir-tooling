package org.smartregister.fct.serverconfig.util

import androidx.compose.runtime.Composable
import fct.server_config.generated.resources.Res
import fct.server_config.generated.resources.auth_token
import fct.server_config.generated.resources.client_id
import fct.server_config.generated.resources.client_secret
import fct.server_config.generated.resources.config_verified
import fct.server_config.generated.resources.create_new
import fct.server_config.generated.resources.description
import fct.server_config.generated.resources.error
import fct.server_config.generated.resources.export_configs
import fct.server_config.generated.resources.failed
import fct.server_config.generated.resources.fhir_base_url
import fct.server_config.generated.resources.import
import fct.server_config.generated.resources.import_configs
import fct.server_config.generated.resources.new_config
import fct.server_config.generated.resources.oauth_url
import fct.server_config.generated.resources.password
import fct.server_config.generated.resources.save
import fct.server_config.generated.resources.setting_saved
import fct.server_config.generated.resources.success
import fct.server_config.generated.resources.username
import fct.server_config.generated.resources.verify
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.StringResource

private val stringMap: Map<StringResource, String> = mapOf(
    Res.string.create_new to "Create New",
    Res.string.new_config to "New Config",
    Res.string.import to "Import",
    Res.string.import_configs to "Import Configs",
    Res.string.export_configs to "Export Configs",
    Res.string.username to "Username",
    Res.string.password to "Password",
    Res.string.fhir_base_url to "Fhir Base Url",
    Res.string.oauth_url to "OAuth Url",
    Res.string.client_id to "Client Id",
    Res.string.client_secret to "Client Secret",
    Res.string.auth_token to "Auth Token",
    Res.string.save to "Save",
    Res.string.verify to "Verify",
    Res.string.setting_saved to "Setting Saved",
    Res.string.config_verified to "%s config successfully verified",
    Res.string.success to "Success",
    Res.string.failed to "Failed",
    Res.string.error to "Error",
    Res.string.description to "Description",
)

@Composable
fun StringResource.asString(vararg formatArgs: Any) = stringMap[this]!!

context (CoroutineScope)
fun StringResource.asString() = stringMap[this]!!