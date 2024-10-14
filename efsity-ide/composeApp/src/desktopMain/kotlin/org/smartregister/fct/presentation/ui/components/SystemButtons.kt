package org.smartregister.fct.presentation.ui.components

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import fct.composeapp.generated.resources.Res
import fct.composeapp.generated.resources.close
import fct.composeapp.generated.resources.maximize
import fct.composeapp.generated.resources.minimize
import fct.composeapp.generated.resources.restore
import org.jetbrains.compose.resources.painterResource
import java.awt.Toolkit

@Composable
fun WindowsActionButtons(
    window: ComposeWindow,
    onRequestClose: () -> Unit,
    onRequestMinimize: (() -> Unit)?,
    onToggleMaximize: ((WindowPlacement) -> Unit)?,
) {

    var mode by remember { mutableStateOf(window.placement) }
    var size by remember { mutableStateOf(window.size) }
    var pos by remember { mutableStateOf(window.location) }
    val graphicsConfiguration by remember { mutableStateOf(window.graphicsConfiguration) }

    Row(
        // Toolbar is aligned center vertically so I fill that and place it on top
        modifier = Modifier.fillMaxHeight().padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        onRequestMinimize?.let {
            IconButton(
                modifier = Modifier.size(30.dp),
                onClick = onRequestMinimize
            ) {
                Icon(
                    modifier = Modifier.scale(0.6f),
                    painter = painterResource(Res.drawable.minimize),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.width(4.dp))


        IconButton(
            modifier = Modifier.size(30.dp),
            onClick = {
                if (mode == WindowPlacement.Maximized) {
                    mode = WindowPlacement.Floating
                    window.size = size
                    window.location = pos
                    onToggleMaximize?.invoke(mode)
                } else {
                    size = window.size
                    pos = window.location
                    mode = WindowPlacement.Maximized
                    pos = window.location
                    val insets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration)
                    val bounds = graphicsConfiguration.bounds
                    window.setSize(bounds.width, bounds.height - insets.bottom)
                    window.setLocation(0, 0)

                    onToggleMaximize?.invoke(mode)
                }
            }
        ) {

            Icon(
                modifier = Modifier.scale(0.6f),
                painter = painterResource(if (mode == WindowPlacement.Maximized) Res.drawable.restore else Res.drawable.maximize),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }


        Spacer(Modifier.width(4.dp))

        val interactionSource = remember { MutableInteractionSource() }
        val isHover by interactionSource.collectIsHoveredAsState()

        IconButton(
            modifier = Modifier.size(30.dp).hoverable(interactionSource),
            onClick = onRequestClose,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = if (isHover) Color.Red else Color.Unspecified
            )
        ) {
            Icon(
                modifier = Modifier.scale(0.5f),
                painter = painterResource(Res.drawable.close),
                tint = if (isHover) Color.White else MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
        }

    }
}