package org.smartregister.fct.settings.presentation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import org.smartregister.fct.settings.domain.model.Setting

internal class SettingsComponent(
    componentContext: ComponentContext,
    activeSetting: Setting
) : ComponentContext by componentContext {

    private val _activeSetting = MutableValue(activeSetting)
    val activeSetting: Value<Setting> = _activeSetting

    fun changeSetting(setting: Setting) {
        _activeSetting.value = setting
    }
}