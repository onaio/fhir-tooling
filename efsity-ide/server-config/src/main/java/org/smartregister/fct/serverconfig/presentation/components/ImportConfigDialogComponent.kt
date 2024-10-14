package org.smartregister.fct.serverconfig.presentation.components

import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.domain.model.ServerConfig
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.decodeJson
import org.smartregister.fct.engine.util.uuid
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.serverconfig.domain.model.ImportDialogState

internal class ImportConfigDialogComponent(
    serverConfigPanelComponent: ServerConfigPanelComponent
) : KoinComponent, ConfigDialogComponent(serverConfigPanelComponent) {

    private val appSettingManager: AppSettingManager by inject()
    private var appSetting = appSettingManager.appSetting

    fun loadConfigs(configJson: String) {
        componentScope.launch {
            try {
                serverConfigPanelComponent.importDialogState.value =
                    ImportDialogState.SelectConfigsDialog(
                        component = this@ImportConfigDialogComponent,
                        configs = configJson.decodeJson<List<ServerConfig>>()
                    )
            } catch (ex: Exception) {
                FCTLogger.e(ex)
                serverConfigPanelComponent.importDialogState.value =
                    ImportDialogState.ImportErrorDialog(
                        error = ex.message ?: "JSON Parse error"
                    )
            }
        }
    }

    fun import() {
        componentScope.launch {

            val mergedConfigs: List<ServerConfig> =
                appSetting.serverConfigs + checkedConfigs.value.map {
                    it.copy(
                        id = uuid()
                    )
                }

            appSetting.updateServerConfigs(mergedConfigs)
            appSettingManager.update()
            serverConfigPanelComponent.hideImportConfigDialog()
        }
    }
}