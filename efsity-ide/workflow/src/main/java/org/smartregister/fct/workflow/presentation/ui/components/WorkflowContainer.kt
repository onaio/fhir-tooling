package org.smartregister.fct.workflow.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.smartregister.fct.common.data.controller.SingleFieldDialogController
import org.smartregister.fct.workflow.presentation.components.BaseWorkflowComponent

@Composable
internal fun WorkflowContainer(
    component: BaseWorkflowComponent,
    newWorkflowDialog: SingleFieldDialogController
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Toolbar(component, newWorkflowDialog)

        Row(Modifier.fillMaxSize()) {

            WorkflowFiles(component)
            WorkflowEditor(component)
            /*HorizontalSplitPane(
                resizeOption = ResizeOption.Flexible(
                    minSizeRatio = 0.1f,
                    maxSizeRatio = 0.9f,
                    sizeRatio = 0.2f,
                ),
                leftContent = {},
                rightContent = {
                    WorkflowEditor(component)
                },
            )*/
        }

    }
}

