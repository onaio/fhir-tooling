package org.smartregister.fct.common.data.manager

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.smartregister.fct.common.domain.model.Message
import org.smartregister.fct.engine.domain.model.ServerConfig

internal typealias ServerConfigOption = Pair<ServerConfig?, (ServerConfig) -> Unit>

internal class AuroraManagerImpl(
    componentContext: ComponentContext
) : AuroraManager, ComponentContext by componentContext {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _showSnackbar = MutableStateFlow<Message?>(null)
    val showSnackbar: StateFlow<Message?> = _showSnackbar

    private val _showLoader = MutableStateFlow(false)
    val showLoader: StateFlow<Boolean> = _showLoader

    private val _showServerConfigDialog = MutableStateFlow<ServerConfigOption?>(null)
    val showServerConfigDialog: StateFlow<ServerConfigOption?> = _showServerConfigDialog

    override fun showSnackbar(text: String?, onDismiss: (() -> Unit)?) {
        text?.let {
            showSnackbar(Message.Info(text), onDismiss)
        }
    }

    override fun showErrorSnackbar(text: String?, onDismiss: (() -> Unit)?) {
        text?.let {
            showSnackbar(Message.Error(text), onDismiss)
        }
    }

    override fun showSnackbar(message: Message, onDismiss: (() -> Unit)?) {
        scope.launch {
            if (message.text.trim().isNotEmpty()) {
                _showSnackbar.emit(null)
                _showSnackbar.emit(message)
                delay(3000)
                _showSnackbar.emit(null)
                onDismiss?.invoke()
            }
        }
    }

    override fun showLoader() {
        scope.launch {
            _showLoader.emit(true)
        }
    }

    override fun hideLoader() {
        scope.launch {
            _showLoader.emit(false)
        }
    }

    override fun selectServerConfig(
        initialConfig: ServerConfig?,
        onSelected: (ServerConfig) -> Unit
    ) {
        scope.launch {
            _showServerConfigDialog.emit(
                Pair(initialConfig, onSelected)
            )
        }
    }

    fun resetServerConfig() {
        scope.launch {
            _showServerConfigDialog.emit(null)
        }
    }
}