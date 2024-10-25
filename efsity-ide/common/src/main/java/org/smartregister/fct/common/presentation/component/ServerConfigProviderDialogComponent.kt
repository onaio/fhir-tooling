package org.smartregister.fct.common.presentation.component

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.domain.model.ServerConfig
import org.smartregister.fct.engine.util.componentScope

internal class ServerConfigProviderDialogComponent(
    componentContext: ComponentContext,
    //prevSelectedConfig: ServerConfig? = null
) : KoinComponent, ComponentContext by componentContext {

    private val appSettingManager: AppSettingManager by inject()
    private val _configs = MutableStateFlow<List<ServerConfig>>(listOf())
    val configs: StateFlow<List<ServerConfig>> = _configs

    /*private val _selectedConfig = MutableStateFlow(prevSelectedConfig)
    val selectedConfig: StateFlow<ServerConfig?> = _selectedConfig*/

    init {
        loadConfigs()
    }

    private fun loadConfigs() {
        componentScope.launch {
            appSettingManager.appSetting.getServerConfigsAsFlow().collectLatest {
                _configs.value = it
            }
        }
    }

    /*fun selectConfig(config: ServerConfig) {
        componentScope.launch {
            _selectedConfig.emit(config)
        }
    }*/
}