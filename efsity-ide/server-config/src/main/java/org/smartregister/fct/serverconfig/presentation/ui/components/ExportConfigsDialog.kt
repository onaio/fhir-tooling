package org.smartregister.fct.serverconfig.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import org.smartregister.fct.aurora.presentation.ui.components.Button
import org.smartregister.fct.common.presentation.ui.dialog.rememberAlertDialog
import org.smartregister.fct.common.presentation.ui.dialog.rememberDialog
import org.smartregister.fct.serverconfig.domain.model.ExportDialogState
import org.smartregister.fct.serverconfig.presentation.components.ExportConfigDialogComponent
import org.smartregister.fct.serverconfig.presentation.components.ServerConfigPanelComponent

context (ServerConfigPanelComponent)
@Composable
internal fun ExportConfigsDialog() {

    val state by exportDialogState.subscribeAsState()

    val exportConfigDialog = rememberDialog<Unit>(
        title = "Export Configs",
        width = 300.dp,
        height = 500.dp,
        onDismiss = { hideExportConfigDialog() }
    ) { _, _ ->

        if (state is ExportDialogState.SelectConfigs) {
            val selectConfigState = state as ExportDialogState.SelectConfigs
            with(selectConfigState.component) {
                ImportExportContent(selectConfigState.configs) {
                    ExportButton()
                }
            }
        }
    }

    val alertDialog = rememberAlertDialog(
        title = "Export Configs",
        message = "Configs exported successfully",
        onDismiss = {
            hideExportConfigDialog()
        }
    )

    val launcher = rememberFileSaverLauncher {
        it?.let {
            alertDialog.show()
        }
    }

    when (val smartState = state) {
        is ExportDialogState.SelectConfigs -> {
            exportConfigDialog.show()
        }

        is ExportDialogState.ExportFileDialog -> {
            exportConfigDialog.hide()
            launcher.launch(
                baseName = "server_configs",
                extension = "json",
                //initialDirectory = "",
                bytes = smartState.configJson.encodeToByteArray()
            )
        }

        is ExportDialogState.ExportCompleteDialog -> {
            alertDialog.show()
        }

        else -> {
            alertDialog.hide()
            exportConfigDialog.hide()
        }
    }
}

context (ExportConfigDialogComponent, ServerConfigPanelComponent)
@Composable
private fun ExportButton() {

    Button(
        modifier = Modifier.fillMaxWidth(),
        label = "Export",
        enable = checkedConfigs.subscribeAsState().value.isNotEmpty(),
        onClick = ::export
    )

}