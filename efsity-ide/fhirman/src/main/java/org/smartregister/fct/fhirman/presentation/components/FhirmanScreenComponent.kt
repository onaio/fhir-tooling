package org.smartregister.fct.fhirman.presentation.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.apiclient.domain.model.Request
import org.smartregister.fct.apiclient.domain.model.Response
import org.smartregister.fct.apiclient.domain.usecase.ApiRequest
import org.smartregister.fct.common.presentation.component.ScreenComponent
import org.smartregister.fct.common.util.windowTitle
import org.smartregister.fct.editor.presentation.components.CodeEditorComponent
import org.smartregister.fct.engine.data.enums.FileType
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.domain.model.HttpMethodType
import org.smartregister.fct.engine.domain.model.ServerConfig
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.encodeResourceToString
import org.smartregister.fct.fhirman.util.FhirmanConfig
import org.smartregister.fct.logger.FCTLogger

class FhirmanScreenComponent(
    componentContext: ComponentContext
) : ScreenComponent, KoinComponent, ComponentContext by componentContext {

    private val appSettingManager: AppSettingManager by inject()
    private val apiRequest: ApiRequest by inject()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _info = MutableStateFlow<String?>(null)
    internal val info: StateFlow<String?> = _info

    private val _methodType = MutableStateFlow<HttpMethodType>(HttpMethodType.Get)
    internal val methodType: StateFlow<HttpMethodType> = _methodType

    private val _resourceType = MutableStateFlow("")
    internal val resourceType: StateFlow<String> = _resourceType

    private val _resourceId = MutableStateFlow("")
    internal val resourceId: StateFlow<String> = _resourceId

    private val _selectedConfig = MutableStateFlow<ServerConfig?>(null)
    val selectedConfig: StateFlow<ServerConfig?> = _selectedConfig

    private val _responseStatus = MutableStateFlow("")
    internal val responseStatus: StateFlow<String> = _responseStatus

    internal val requestCodeEditorComponent: CodeEditorComponent
    internal val responseCodeEditorComponent: CodeEditorComponent

    init {
        componentScope.launch {
            windowTitle.emit("Fhirman")
        }

        componentScope.launch {
            _selectedConfig.emit(FhirmanConfig.selectedConfig)
            _methodType.emit(FhirmanConfig.methodType)
            _resourceType.emit(FhirmanConfig.resourceType)
            _resourceId.emit(FhirmanConfig.resourceId)
            _responseStatus.emit(FhirmanConfig.responseStatus)
        }

        requestCodeEditorComponent = CodeEditorComponent(
            componentContext = this,
            fileType = FileType.Json
        )
        componentScope.launch(Dispatchers.IO) {
            requestCodeEditorComponent.setText(FhirmanConfig.requestText)
        }

        responseCodeEditorComponent = CodeEditorComponent(
            componentContext = this,
            fileType = FileType.Json,
            readOnly = true
        )
        componentScope.launch(Dispatchers.IO) {
            responseCodeEditorComponent.setText(FhirmanConfig.responseText)
        }

        listenConfigs()
    }

    private fun listenConfigs() {
        componentScope.launch {
            appSettingManager.appSetting.getServerConfigsAsFlow().collectLatest {
                if (_selectedConfig.value != null && _selectedConfig.value !in it) {
                    _selectedConfig.emit(null)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        with(FhirmanConfig) {
            requestText = requestCodeEditorComponent.getText()
            responseText = responseCodeEditorComponent.getText()
            selectedConfig = _selectedConfig.value
            methodType = _methodType.value
            resourceType = _resourceType.value
            resourceId = _resourceId.value
            responseStatus = _responseStatus.value
        }
    }

    fun selectConfig(config: ServerConfig) {
        componentScope.launch {
            _selectedConfig.emit(config)
        }
    }

    fun selectMethodType(httpMethodType: HttpMethodType) {
        componentScope.launch {
            _methodType.emit(httpMethodType)
        }
    }

    fun selectResourceType(resourceType: String) {
        componentScope.launch {
            _resourceType.emit(resourceType)
        }
    }

    fun setResourceId(resourceId: String) {
        componentScope.launch {
            _resourceId.emit(resourceId)
        }
    }

    fun send() {
        componentScope.launch {
            buildRequest()?.let {
                _loading.emit(true)

                try {
                    when (val response = apiRequest(it)) {
                        is Response.Success -> {
                            _loading.emit(false)

                            _responseStatus.emit("Status: ${response.httpStatusCode} ${response.httpStatus}")

                            if (_methodType.value is HttpMethodType.Delete) {
                                showInfo("${_resourceType.value}/${_resourceId.value} successfully deleted")
                            }
                            responseCodeEditorComponent.setText(response.response)
                            responseCodeEditorComponent.formatJson()
                        }

                        is Response.Failed -> {
                            _loading.emit(false)
                            _responseStatus.emit("Status: ${response.httpStatusCode} ${response.httpStatus}")
                            val error = response.outcome.issue.firstOrNull()?.diagnostics
                            responseCodeEditorComponent.setText(response.outcome.encodeResourceToString())
                            FCTLogger.e(error)
                            showError(error)
                        }
                    }
                } catch (ex: Exception) {
                    FCTLogger.e(ex)
                    showError(ex.message)
                }

            }
        }
    }

    private fun buildRequest(): Request? {
        if (_selectedConfig.value == null) {
            showError("Server config is not selected.")
            return null
        }

        if (_resourceType.value.trim().isEmpty()) {
            showError("Resource type could not be empty.")
            return null
        }

        if (_resourceId.value.trim().isEmpty()) {
            showError("Resource id could not be empty.")
            return null
        }

        return Request(
            config = _selectedConfig.value!!,
            methodType = _methodType.value,
            resourceType = _resourceType.value,
            resourceId = _resourceId.value,
            body = requestCodeEditorComponent.getText()
        )
    }

    internal fun showInfo(text: String?) {
        componentScope.launch {
            _info.emit(text)
        }
    }

    internal fun showError(text: String?) {
        componentScope.launch {
            _error.emit(text)
        }
    }

    internal fun reset() {
        componentScope.launch {
            FhirmanConfig.reset()
            requestCodeEditorComponent.setText("")
            responseCodeEditorComponent.setText("")
            _selectedConfig.emit(null)
            _methodType.emit(HttpMethodType.Get)
            _resourceType.emit("")
            _resourceId.emit("")
            _responseStatus.emit("")
        }
    }
}