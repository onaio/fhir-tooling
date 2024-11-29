package org.smartregister.fct.insights.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.aurora.presentation.ui.components.CardWidget
import org.smartregister.fct.aurora.presentation.ui.components.TextButton
import org.smartregister.fct.insights.presentation.components.InsightsComponent

@Composable
internal fun InsightFetchFailed(component: InsightsComponent) {

    CardWidget(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            TextButton(
                modifier = Modifier.align(Alignment.Center),
                label = "Reload",
                onClick = {
                    DeviceManager.getActiveDevice()?.let {
                        component.fetchInsights(it, true)
                    } ?: component.setError("No device found")
                }
            )
        }
    }
}