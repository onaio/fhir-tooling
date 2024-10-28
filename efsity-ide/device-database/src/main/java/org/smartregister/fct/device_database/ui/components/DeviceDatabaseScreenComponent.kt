package org.smartregister.fct.device_database.ui.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import org.smartregister.fct.common.presentation.component.ScreenComponent
import org.smartregister.fct.common.util.windowTitle
import org.smartregister.fct.engine.util.componentScope

class DeviceDatabaseScreenComponent(componentContext: ComponentContext) : ScreenComponent,
    ComponentContext by componentContext {

    init {
        componentScope.launch {
            windowTitle.emit("Database")
        }
    }
}