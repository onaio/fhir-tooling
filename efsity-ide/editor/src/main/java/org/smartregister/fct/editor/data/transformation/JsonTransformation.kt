package org.smartregister.fct.editor.data.transformation

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import org.smartregister.fct.common.domain.transformation.BaseVisualTransformation

internal class JsonTransformation(
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

    private val blueColorStyle = SpanStyle(color = blueColor)
    /*private val baseColor = SpanStyle(color = colorScheme.onBackground)
    private val greenColorStyle = SpanStyle(color = greenColor)
    private val yellowColorStyle = SpanStyle(color = yellowColor)*/

    private val keyRegex = Regex("((?<!\\\\)['\"])((?:.(?!(?<!\\\\)\\1))*.?)\\1(?=\\s*\\n?\\r?:)")
    /*private val openCurlyBracketRegex = Regex("(?<!\\w)\\{(?=\\s*\")")
    private val closeCurlyBracketRegex = Regex("\\}(?=\\s*,|\\n)")
    private val openBracketRegex = Regex("\\[(?=\\s*(\\{|\\[|\"|'|false|true|\\.?\\d))")
    private val closeBracketRegex = Regex("(?<=(false|true|\\n|\\s|\\}|]|\"|'|\\d))]")
    private val valueRegex =
        Regex("((?<!\\\\)['\"])((?:.(?!(?<!\\\\)\\1))*.?)\\1(?=\\s*(\\n?\\r?\\}|]|,))")
    private val valueRegexContinuation = Regex("(false|true|\\d*\\.?\\d)(?=\\s*\\n?\\r?([,}\\]]))")*/

    override fun AnnotatedString.Builder.transform(text: String) {
        styleText(text, keyRegex, blueColorStyle)
        //styleText(text, openCurlyBracketRegex, yellowColorStyle)
        //styleText(text, closeCurlyBracketRegex, yellowColorStyle)
        //styleText(text, openBracketRegex, yellowColorStyle)
        //styleText(text, closeBracketRegex, yellowColorStyle)
        //styleText(text, valueRegex, greenColorStyle)
        //styleText(text, valueRegexContinuation, greenColorStyle)
    }
}