package org.smartregister.fct.fm.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.smartregister.fct.aurora.presentation.ui.components.TextButton
import org.smartregister.fct.fm.domain.model.Directory

@Composable
internal fun NavigationItem(directory: Directory, selected: Boolean, onClick: (Directory) -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            modifier = Modifier.fillMaxWidth(0.9f),
            label = directory.name,
            icon = directory.icon,
            selected = selected,
            textAlign = TextAlign.Start,
            onClick = {
                onClick(directory)
            }
        )
    }
}