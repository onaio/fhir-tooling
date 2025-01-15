package org.smartregister.fct.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.common.util.allResourcesSyncedStatus
import org.smartregister.fct.common.util.appVersion
import org.smartregister.fct.common.util.buildDate

@Composable
fun StatusBar() {
    Column(
        modifier = Modifier.fillMaxWidth().background(colorScheme.surfaceContainer)
    ) {
        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AllResourcesSyncedStatus()
                AppVersion()
                BuildDate()
            }

            Text(
                text = "v1.0.0",
                style = typography.bodySmall
            )
        }
    }
}

@Composable
private fun AllResourcesSyncedStatus() {
    allResourcesSyncedStatus.collectAsState().value?.let { list ->
        val bgColor = if (list.isEmpty()) Color(0xff42e042) else Color(0xffff6e54)
        Box(Modifier.size(8.dp).clip(CircleShape).background(bgColor))
        Spacer(Modifier.width(8.dp))
        Tooltip(
            tooltip = list.joinToString("\n") { "${it.first} = ${it.second}" },
            tooltipPosition = TooltipPosition.Top()
        ) {
            Text(
                text = if (list.isEmpty()) "All Resources Synced" else "Unsynced Resources",
                style = typography.bodySmall
            )
        }
    }
}

@Composable
private fun AppVersion() {
    Spacer(Modifier.width(20.dp))
    appVersion.collectAsState().value?.let {
        Text(
            text = "App Version: $it",
            style = typography.bodySmall
        )
    }
}

@Composable
private fun BuildDate() {
    Spacer(Modifier.width(20.dp))
    buildDate.collectAsState().value?.let {
        Text(
            text = "Build Date: $it",
            style = typography.bodySmall
        )
    }
}