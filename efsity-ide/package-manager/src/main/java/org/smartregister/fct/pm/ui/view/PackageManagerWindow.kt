package org.smartregister.fct.pm.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.smartregister.fct.common.data.enums.RightWindowState
import org.smartregister.fct.common.data.manager.SubWindowManager
import org.smartregister.fct.common.presentation.ui.components.RightWindowHeader
import org.smartregister.fct.pm.ui.components.DeviceSelectionMenu

@Composable
fun PackageManagerWindow(subWindowManager: SubWindowManager) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        RightWindowHeader(
            title = "Package Manager",
            onViewModeSelected = {
                subWindowManager.changeRightWindowViewMode(
                    state = RightWindowState.PackageManager,
                    viewMode = it
                )
            }
        )
        HorizontalDivider()
        DeviceSelectionMenu()
    }
}


