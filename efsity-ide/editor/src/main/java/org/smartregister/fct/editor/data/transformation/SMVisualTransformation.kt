package org.smartregister.fct.editor.data.transformation

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import org.smartregister.fct.common.domain.transformation.BaseVisualTransformation

internal class SMVisualTransformation(
    searchText: String,
    isDarkTheme: Boolean,
    colorScheme: ColorScheme
) : BaseVisualTransformation(searchText) {

    private var blueColor = Color(0xFF86B1FF)
    private var greenColor = Color(0xFF91BE61)
    private var yellowColor = Color(0xFFDEA834)

    init {
        if (!isDarkTheme) {
            blueColor = Color(0xFF0050A5)
            greenColor = Color(0xFF457700)
            yellowColor = Color(0xFFBB8800)
        }
    }

    private val baseColorStyle = SpanStyle(color = colorScheme.onBackground)
    private val blueColorStyle = SpanStyle(color = blueColor)
    private val greenColorStyle = SpanStyle(color = greenColor)
    private val greyColorStyle = SpanStyle(color = Color(0xFFA0A0A0))

    private val groupTagRegex = Regex("(?<=\\n|\\s)group(?=\\s+)")
    private val mapTagRegex = Regex("map(?=\\s+([\"']))")
    private val usesTagRegex = Regex("uses(?=\\s+([\"']))")
    private val ruleLabelRegex = Regex("(\"[a-zA-Z_]+\";)(\\s*\\n|\\n)")
    private val sourceInputRegex = Regex("(?<=\\s)->(?=\\s+.)")

    private val createObjectRegex = Regex("(?<=\\s|\\n)create(?=\\((['\"]).*(['\"])\\)\\s+)")
    private val sourceAndTargetRegex =
        Regex("(?<=\\(|,|,\\s|,\\s{2}|,\\s{3})\\n*\\s*(source|target)(?=\\s+.)")
    private val thenTagRegex = Regex("\\s*then(?=\\s*.)")
    private val asKeywordRegex = Regex("(?<=\\s)as(?=\\s+)")
    private val stringLiteralRegex = Regex("((?<!\\\\)['\"])((?:.(?!(?<!\\\\)\\1))*.?)\\1(?!;)")
    private val dotRegex = Regex("\\.")
    private val commentRegex = Regex("(?<!.)(\\s*/{2}.*)")

    override fun AnnotatedString.Builder.transform(text: String) {
        styleText(text, groupTagRegex, blueColorStyle)
        styleText(text, mapTagRegex, blueColorStyle)
        styleText(text, usesTagRegex, blueColorStyle)
        styleText(text, asKeywordRegex, blueColorStyle)
        styleText(text, ruleLabelRegex, greyColorStyle)
        styleText(text, sourceInputRegex, blueColorStyle)
        styleText(text, thenTagRegex, blueColorStyle)
        styleText(text, createObjectRegex, blueColorStyle)
        styleText(text, sourceAndTargetRegex, blueColorStyle)
        styleText(text, stringLiteralRegex, greenColorStyle)
        styleText(text, dotRegex, baseColorStyle)
        styleText(text, commentRegex, greyColorStyle)
    }
}