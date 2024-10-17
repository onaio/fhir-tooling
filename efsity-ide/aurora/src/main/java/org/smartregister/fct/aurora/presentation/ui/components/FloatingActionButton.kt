package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.ExtendedFloatingActionButton as Mat3ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton as Mat3FloatingActionButton
import androidx.compose.material3.SmallFloatingActionButton as Mat3SmallFloatingActionButton

@Composable
fun SmallFloatingActionIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onClick: () -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    containerColor: Color = MaterialTheme.colorScheme.primary
) {
    Mat3SmallFloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(icon, null)
    }
}

@Composable
fun FloatingActionIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.shape,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Mat3FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        contentColor = contentColor,
        containerColor = containerColor,
        elevation = elevation,
        interactionSource = interactionSource,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
    }
}

@Composable
fun ExtendedFloatingActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    shape: Shape = FloatingActionButtonDefaults.extendedFabShape,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Mat3ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        text = {
            Text(label)
        },
        expanded = expanded,
        shape = shape,
        contentColor = contentColor,
        containerColor = containerColor,
        elevation = elevation,
        interactionSource = interactionSource
    )
}