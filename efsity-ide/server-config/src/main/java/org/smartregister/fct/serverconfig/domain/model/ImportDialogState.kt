package org.smartregister.fct.serverconfig.domain.model

import org.smartregister.fct.engine.domain.model.ServerConfig
import org.smartregister.fct.serverconfig.presentation.components.ImportConfigDialogComponent

internal sealed class ImportDialogState {
    data object Idle : ImportDialogState()

    data class ImportFileDialog(
        val component: ImportConfigDialogComponent
    ) : ImportDialogState()

    data class SelectConfigsDialog(
        val component: ImportConfigDialogComponent,
        val configs: List<ServerConfig>
    ) : ImportDialogState()

    data class ImportErrorDialog(
        val error: String
    ) : ImportDialogState()
}