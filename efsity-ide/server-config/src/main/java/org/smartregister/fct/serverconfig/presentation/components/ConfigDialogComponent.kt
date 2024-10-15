package org.smartregister.fct.serverconfig.presentation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import org.smartregister.fct.engine.domain.model.ServerConfig

internal abstract class ConfigDialogComponent(
    protected val serverConfigPanelComponent: ServerConfigPanelComponent
) : ComponentContext by serverConfigPanelComponent {

    private val _checkedConfigs = MutableValue<List<ServerConfig>>(listOf())
    val checkedConfigs: Value<List<ServerConfig>> = _checkedConfigs

    fun addOrRemoveConfig(checked: Boolean, config: ServerConfig) {
        if (checked) {
            _checkedConfigs.value += listOf(config)
        } else {
            _checkedConfigs.value = _checkedConfigs
                .value
                .filter { it.id != config.id }
        }
    }
}