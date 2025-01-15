package org.smartregister.fct.insights.presentation.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.common.util.allResourcesSyncedStatus
import org.smartregister.fct.common.util.appVersion
import org.smartregister.fct.common.util.buildDate
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.decodeJson
import org.smartregister.fct.insights.domain.model.Insights
import org.smartregister.fct.insights.util.InsightsConfig
import org.smartregister.fct.logger.FCTLogger


class InsightsComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val _loading = MutableStateFlow(false)
    internal val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    internal val error: StateFlow<String?> = _error

    private val _insights = MutableStateFlow<Insights?>(null)
    internal val insights: StateFlow<Insights?> = _insights


    init {
        componentScope.launch {
            updateInsights(InsightsConfig.activeInsights)

            DeviceManager.listenActiveDevice().collectLatest {

                it?.let {
                    if (it.getDeviceInfo().id != InsightsConfig.activeDeviceId) {
                        InsightsConfig.activeDeviceId = it.getDeviceInfo().id
                        fetchInsights(it, false)
                    }
                } ?: updateInsights(null)
            }
        }
    }

    internal fun setError(error: String?) {
        componentScope.launch {
            _error.emit(error)
        }
    }

    internal fun fetchInsights(device: Device, force: Boolean) {
        componentScope.launch {
            if (!loading.value) {

                _loading.emit(true)
                val result = device.getInsights("_")
                _loading.emit(false)

                if (result.isSuccess) {
                    val response = result.getOrThrow().toString().decodeJson<Response>()
                    if (response.error == null) {
                        updateInsights(response.result)
                        InsightsConfig.activeInsights = response.result
                    } else {
                        updateInsights(null)
                        InsightsConfig.activeInsights = null
                        FCTLogger.e(response.error)
                        setError(response.error)
                    }
                } else {
                    updateInsights(null)
                    InsightsConfig.activeInsights = null
                    FCTLogger.e(result.exceptionOrNull())
                    setError(result.exceptionOrNull()?.message)
                }
            }
        }
    }

    @Serializable
    private data class Response(
        var error: String?,
        val result: Insights? = null,
    )

    private fun updateInsights(insights: Insights?) {
        componentScope.launch {
            allResourcesSyncedStatus.emit(insights?.unSyncedResources)
            appVersion.emit(insights?.appVersion)
            buildDate.emit(insights?.buildDate)
            _insights.emit(insights)
        }
    }
}