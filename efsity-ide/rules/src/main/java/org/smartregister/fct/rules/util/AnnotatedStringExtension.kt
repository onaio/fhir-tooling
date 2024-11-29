package org.smartregister.fct.rules.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

internal fun AnnotatedString.Builder.appendBold(text: String) {
    withStyle(
        style = SpanStyle(
            fontWeight = FontWeight.Bold,
        )
    ) {
        append(text)
    }
}

internal fun AnnotatedString.Builder.appendColor(text: String, color: Color = Color(0xff7878ff)) {
    withStyle(
        style = SpanStyle(
            color = color
        )
    ) {
        append(text)
    }
}