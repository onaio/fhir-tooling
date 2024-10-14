package org.smartregister.fct.pm.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun NoPackageFound(isFound: Boolean) {
    if (!isFound) {
        Text(
            modifier = Modifier.fillMaxWidth().alpha(0.5f).padding(top = 20.dp),
            textAlign = TextAlign.Center,
            text = "No Package(s) found",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}