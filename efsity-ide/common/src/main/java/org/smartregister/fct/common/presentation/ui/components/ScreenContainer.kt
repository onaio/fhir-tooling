package org.smartregister.fct.common.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ScreenContainer(
    panelWidth: Dp = 200.dp,
    leftPanel: @Composable BoxScope.() -> Unit,
    mainContent: @Composable RowScope.() -> Unit
) {

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .width(panelWidth)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f))
        ) {
            leftPanel(this)
            VerticalDivider(modifier = Modifier.align(Alignment.CenterEnd))
        }

        mainContent(this)
    }

}