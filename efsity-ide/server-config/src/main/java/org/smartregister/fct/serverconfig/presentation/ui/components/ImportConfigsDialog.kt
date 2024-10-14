package org.smartregister.fct.serverconfig.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.smartregister.fct.aurora.presentation.ui.components.Button
import org.smartregister.fct.common.presentation.ui.dialog.DialogType
import org.smartregister.fct.common.presentation.ui.dialog.rememberAlertDialog
import org.smartregister.fct.common.presentation.ui.dialog.rememberDialog
import org.smartregister.fct.engine.data.enums.FileType
import org.smartregister.fct.fm.presentation.ui.dialog.rememberFileProviderDialog
import org.smartregister.fct.serverconfig.domain.model.ImportDialogState
import org.smartregister.fct.serverconfig.presentation.components.ImportConfigDialogComponent
import org.smartregister.fct.serverconfig.presentation.components.ServerConfigPanelComponent

@Composable
internal fun ServerConfigPanelComponent.ImportConfigsDialog() {

    val state by importDialogState.subscribeAsState()

    val fileProviderDialog = rememberFileProviderDialog(
        componentContext = this,
        title = "Import Server Configs",
        fileType = FileType.Json,
        onFileContent = { _, _, fileContent ->
            (state as ImportDialogState.ImportFileDialog).component.loadConfigs(fileContent)
        }
    )

    val importErrorDialog = rememberAlertDialog(
        title = "Import Error",
        dialogType = DialogType.Error,
    )

    val importConfigDialog = rememberDialog<Unit>(
        title = "Import Configs",
        width = 300.dp,
        height = 500.dp,
        onDismiss = { hideExportConfigDialog() }
    ) { _, _ ->

        if (state is ImportDialogState.SelectConfigsDialog) {
            val selectConfigState = state as ImportDialogState.SelectConfigsDialog
            with(selectConfigState.component) {
                ImportExportContent(selectConfigState.configs) {
                    ExportButton()
                }
            }
        }
    }

    when (val s = state) {
        is ImportDialogState.ImportFileDialog -> {
            fileProviderDialog.show()
        }

        is ImportDialogState.SelectConfigsDialog -> {
            importConfigDialog.show()
        }

        is ImportDialogState.ImportErrorDialog -> {
            importErrorDialog.show(s.error)
        }

        else -> {
            fileProviderDialog.hide()
            importConfigDialog.hide()
            importErrorDialog.hide()
        }
    }
}

context (ImportConfigDialogComponent, ServerConfigPanelComponent)
@Composable
private fun ExportButton() {

    Button(
        modifier = Modifier.fillMaxWidth(),
        label = "Import",
        enable = checkedConfigs.subscribeAsState().value.isNotEmpty(),
        onClick = ::import
    )

}