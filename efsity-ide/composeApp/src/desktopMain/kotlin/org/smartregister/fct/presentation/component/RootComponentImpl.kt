package org.smartregister.fct.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.statekeeper.ExperimentalStateKeeperApi
import com.arkivanov.essenty.statekeeper.saveable
import kotlinx.serialization.Serializable
import org.smartregister.fct.common.domain.model.Config
import org.smartregister.fct.common.presentation.component.RootComponent
import org.smartregister.fct.common.presentation.component.ScreenComponent
import org.smartregister.fct.cql.presentation.component.CQLScreenComponent
import org.smartregister.fct.dashboard.ui.components.DashboardScreenComponent
import org.smartregister.fct.device_database.ui.components.DeviceDatabaseScreenComponent
import org.smartregister.fct.fhirman.presentation.components.FhirmanScreenComponent
import org.smartregister.fct.fm.presentation.components.FileManagerScreenComponent
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent
import org.smartregister.fct.sm.presentation.component.StructureMapScreenComponent
import org.smartregister.fct.workflow.presentation.components.WorkflowScreenComponent

@OptIn(ExperimentalStateKeeperApi::class)
class RootComponentImpl(componentContext: ComponentContext) :
    RootComponent(componentContext) {

    private val navigation = SlotNavigation<Config>()
    private var state: State by saveable(serializer = State.serializer(), init = ::State)

    override val slot: Value<ChildSlot<*, ScreenComponent>> = childSlot(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = {
            Config.Dashboard
        },
        key = "MainRoot"
    ) { config, childComponentContext ->

        state.activeComponent?.onDestroy()

        val activeComponent = when (config) {
            is Config.Dashboard -> DashboardScreenComponent(childComponentContext)
            is Config.StructureMap -> StructureMapScreenComponent(childComponentContext)
            is Config.Workflow -> WorkflowScreenComponent(childComponentContext)
            is Config.CQL -> CQLScreenComponent(childComponentContext)
            is Config.FileManager -> FileManagerScreenComponent(childComponentContext)
            is Config.Fhirman -> FhirmanScreenComponent(childComponentContext)
            is Config.DeviceDatabase -> DeviceDatabaseScreenComponent(childComponentContext)
            is Config.Rules -> RulesScreenComponent(childComponentContext)
        }

        state.activeComponent = activeComponent
        state.activeComponent!!
    }

    override fun changeSlot(item: Config) {
        navigation.activate(item)
    }

    @Serializable
    private class State {
        var activeComponent: ScreenComponent? = null
    }
}