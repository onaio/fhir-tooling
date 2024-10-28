package org.smartregister.fct.logcat.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.smartregister.fct.logger.model.LogLevel

@Composable
internal fun LogContainer(logLevelFilter: State<LogLevel?>) {

    val wrapText = remember { mutableStateOf(true) }
    val stickScrollToBottom = remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        LogConfiguration(wrapText, stickScrollToBottom)
        VerticalDivider()
        LogWindow(wrapText, stickScrollToBottom, logLevelFilter)
    }
}