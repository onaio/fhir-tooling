package org.smartregister.fct.base64.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.automirrored.outlined.Segment
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.OutlinedButton
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.base64.util.compactText
import org.smartregister.fct.base64.util.formatText
import org.smartregister.fct.common.data.manager.AuroraManager

context (AuroraManager)
@Composable
internal fun CompactJsonIconButton(
    textState: MutableState<String>
) {
    Tooltip(
        tooltip = "Compact JSON\nCtrl+Alt+K",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            icon = Icons.AutoMirrored.Outlined.Notes,
            onClick = {
                textState.compactText {
                    showErrorSnackbar(it)
                }
            }
        )
    }
}

context (AuroraManager)
@Composable
internal fun FormatJsonIconButton(
    textState: MutableState<String>,
    tabIndentState: State<Int>
) {
    Tooltip(
        tooltip = "Format JSON\nCtrl+Alt+L",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            icon = Icons.AutoMirrored.Outlined.Segment,
            onClick = {
                textState.formatText(tabIndentState.value) {
                    showErrorSnackbar(it)
                }
            }
        )
    }
}

context (AuroraManager)
@Composable
internal fun CopyAllContentIconButton(
    textState: State<String>
) {
    val clipboardManager = LocalClipboardManager.current

    Tooltip(
        tooltip = "Copy All Content",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            iconModifier = Modifier.height(16.dp),
            icon = Icons.Outlined.ContentCopy,
            onClick = {
                clipboardManager.setText(AnnotatedString(textState.value))
                showSnackbar("Content copied")
            }
        )
    }
}

@Composable
internal fun ActionButton(
    label: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier.height(30.dp),
        label = label,
        contentPadding = PaddingValues(8.dp, 0.dp),
        style = MaterialTheme.typography.bodyMedium,
        onClick = onClick,
    )
}