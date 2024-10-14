package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.TextField as Mat3TextField

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    readOnly: Boolean = false,
    singleLine: Boolean = true
) {
    Mat3TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            label?.let { Text(label) }
        },
        placeholder = {
            placeholder?.let { Text(placeholder) }
        },
        readOnly = readOnly,
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodyMedium,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
    )
}