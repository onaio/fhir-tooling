package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SmallIconButton(
    mainModifier: Modifier = Modifier.size(30.dp),
    iconModifier: Modifier = Modifier.size(20.dp),
    icon: ImageVector,
    enable: Boolean = true,
    selected: Boolean = false,
    tint: Color? = null,
    alpha: Float = 1f,
    tooltip: String? = null,
    tooltipPosition: TooltipPosition = TooltipPosition.Bottom(),
    delayMillis: Int = 500,
    onClick: () -> Unit
) {

    var colors = IconButtonDefaults.iconButtonColors()

    if (selected) {
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = colorScheme.primary
        )
    }


    val composable: @Composable () -> Unit = {
        if (enable) {
            IconButton(
                modifier = mainModifier,
                onClick = onClick,
                colors = colors
            ) {
                Icon(
                    modifier = iconModifier,
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) colorScheme.onPrimary else tint?.copy(alpha = alpha)
                        ?: LocalContentColor.current.copy(alpha = alpha)
                )
            }
        } else {
            Box(
                modifier = mainModifier
                    .minimumInteractiveComponentSize()
                    .clip(CircleShape)
                    .background(colorScheme.background.copy(0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = iconModifier,
                    imageVector = icon,
                    contentDescription = null,
                    tint = LocalContentColor.current.copy(alpha = 0.3f)
                )
            }
        }

    }

    if (tooltip == null) {
        composable()
    } else {
        Tooltip(
            tooltip = tooltip,
            tooltipPosition = tooltipPosition,
            delayMillis = delayMillis,
        ) {
            composable()
        }
    }


}
