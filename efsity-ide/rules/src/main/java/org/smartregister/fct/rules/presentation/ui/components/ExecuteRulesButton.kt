package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Send
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent

@Composable
internal fun BoxScope.ExecuteRulesButton(
    component: RulesScreenComponent
) {

    CircleButton(
        modifier = Modifier.align(Alignment.BottomEnd).padding(end = 12.dp, bottom = 12.dp),
        icon = AuroraIconPack.Send,
        tooltip = "Execute Rules",
        tooltipPosition = TooltipPosition.Left(),
        enable = component.workspace.collectAsState().value != null,
        onClick = {
            component.executeRules()
        }
    )
}

