package org.smartregister.fct.serverconfig.presentation.ui.panel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.smartregister.fct.aurora.presentation.ui.components.CloseableTab
import org.smartregister.fct.aurora.presentation.ui.components.ScrollableTabRow
import org.smartregister.fct.common.presentation.ui.dialog.rememberConfirmationDialog
import org.smartregister.fct.common.presentation.ui.dialog.rememberSingleFieldDialog
import org.smartregister.fct.common.util.fileNameValidation
import org.smartregister.fct.serverconfig.presentation.components.ServerConfigPanelComponent
import org.smartregister.fct.serverconfig.presentation.ui.components.CreateOrImportConfig
import org.smartregister.fct.serverconfig.presentation.ui.components.ExportConfigsDialog
import org.smartregister.fct.serverconfig.presentation.ui.components.ImportConfigsDialog
import org.smartregister.fct.serverconfig.presentation.ui.components.ImportExportContent
import org.smartregister.fct.serverconfig.presentation.ui.components.MultiItemFloatingActionButton

@Composable
fun ServerConfigPanel(componentContext: ComponentContext) {

    val component = remember {
        ServerConfigPanelComponent(componentContext)
    }

    with (component) {
        val activeTabIndex by activeTabIndex.subscribeAsState()
        val serverConfigList by tabComponents.subscribeAsState()
        val deleteConfigDialog = deleteConfigDialog()
        val titleDialogController = titleDialogController()

        Column {

            if (serverConfigList.isNotEmpty()) {

                ScrollableTabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = activeTabIndex,
                ) {
                    serverConfigList
                        .map { it.serverConfig }
                        .forEachIndexed { index, item ->
                            CloseableTab(
                                index = index,
                                item = item,
                                title = { it.title },
                                selected = index == activeTabIndex,
                                onClick = {
                                    changeTab(it)
                                },
                                onClose = {
                                    deleteConfigDialog.show(
                                        title = "Delete Config",
                                        message = "Are you sure you want to delete ${item.title} config?",
                                        data = index
                                    )
                                }
                            )
                        }
                }

                Box(Modifier.fillMaxSize()) {
                    with(serverConfigList[activeTabIndex]) {
                        ImportExportContent()
                    }
                    MultiItemFloatingActionButton(titleDialogController)
                }
            } else {
                CreateOrImportConfig(
                    titleDialogController = titleDialogController
                )
            }
        }

        ExportConfigsDialog()
        ImportConfigsDialog()
    }

}

context (ServerConfigPanelComponent)
@Composable
private fun titleDialogController() = rememberSingleFieldDialog(
    title = "Config Title",
    maxLength = 30,
    validations = listOf(fileNameValidation)
) { title, _ ->
    createNewConfig(title)
}

context (ServerConfigPanelComponent)
@Composable
private fun deleteConfigDialog() = rememberConfirmationDialog<Int> { _, tabIndex ->
    tabIndex?.let {
        closeTab(it)
    }
}