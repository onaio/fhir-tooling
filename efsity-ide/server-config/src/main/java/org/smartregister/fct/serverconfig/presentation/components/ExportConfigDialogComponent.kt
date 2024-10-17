package org.smartregister.fct.serverconfig.presentation.components

import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.encodeJson
import org.smartregister.fct.serverconfig.domain.model.ExportDialogState

internal class ExportConfigDialogComponent(
    serverConfigPanelComponent: ServerConfigPanelComponent,
) : KoinComponent, ConfigDialogComponent(serverConfigPanelComponent) {

    fun export() {
        componentScope.launch {
            val configJson = checkedConfigs
                .value
                .map {
                    it.copy(
                        authToken = ""
                    )
                }.encodeJson()

            serverConfigPanelComponent.exportDialogState.value =
                ExportDialogState.ExportFileDialog(
                    configJson = configJson
                )
        }
    }
}