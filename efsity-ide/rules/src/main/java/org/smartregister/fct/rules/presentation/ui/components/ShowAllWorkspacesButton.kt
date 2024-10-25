package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.runtime.Composable
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.FolderOpen
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent

@Composable
internal fun ShowAllWorkspacesButton(
    component: RulesScreenComponent
) {

    CircleButton(
        icon = AuroraIconPack.FolderOpen,
        tooltip = "Show Workspaces",
        tooltipPosition = TooltipPosition.Bottom(),
        onClick = {
            component.toggleAllWorkflowPanel()
        }
    )
}