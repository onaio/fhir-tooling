package org.smartregister.fct.rules.util

import androidx.compose.ui.unit.IntOffset
import org.smartregister.fct.rules.domain.model.Workspace

internal object WorkspaceConfig {
    const val jexlError = "org.jeasy.rules.jexl.JexlAction."
    const val ambiguousError = "ambiguous statement error near"

    var workspace: Workspace? = null
    var defaultBoardScale = 1f
    var defaultBoardOffset = IntOffset.Zero
    var showConnection: Boolean = true
}