package org.smartregister.fct.text_viewer.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.OutlinedButton

@Composable
internal fun TabIndent(tabIndentState: MutableState<Int>) {

    OutlinedButton(
        modifier = Modifier.height(30.dp),
        label = "${tabIndentState.value} Space Indent",
        contentPadding = PaddingValues(8.dp, 0.dp),
        style = MaterialTheme.typography.bodyMedium,
        onClick = {
            tabIndentState.value = if (tabIndentState.value == 4) 2 else 4
        },
    )
}