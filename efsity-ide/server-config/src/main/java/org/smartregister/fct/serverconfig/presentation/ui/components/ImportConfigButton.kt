package org.smartregister.fct.serverconfig.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.runtime.Composable
import fct.server_config.generated.resources.Res
import fct.server_config.generated.resources.import
import org.smartregister.fct.aurora.presentation.ui.components.TextButton
import org.smartregister.fct.serverconfig.presentation.components.ServerConfigPanelComponent
import org.smartregister.fct.serverconfig.util.asString

context (ServerConfigPanelComponent)
@Composable
internal fun ImportConfigButton() {
    TextButton(
        label = Res.string.import.asString(),
        icon = Icons.Outlined.Download,
        onClick = ::showImportConfigDialog
    )
}