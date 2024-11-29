package org.smartregister.fct.workflow.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cyclone
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.TextButton
import org.smartregister.fct.common.data.controller.SingleFieldDialogController
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.workflow.data.enums.WorkflowType
import org.smartregister.fct.workflow.presentation.components.WorkflowScreenComponent

@Composable
internal fun BlankWorkspace(
    component: WorkflowScreenComponent,
    newWorkflowDialog: SingleFieldDialogController
) {

    Aurora(
        modifier = Modifier.fillMaxSize(),
        componentContext = component
    ) {

        Box(Modifier.fillMaxSize().wrapContentSize(unbounded = true)) {
            Icon(
                modifier = Modifier.size(1000.dp).align(Alignment.Center),
                icon = Icons.Rounded.Cyclone,
                tint = LocalContentColor.current.copy(0.01f)
            )
        }
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                label = "Create \$lite",
                onClick = {
                    newWorkflowDialog.show(WorkflowType.Lite)
                }
            )
            Spacer(Modifier.width(8.dp))
            Text("..::..")
            Spacer(Modifier.width(8.dp))
            TextButton(
                label = "Create \$apply",
                onClick = {
                    newWorkflowDialog.show(WorkflowType.Apply)
                }
            )
            Spacer(Modifier.width(8.dp))
            Text("..::..")
            Spacer(Modifier.width(8.dp))
            TextButton(
                label = "Open",
                onClick = {
                    component.toggleAllWorkflowPanel()
                }
            )
        }
    }
}