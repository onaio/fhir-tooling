package org.smartregister.fct.logcat.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.smartregister.fct.common.domain.model.ViewMode
import org.smartregister.fct.logcat.ui.components.LogContainer
import org.smartregister.fct.logcat.ui.components.TopBar
import org.smartregister.fct.logger.model.LogLevel

@Composable
fun LogcatWindow(
    modifier: Modifier = Modifier,
    onViewModeSelected: (ViewMode) -> Unit,
) {

    val logLevelFilter = remember { mutableStateOf<LogLevel?>(null) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopBar(
            onViewModeSelected = onViewModeSelected,
            logLevelFilter = logLevelFilter
        )
        HorizontalDivider()
        LogContainer(logLevelFilter)
    }
}








