package org.smartregister.fct.rules.data.transformation

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

internal class RuleActionTransformation(
    isDarkTheme: Boolean,
    private val colorScheme: ColorScheme,
) : VisualTransformation {

    private var blueColor = Color(0xFF86B1FF)
    private var greenColor = Color(0xFF91BE61)
    private var yellowColor = Color(0xFFDEA834)
    private var grayColor = Color.Gray

    init {
        if (!isDarkTheme) {
            blueColor = Color(0xFF0050A5)
            greenColor = Color(0xFF457700)
            yellowColor = Color(0xFFBB8800)
            grayColor = Color.LightGray
        }
    }

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(buildAnnotatedString {

            append(text)

            // rules-engine method name finding
            ruleEngineMethodNames.forEach { methodName ->
                "(service|fhirPath)\\.$methodName".toRegex().findAll(text).forEach { matchResult ->
                    addSpanStyle(blueColor, null, matchResult)
                }
            }

            // text finding
            textRegex.findAll(text).forEach { matchResult ->
                addSpanStyle(greenColor, null, matchResult)
            }

            // entry point finding
            entryRegex.findAll(text).forEach { matchResult ->
                addSpanStyle(colorScheme.onBackground, FontWeight.Bold, matchResult)
            }
        }, OffsetMapping.Identity)
    }

    private fun AnnotatedString.Builder.addSpanStyle(color: Color, fontWeight: FontWeight?, matchResult: MatchResult) {
        addStyle(
            SpanStyle(
                color = color,
                fontWeight = fontWeight
            ),
            start = matchResult.range.first,
            end = matchResult.range.last + 1
        )
    }

    private val entryRegex = "data\\.put\\s*\\(|\\)(?=[^)]*\$)".toRegex()
    private val textRegex = "((?<!\\\\)['\"])((?:.(?!(?<!\\\\)\\1))*.?)\\1(?!;)".toRegex()

    private val ruleEngineMethodNames = listOf(
        "translate",
        "retrieveRelatedResources",
        "retrieveParentResource",
        "evaluateToBoolean",
        "mapResourcesToLabeledCSV",
        "mapResourceToLabeledCSV",
        "extractAge",
        "extractGender",
        "extractDOB",
        "prettifyDate",
        "daysPassed",
        "extractPractitionerInfoFromSharedPrefs",
        "formatDate",
        "generateRandomSixDigitInt",
        "filterResources",
        "filterResourcesByJsonPath",
        "joinToString",
        "limitTo",
        "mapResourcesToExtractedValues",
        "computeTotalCount",
        "retrieveCount",
        "sortResources",
        "generateTaskServiceStatus",
        "taskServiceStatusExist",
        "extractValue",
        "extractData",
    )
}