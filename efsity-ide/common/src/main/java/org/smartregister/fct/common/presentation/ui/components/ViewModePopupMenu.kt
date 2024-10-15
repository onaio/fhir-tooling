package org.smartregister.fct.common.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.common.domain.model.ViewMode

@Composable
fun ViewModePopupMenu(
    modifier: Modifier = Modifier,
    onSelected: (ViewMode) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Box {
        SmallIconButton(
            iconModifier = modifier.size(18.dp),
            icon = Icons.Outlined.MoreVert,
            onClick = {
                expanded = !expanded
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Dock") },
                onClick = {
                    expanded = false
                    onSelected(ViewMode.Dock)
                }
            )
            DropdownMenuItem(
                text = { Text("Undock") },
                onClick = {
                    expanded = false
                    onSelected(ViewMode.Undock)
                }
            )
        }
    }
}