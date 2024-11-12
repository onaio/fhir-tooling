package org.smartregister.fct.common.util

import org.smartregister.fct.common.presentation.ui.dialog.TextFieldValidation


val folderNameValidation: TextFieldValidation = {
    val result = "[\\s*a-zA-Z0-9_-]+".toRegex().matches(it)
    if (!result) {
        Pair(false, "Only alphabets, numbers, dash, underscore and space are Allowed")
    } else {
        Pair(true, "")
    }
}

val fileNameValidation: TextFieldValidation = {
    val result = "[\\s*a-zA-Z0-9_-]+".toRegex().matches(it)
    if (!result) {
        Pair(false, "Only alphabets, numbers, dash, underscore and space are Allowed")
    } else {
        Pair(true, "")
    }
}