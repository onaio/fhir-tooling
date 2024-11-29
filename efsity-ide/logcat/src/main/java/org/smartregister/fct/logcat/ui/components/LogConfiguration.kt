package org.smartregister.fct.logcat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Delete
import org.smartregister.fct.aurora.auroraiconpack.Pause
import org.smartregister.fct.aurora.auroraiconpack.VerticalAlignBottom
import org.smartregister.fct.aurora.auroraiconpack.WrapText
import org.smartregister.fct.logger.FCTLogger

@Composable
internal fun LogConfiguration(
    wrapText: MutableState<Boolean>,
    stickScrollToBottom: MutableState<Boolean>,
) {

    val isPause by FCTLogger.getPause().collectAsState(initial = false)

    Column(
        modifier = Modifier.fillMaxHeight().background(MaterialTheme.colorScheme.surfaceContainer).width(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(6.dp))
        LogcatSmallIconButton(
            icon = AuroraIconPack.Delete,
            onClick = { FCTLogger.clearLogs() }
        )
        Spacer(Modifier.height(3.dp))
        LogcatSmallIconButton(
            icon = if(isPause) Icons.Rounded.PlayArrow else AuroraIconPack.Pause,
            onClick = { FCTLogger.togglePause() }
        )
        Spacer(Modifier.height(3.dp))
        LogcatSmallIconButton(
            icon =AuroraIconPack.WrapText,
            selected = wrapText.value,
            onClick = {
                wrapText.value = !wrapText.value
            }
        )
        Spacer(Modifier.height(3.dp))
        LogcatSmallIconButton(
            icon = AuroraIconPack.VerticalAlignBottom,
            selected = stickScrollToBottom.value,
            onClick = {
                stickScrollToBottom.value = !stickScrollToBottom.value
            }
        )
    }
}