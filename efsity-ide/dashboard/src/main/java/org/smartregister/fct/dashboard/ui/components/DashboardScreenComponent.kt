package org.smartregister.fct.dashboard.ui.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import org.smartregister.fct.common.presentation.component.ScreenComponent
import org.smartregister.fct.common.util.windowTitle
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.insights.presentation.components.InsightsComponent

class DashboardScreenComponent(componentContext: ComponentContext) : ScreenComponent,
    ComponentContext by componentContext
{

    internal val deviceInfoComponent: DeviceInfoComponent
    internal val insightsComponent: InsightsComponent

    init {
        componentScope.launch {
            windowTitle.emit("Dashboard")
        }
        deviceInfoComponent = DeviceInfoComponent(this)
        insightsComponent = InsightsComponent(this)
    }
}