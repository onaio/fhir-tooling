package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PanelHeading(text: String, rightIcon: ImageVector? = null, onRightIconClick: (() -> Unit)? = null) {
    Box(
        Modifier.fillMaxWidth().height(40.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            style = MaterialTheme.typography.titleSmall
        )

        if (rightIcon != null) {
            if (onRightIconClick != null) {
                Row(Modifier.align(Alignment.CenterEnd)) {
                    SmallIconButton(
                        mainModifier = Modifier
                            .size(30.dp),
                        iconModifier = Modifier.size(20.dp),
                        icon = rightIcon,
                        onClick = onRightIconClick
                    )
                    Spacer(Modifier.width(8.dp))
                }
            } else {
                Icon(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp),
                    icon = rightIcon
                )
            }
        }

        VerticalDivider(modifier = Modifier.align(Alignment.CenterEnd))
        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}