package org.smartregister.fct.workflow.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.FolderOpen
import org.smartregister.fct.aurora.auroraiconpack.NoteAdd
import org.smartregister.fct.aurora.auroraiconpack.Send
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.common.data.controller.SingleFieldDialogController
import org.smartregister.fct.workflow.data.enums.WorkflowType
import org.smartregister.fct.workflow.presentation.components.BaseWorkflowComponent

@Composable
internal fun Toolbar(
    component: BaseWorkflowComponent,
    newWorkflowDialog: SingleFieldDialogController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(39.dp)
            .background(colorScheme.surface.copy(0.8f))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        CreateNewWorkflowButton(newWorkflowDialog)
        Spacer(Modifier.width(8.dp))
        OpenWorkflowButton(component)
        Spacer(Modifier.width(8.dp))
        ExecuteWorkflowButton(component)
    }
    HorizontalDivider()
}

@Composable
private fun CreateNewWorkflowButton(newWorkflowDialog: SingleFieldDialogController) {
    var expanded by remember { mutableStateOf(false) }
    Tooltip(
        tooltip = "New Workflow",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            icon = AuroraIconPack.NoteAdd,
            onClick = {
                expanded = true
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("\$Lite") },
                onClick = {
                    expanded = false
                    newWorkflowDialog.show(WorkflowType.Lite)
                }
            )
            DropdownMenuItem(
                text = { Text("\$Apply") },
                onClick = {
                    expanded = false
                    newWorkflowDialog.show(WorkflowType.Apply)
                }
            )
        }
    }
}

@Composable
private fun OpenWorkflowButton(component: BaseWorkflowComponent) {
    Tooltip(
        tooltip = "Open Workflow",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            icon = AuroraIconPack.FolderOpen,
            onClick = {
                component.screenComponent.toggleAllWorkflowPanel()
            }
        )
    }
}

@Composable
private fun ExecuteWorkflowButton(component: BaseWorkflowComponent) {
    Tooltip(
        tooltip = "Run",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            icon = AuroraIconPack.Send,
            onClick = {
                component.execute()
            },
        )
    }
}