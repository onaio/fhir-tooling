package org.smartregister.fct.fhirman.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.aurora.presentation.ui.components.OutlinedButton
import org.smartregister.fct.settings.presentation.ui.dialogs.rememberSettingsDialog

@Composable
internal fun NoConfig(componentContext: ComponentContext) {

    val settingsDialog = rememberSettingsDialog(
        componentContext = componentContext
    )

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No server configs found"
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                label = "Create",
                onClick = {
                    settingsDialog.show()
                }
            )
        }
    }
}