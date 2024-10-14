package org.smartregister.fct.text_viewer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.automirrored.outlined.Segment
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.common.data.manager.AuroraManager
import org.smartregister.fct.text_viewer.util.compactText
import org.smartregister.fct.text_viewer.util.formatText

context (AuroraManager)
@Composable
internal fun Toolbar(
    textState: MutableState<String>,
    tabIndentState: MutableState<Int>,
) {
    val clipboardManager = LocalClipboardManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
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
            Spacer(Modifier.width(12.dp))
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
            Spacer(Modifier.width(12.dp))
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
        TabIndent(tabIndentState)
    }
}