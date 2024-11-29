package org.smartregister.fct.base64.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.Meta
import org.smartregister.fct.common.data.manager.AuroraManager
import org.smartregister.fct.engine.util.encodeResourceToString
import org.smartregister.fct.engine.util.prettyJson
import org.smartregister.fct.logger.FCTLogger
import java.time.Instant
import java.util.Date
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

context (AuroraManager)
@Composable
internal fun SourceToolbar(
    sourceTextState: MutableState<String>,
    targetTextState: MutableState<String>,
    tabIndentState: MutableState<Int>,
    binaryCheckedState: State<Boolean>
) {

    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            CompactJsonIconButton(
                textState = sourceTextState
            )
            Spacer(Modifier.width(10.dp))
            FormatJsonIconButton(
                textState = sourceTextState,
                tabIndentState = tabIndentState
            )
            Spacer(Modifier.width(10.dp))
            CopyAllContentIconButton(
                textState = sourceTextState
            )
            Spacer(Modifier.width(10.dp))
            ActionButton(
                label = "Encode",
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        setResult(
                            sourceTextState = sourceTextState,
                            targetTextState = targetTextState,
                            binaryCheckedState = binaryCheckedState,
                            tabIndentState = tabIndentState,
                            base64Type = Base64Type.Encode
                        )
                    }
                },
            )
            Spacer(Modifier.width(10.dp))
            ActionButton(
                label = "Decode",
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        setResult(
                            sourceTextState = sourceTextState,
                            targetTextState = targetTextState,
                            binaryCheckedState = binaryCheckedState,
                            tabIndentState = tabIndentState,
                            base64Type = Base64Type.Decode
                        )

                    }
                },
            )

        }
        TabIndentButton(tabIndentState)
    }
}

context (AuroraManager)
@OptIn(ExperimentalEncodingApi::class)
private fun setResult(
    sourceTextState: MutableState<String>,
    targetTextState: MutableState<String>,
    binaryCheckedState: State<Boolean>,
    tabIndentState: MutableState<Int>,
    base64Type: Base64Type,
) {

    try {
        if (binaryCheckedState.value) {
            val binaryResource = Binary().apply {
                meta = Meta().apply {
                    lastUpdated = Date.from(Instant.now())
                }
                contentType = "application/json"
                content = sourceTextState.value.toByteArray()
            }
            targetTextState.value =
                binaryResource.encodeResourceToString().prettyJson(tabIndentState.value)
        } else {
            val result = when (base64Type) {
                Base64Type.Encode -> Base64.encode(sourceTextState.value.toByteArray())
                Base64Type.Decode -> String(Base64.decode(sourceTextState.value))
            }
            targetTextState.value = result
        }
    } catch (ex: Exception) {
        showErrorSnackbar(ex.message)
        FCTLogger.e(ex)
    }
}

private enum class Base64Type {
    Encode, Decode
}