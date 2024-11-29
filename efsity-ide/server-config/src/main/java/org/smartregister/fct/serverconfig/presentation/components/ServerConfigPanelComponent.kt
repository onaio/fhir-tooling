package org.smartregister.fct.serverconfig.presentation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.domain.model.ServerConfig
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.uuid
import org.smartregister.fct.serverconfig.domain.model.ExportDialogState
import org.smartregister.fct.serverconfig.domain.model.ImportDialogState

internal class ServerConfigPanelComponent(
    private val componentContext: ComponentContext
) : KoinComponent, ComponentContext by componentContext {

    private val appSettingManager: AppSettingManager by inject()
    private var appSetting = appSettingManager.appSetting

    private var _activeTabIndex = MutableValue(0)
    val activeTabIndex: Value<Int> = _activeTabIndex

    private val _tabComponents = MutableValue<List<ServerConfigComponent>>(listOf())
    val tabComponents: Value<List<ServerConfigComponent>> = _tabComponents

    val importDialogState = MutableValue<ImportDialogState>(ImportDialogState.Idle)
    val exportDialogState = MutableValue<ExportDialogState>(ExportDialogState.Idle)

    init {
        loadConfigs()
    }

    fun changeTab(index: Int) {
        _activeTabIndex.value = index
    }

    fun createNewConfig(title: String) {
        val config = ServerConfig(
            id = uuid(),
            title = title
        )
        _tabComponents.value += listOf(
            ServerConfigComponent(
                componentContext = componentContext,
                serverConfig = config
            )
        )
        updateSetting()
    }

    fun closeTab(tabIndex: Int) {
        val updatedConfigComponents = _tabComponents
            .value
            .filterIndexed { index, _ -> index != tabIndex }

        if (activeTabIndex.value > 0 && activeTabIndex.value >= tabIndex) {
            _activeTabIndex.value -= 1
        }

        _tabComponents.value = updatedConfigComponents
        updateSetting()
    }

    fun showImportConfigDialog() {
        importDialogState.value = ImportDialogState.ImportFileDialog(
            ImportConfigDialogComponent(
                serverConfigPanelComponent = this@ServerConfigPanelComponent
            )
        )
    }

    fun hideImportConfigDialog() {
        importDialogState.value = ImportDialogState.Idle
    }

    fun showExportConfigDialog() {
        exportDialogState.value = ExportDialogState.SelectConfigs(
            ExportConfigDialogComponent(
                serverConfigPanelComponent = this@ServerConfigPanelComponent,
            ),
            configs = _tabComponents.value.map { it.serverConfig }
        )
    }

    fun hideExportConfigDialog() {
        exportDialogState.value = ExportDialogState.Idle
    }

    private fun loadConfigs() {
        componentScope.launch {
            appSetting.getServerConfigsAsFlow().collectLatest {
                _tabComponents.value = it
                    .map { serverConfig ->
                        ServerConfigComponent(
                            componentContext = componentContext,
                            serverConfig = serverConfig,
                        )
                    }
            }
        }

    }

    private fun updateSetting() {
        componentScope.launch {
            val configs = _tabComponents.value.map { it.serverConfig }
            appSetting.updateServerConfigs(configs)
            appSettingManager.update()
        }
    }
}