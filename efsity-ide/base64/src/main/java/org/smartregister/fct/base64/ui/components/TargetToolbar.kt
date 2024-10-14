package org.smartregister.fct.base64.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.smartregister.fct.common.data.manager.AuroraManager

context (AuroraManager)
@Composable
internal fun TargetToolbar(
    targetTextState: MutableState<String>,
    tabIndentState: MutableState<Int>,
    binaryCheckedState: MutableState<Boolean>
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactJsonIconButton(
                textState = targetTextState
            )
            Spacer(Modifier.width(10.dp))
            FormatJsonIconButton(
                textState = targetTextState,
                tabIndentState = tabIndentState
            )
            Spacer(Modifier.width(10.dp))
            CopyAllContentIconButton(
                textState = targetTextState
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Checkbox(
                modifier = Modifier.size(30.dp).graphicsLayer {
                    scaleX = 0.8f
                    scaleY = 0.8f
                    translationY = -1f
                },
                checked = binaryCheckedState.value,
                onCheckedChange = {
                    binaryCheckedState.value = it
                }
            )
            Spacer(Modifier.width(4.dp))
            Text("Binary Resource")
            Spacer(Modifier.width(12.dp))
            TabIndentButton(tabIndentState)
        }
    }
}