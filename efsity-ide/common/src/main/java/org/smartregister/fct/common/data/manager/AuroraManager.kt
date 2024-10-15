package org.smartregister.fct.common.data.manager

import org.smartregister.fct.common.domain.model.Message
import org.smartregister.fct.engine.domain.model.ServerConfig


interface AuroraManager {

    fun showSnackbar(text: String?, onDismiss: (() -> Unit)? = null)

    fun showErrorSnackbar(text: String?, onDismiss: (() -> Unit)? = null)

    fun showSnackbar(message: Message, onDismiss: (() -> Unit)? = null)

    fun showLoader()
    fun hideLoader()

    fun selectServerConfig(initialConfig: ServerConfig?, onSelected: (ServerConfig) -> Unit)
}