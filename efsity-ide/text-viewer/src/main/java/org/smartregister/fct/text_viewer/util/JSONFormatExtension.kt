package org.smartregister.fct.text_viewer.util

import androidx.compose.runtime.MutableState
import org.smartregister.fct.engine.util.compactJson
import org.smartregister.fct.engine.util.prettyJson

internal fun MutableState<String>.compactText(onError: (String) -> Unit) {
    try {
        if (value.trim().isEmpty()) return
        value = value.compactJson()
    } catch (ex: Exception) {
        onError("Content is not a valid JSON")
    }
}

internal fun MutableState<String>.formatText(tabIndent: Int, onError: (String) -> Unit) {
    try {
        if (value.trim().isEmpty()) return
        value = value.prettyJson(tabIndent)
    } catch (ex: Exception) {
        onError("Content is not a valid JSON")
    }
}