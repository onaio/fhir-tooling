package org.smartregister.fct.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.smartregister.fct.common.data.enums.BottomWindowState
import org.smartregister.fct.common.data.manager.SubWindowManager
import org.smartregister.fct.logcat.ui.view.LogcatWindow
import org.smartregister.fct.logger.FCTLogger

@Composable
fun BottomWindow(subWindowManager: SubWindowManager) {

    val windowState by subWindowManager.getBottomWindowState().collectAsState(initial = null)

    if (windowState != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            when (windowState) {
                BottomWindowState.Logcat -> {
                    LogcatWindow(
                        onViewModeSelected = {
                            subWindowManager.changeBottomWindowViewMode(
                                state = BottomWindowState.Logcat,
                                viewMode = it
                            )
                        }
                    )
                }

                else -> FCTLogger.e(IllegalStateException("Unknown State"))
            }
        }
    }
}