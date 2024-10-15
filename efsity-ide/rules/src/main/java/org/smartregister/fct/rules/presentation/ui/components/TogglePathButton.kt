package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Visibility
import org.smartregister.fct.aurora.auroraiconpack.VisibilityOff
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent

@Composable
internal fun TogglePathButton(
    component: RulesScreenComponent
) {

    val showConnection by component.showConnection.collectAsState()
    CircleButton(
        icon = if (showConnection) AuroraIconPack.Visibility else AuroraIconPack.VisibilityOff,
        tooltip = "${if (showConnection) "Hide" else "Show"} Connections",
        tooltipPosition = TooltipPosition.Top(),
        enable = component.workspace.collectAsState().value != null,
        onClick = {
            component.toggleConnection()
        }
    )
}

