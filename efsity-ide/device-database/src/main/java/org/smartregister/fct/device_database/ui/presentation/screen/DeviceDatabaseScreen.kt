package org.smartregister.fct.device_database.ui.presentation.screen

import androidx.compose.runtime.Composable
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.device_database.ui.components.DeviceDatabaseScreenComponent
import org.smartregister.fct.device_database.ui.presentation.components.AppDatabasePanel

@Composable
fun DeviceDatabaseScreen(component: DeviceDatabaseScreenComponent) {
    Aurora(component) {
        with (it) {
            AppDatabasePanel(component)
        }
    }
}