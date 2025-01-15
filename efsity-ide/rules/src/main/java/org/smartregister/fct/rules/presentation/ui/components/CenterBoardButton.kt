package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.CenterFocusWeak
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent

@Composable
internal fun BoxScope.CenterBoardButton(
    component: RulesScreenComponent
) {

    val offset by component.boardOffset.collectAsState()
    if (offset.x != 0 || offset.y != 0) {
        CircleButton(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 12.dp, bottom = 12.dp),
            icon = AuroraIconPack.CenterFocusWeak,
            tooltip = "Center Board",
            tooltipPosition = TooltipPosition.Right(),
            onClick = {
                component.focusCenter()
            }
        )
    }
}