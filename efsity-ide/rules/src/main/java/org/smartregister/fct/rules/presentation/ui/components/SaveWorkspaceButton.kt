package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Save
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent

@Composable
internal fun SaveWorkspaceButton(
    component: RulesScreenComponent
) {

    val scope = rememberCoroutineScope()
    CircleButton(
        icon = AuroraIconPack.Save,
        tooltip = "Save Workspace",
        tooltipPosition = TooltipPosition.Bottom(),
        enable = component.workspace.collectAsState().value != null,
        onClick = {
            scope.launch {
                component.saveWorkspace()
                component.showInfo("Workspace saved")
            }
        }
    )
}