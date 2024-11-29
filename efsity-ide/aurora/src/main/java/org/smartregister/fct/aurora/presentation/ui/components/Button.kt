package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button as Mat3Button
import androidx.compose.material3.OutlinedButton as Mat3OutlinedButton
import androidx.compose.material3.TextButton as Mat3TextButton

val auroraButtonShape = RoundedCornerShape(8.dp)

enum class ButtonSize {
    Small, Regular
}

@Composable
fun Button(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector? = null,
    enable: Boolean = true,
    shape: Shape = auroraButtonShape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: () -> Unit
) {
    Mat3Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enable,
        colors = colors,
        shape = shape,
    ) {
        icon?.let {
            Icon(
                icon, contentDescription = null,
            )

            Spacer(Modifier.width(8.dp))
        }

        Text(
            text = label,
        )
    }
}

@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    label: String,
    icon: ImageVector? = null,
    labelColor: Color? = null,
    enable: Boolean = true,
    textAlign: TextAlign? = null,
    selected: Boolean = false,
    buttonSize: ButtonSize = ButtonSize.Regular,
    shape: Shape = auroraButtonShape,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    selectedContainerColor: Color = MaterialTheme.colorScheme.surface,
    onClick: () -> Unit
) {

    val content: @Composable (RowScope.() -> Unit) = {

        val alpha = if(enable) 1f else 0.5f

        val textStyle = when(buttonSize) {
            ButtonSize.Small -> MaterialTheme.typography.bodySmall
            ButtonSize.Regular -> MaterialTheme.typography.titleSmall
        }

        icon?.let {
            Icon(
                modifier = iconModifier,
                icon = icon,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
            )

            Spacer(Modifier.width(8.dp))
        }

        if (textAlign == TextAlign.Start) {

            MiddleEllipsisText(
                modifier = Modifier.weight(1f),
                text = label,
                color = labelColor ?: MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                textAlign = textAlign,
                style = textStyle,
            )
        } else {
            MiddleEllipsisText(
                text = label,
                color = labelColor ?: MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                style = textStyle,
            )
        }
    }

    if (selected) {
        Mat3TextButton(
            modifier = modifier,
            onClick = onClick,
            enabled = enable,
            shape = shape,
            contentPadding = contentPadding,
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedContainerColor
            ),
            content = content
        )
    } else {
        Mat3TextButton(
            modifier = modifier,
            onClick = onClick,
            enabled = enable,
            shape = shape,
            contentPadding = contentPadding,
            content = content
        )
    }

}

@Composable
fun OutlinedButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector? = null,
    enable: Boolean = true,
    shape: Shape = auroraButtonShape,
    style: TextStyle = LocalTextStyle.current,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit
) {
    Mat3OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enable,
        shape = shape,
        contentPadding = contentPadding,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = if (enable) SolidColor(MaterialTheme.colorScheme.primary) else SolidColor(MaterialTheme.colorScheme.surface)
        )
    ) {
        icon?.let {
            Icon(
                icon, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.width(8.dp))
        }

        Text(
            text = label,
            color = if (enable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

