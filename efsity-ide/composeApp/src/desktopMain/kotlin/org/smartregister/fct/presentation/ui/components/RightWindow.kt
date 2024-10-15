package org.smartregister.fct.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.smartregister.fct.common.data.enums.RightWindowState
import org.smartregister.fct.common.data.manager.SubWindowManager
import org.smartregister.fct.device.ui.DeviceManagerWindow
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.pm.ui.view.PackageManagerWindow

@Composable
fun RightWindow(subWindowManager: SubWindowManager) {

    val windowState by subWindowManager.getRightWindowState().collectAsState(initial = null)
    if (windowState != null) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (windowState) {
                RightWindowState.DeviceManager -> DeviceManagerWindow(subWindowManager)
                RightWindowState.PackageManager -> PackageManagerWindow(subWindowManager)
                else -> FCTLogger.e(IllegalStateException("Unknown State"))
            }
        }
    }

}