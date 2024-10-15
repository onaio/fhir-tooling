package org.smartregister.fct.dashboard.ui.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.smartregister.fct.adb.domain.model.DeviceInfo
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.engine.util.componentScope

internal class DeviceInfoComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {


    private val _deviceInfo = MutableStateFlow<DeviceInfo?>(null)
    internal val deviceInfo: StateFlow<DeviceInfo?> = _deviceInfo

    init {
        componentScope.launch {
            DeviceManager.listenActiveDevice().collectLatest {
                _deviceInfo.emit(it?.getDeviceInfo())
            }
        }
    }
}