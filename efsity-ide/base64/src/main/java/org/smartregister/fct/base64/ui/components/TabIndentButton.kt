package org.smartregister.fct.base64.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
internal fun TabIndentButton(tabIndentState: MutableState<Int>) {

    ActionButton(
        label = "${tabIndentState.value} Space Indent",
        onClick = {
            tabIndentState.value = if (tabIndentState.value == 4) 2 else 4
        },
    )
}