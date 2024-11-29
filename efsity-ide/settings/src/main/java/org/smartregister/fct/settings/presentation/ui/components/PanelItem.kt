package org.smartregister.fct.settings.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.smartregister.fct.aurora.presentation.ui.components.TextButton
import org.smartregister.fct.settings.domain.model.Setting

@Composable
internal fun PanelItem(setting: Setting, selected: Boolean, onClick: (Setting) -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            modifier = Modifier.fillMaxWidth(0.9f),
            label = setting.label,
            icon = setting.icon,
            selected = selected,
            textAlign = TextAlign.Start,
            onClick = {
                onClick(setting)
            }
        )
    }
}