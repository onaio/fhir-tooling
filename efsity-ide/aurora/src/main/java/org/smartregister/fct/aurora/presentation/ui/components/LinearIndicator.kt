package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LinearIndicator(
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    LinearProgressIndicator(
        modifier = modifier,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        trackColor = MaterialTheme.colorScheme.surface
    )
}