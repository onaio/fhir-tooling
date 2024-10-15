package org.smartregister.fct.device_database.ui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.device_database.ui.components.QueryTabComponent

@Composable
internal fun QueryTabToolbar(component: QueryTabComponent) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {

        DatabaseDropdown(
            modifier = Modifier.width(150.dp),
            onDBSelected = {
                component.selectedDBInfo = it
            },
            initialSelected = component.selectedDBInfo
        )
        VerticalDivider()
        Spacer(Modifier.width(14.dp))
        SmallIconButton(
            iconModifier = Modifier.size(16.dp),
            tooltip = "Run\nCtrl + Enter",
            icon = Icons.AutoMirrored.Outlined.Send,
            onClick = component::runQuery
        )
    }
}