package org.smartregister.fct.presentation.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.smartregister.fct.common.presentation.component.RootComponent
import org.smartregister.fct.cql.presentation.component.CQLScreenComponent
import org.smartregister.fct.cql.presentation.ui.screen.CQLScreen
import org.smartregister.fct.dashboard.ui.components.DashboardScreenComponent
import org.smartregister.fct.dashboard.ui.presentation.screen.DashboardScreen
import org.smartregister.fct.device_database.ui.components.DeviceDatabaseScreenComponent
import org.smartregister.fct.device_database.ui.presentation.screen.DeviceDatabaseScreen
import org.smartregister.fct.fhirman.presentation.components.FhirmanScreenComponent
import org.smartregister.fct.fhirman.presentation.ui.screen.FhirmanScreen
import org.smartregister.fct.fm.presentation.components.FileManagerScreenComponent
import org.smartregister.fct.fm.presentation.ui.screen.FileManagerScreen
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent
import org.smartregister.fct.rules.presentation.ui.screen.RulesScreen
import org.smartregister.fct.sm.presentation.component.StructureMapScreenComponent
import org.smartregister.fct.sm.presentation.ui.screen.StructureMapScreen
import org.smartregister.fct.workflow.presentation.components.WorkflowScreenComponent
import org.smartregister.fct.workflow.presentation.ui.screen.WorkflowScreen

context (BoxScope)
@Composable
fun MainRoot(component: RootComponent) {

    val mainSlot by component.slot.subscribeAsState()

    when (val contextComponent = mainSlot.child?.instance) {
        is DashboardScreenComponent -> DashboardScreen(contextComponent)
        is StructureMapScreenComponent -> StructureMapScreen(contextComponent)
        is WorkflowScreenComponent -> WorkflowScreen(contextComponent)
        is CQLScreenComponent -> CQLScreen(contextComponent)
        is FileManagerScreenComponent -> FileManagerScreen(contextComponent)
        is FhirmanScreenComponent -> FhirmanScreen(contextComponent)
        is DeviceDatabaseScreenComponent -> DeviceDatabaseScreen(contextComponent)
        is RulesScreenComponent -> RulesScreen(contextComponent)
    }
}