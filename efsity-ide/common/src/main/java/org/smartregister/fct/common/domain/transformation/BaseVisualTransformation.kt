package org.smartregister.fct.common.domain.transformation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import org.smartregister.fct.common.data.helper.TabOffsetMapping

private const val TAB_WIDTH_IN_SPACES = 4

abstract class BaseVisualTransformation(private val searchText: String = "") :
    VisualTransformation {


    private val originalTabPositions = mutableListOf<Int>()

    private fun transformText(text: AnnotatedString): String {
        originalTabPositions.clear()

        val builder = StringBuilder()
        for (offset in 0 until text.text.length) {
            val char = text.text[offset]
            if (char == '\t') {
                for (i in 0 until TAB_WIDTH_IN_SPACES) {
                    builder.append(' ')
                }
                originalTabPositions.add(offset)
            } else {
                builder.append(char)
            }
        }

        return builder.toString()
    }

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = buildAnnotatedString {
                val transformText = transformText(text)

                append(transformText)

                transform(transformText)

                if (searchText.isNotEmpty()) {
                    searchText.toRegex(
                        option = RegexOption.LITERAL
                    ).findAll(transformText).forEach { match ->
                        addStyle(
                            SpanStyle(
                                color = Color.Black,
                                background = Color.Yellow
                            ),
                            match.range.first,
                            match.range.last + 1
                        )
                    }
                }
            },
            offsetMapping = TabOffsetMapping(originalTabPositions)
        )
    }

    fun AnnotatedString.Builder.styleText(text: String, regex: Regex, spanStyle: SpanStyle) {
        regex.findAll(text).forEach { match ->
            addStyle(spanStyle, match.range.first, match.range.last + 1)
        }
    }

    abstract fun AnnotatedString.Builder.transform(text: String)
}