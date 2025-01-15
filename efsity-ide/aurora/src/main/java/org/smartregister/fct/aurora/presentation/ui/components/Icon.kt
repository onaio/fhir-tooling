package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.Image as MatImage
import androidx.compose.material3.Icon as Mat3Icon

@Composable
fun Icon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    tint: Color = LocalContentColor.current,
) {
    Mat3Icon(
        modifier = modifier,
        imageVector = icon,
        tint = tint,
        contentDescription = null
    )
}

@Composable
fun Icon(
    modifier: Modifier = Modifier,
    icon: DrawableResource,
    tint: Color? = null,
) {
    MatImage(
        modifier = modifier,
        painter = painterResource(icon),
        contentDescription = null,
        colorFilter = if (tint != null) ColorFilter.tint(color = tint) else null
    )
}