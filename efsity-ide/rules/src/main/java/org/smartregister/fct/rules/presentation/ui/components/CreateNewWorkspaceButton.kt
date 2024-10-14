package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.runtime.Composable
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Add
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent
import org.smartregister.fct.rules.presentation.ui.dialog.rememberNewWorkspaceDialog

@Composable
internal fun CreateNewWorkspaceButton(
    component: RulesScreenComponent
) {

    val createNewWorkspaceDialog = rememberNewWorkspaceDialog(component) {
        component.createWorkspace(it)
    }

    CircleButton(
        icon = AuroraIconPack.Add,
        tooltip = "New Workspace",
        tooltipPosition = TooltipPosition.Bottom(),
        onClick = {
            createNewWorkspaceDialog.show()
        }
    )
}