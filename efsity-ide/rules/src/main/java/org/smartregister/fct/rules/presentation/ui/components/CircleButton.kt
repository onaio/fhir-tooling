package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition

@Composable
internal fun CircleButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    tooltip: String,
    tooltipPosition: TooltipPosition,
    enable: Boolean = true,
    onClick: () -> Unit
) {
    Tooltip(
        modifier = modifier,
        tooltip = tooltip,
        tooltipPosition = tooltipPosition
    ) {
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = if (enable) colorScheme.surface else colorScheme.surfaceContainer
            ),
            border = BorderStroke(
                width = 0.5.dp,
                color = if (enable) colorScheme.onSurface.copy(0.6f) else colorScheme.onSurface.copy(0.4f)
            ),
            elevation = CardDefaults.cardElevation(
                //defaultElevation = 2.dp
            )
        ) {

            val iconModifier = if(enable) Modifier.clickable { onClick() } else Modifier

            Icon(
                modifier = iconModifier
                    .size(36.dp)
                    .padding(8.dp),
                icon = icon,
                tint = if (enable) colorScheme.onSurface else colorScheme.onSurface.copy(0.4f)
            )
        }
    }

}