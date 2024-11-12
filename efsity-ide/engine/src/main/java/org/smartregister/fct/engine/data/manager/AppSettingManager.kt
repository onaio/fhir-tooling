package org.smartregister.fct.engine.data.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.smartregister.fct.engine.domain.model.AppSetting
import org.smartregister.fct.engine.domain.usecase.GetAppSetting
import org.smartregister.fct.engine.domain.usecase.UpdateAppSetting

class AppSettingManager(
    private val getAppSetting: GetAppSetting,
    private val updateAppSetting: UpdateAppSetting
) {

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    val appSetting = AppSetting()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            getAppSettingFlow().collectLatest {
                appSetting.isDarkTheme = it.isDarkTheme
                appSetting.updateServerConfigs(it.serverConfigs)
                appSetting.codeEditorConfig = it.codeEditorConfig
                appSetting.updatePackageInfo(it.packageInfo)
                _isDarkTheme.emit(it.isDarkTheme)
            }
        }
    }

    fun update() {
        updateAppSetting(appSetting)
    }

    private fun getAppSettingFlow(): Flow<AppSetting> {
        return getAppSetting()
    }

}