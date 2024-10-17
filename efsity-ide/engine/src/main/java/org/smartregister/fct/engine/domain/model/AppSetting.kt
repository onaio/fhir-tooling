package org.smartregister.fct.engine.domain.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

@Serializable
data class AppSetting(
    var isDarkTheme: Boolean = false,
    var codeEditorConfig: CodeEditorConfig = CodeEditorConfig(),

) {

    var packageInfo: PackageInfo? = null
        private set
    var serverConfigs: List<ServerConfig> = listOf()
        private set

    @kotlinx.serialization.Transient
    private val _serverConfigs = MutableStateFlow(serverConfigs)

    @kotlinx.serialization.Transient
    private val _packageInfo = MutableStateFlow(packageInfo)

    fun getServerConfigsAsFlow(): StateFlow<List<ServerConfig>> = _serverConfigs

    suspend fun updateServerConfigs(serverConfigs: List<ServerConfig>) {
        this.serverConfigs = serverConfigs
        _serverConfigs.emit(serverConfigs)
    }

    fun getPackageInfoAsFlow(): StateFlow<PackageInfo?> = _packageInfo

    suspend fun updatePackageInfo(packageInfo: PackageInfo?) {
        this.packageInfo = packageInfo
        _packageInfo.emit(packageInfo)
    }

}
