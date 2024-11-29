package org.smartregister.fct.datatable.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.smartregister.fct.datatable.presentation.ui.view.defaultDataCellWidth

@Composable
internal fun DataBox(
    modifier: Modifier = Modifier,
    index: Int,
    columnWidthMapState: SnapshotStateMap<Int, Dp>,
    enableDivider: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .width(columnWidthMapState[index] ?: defaultDataCellWidth)
            .fillMaxHeight(),
    ) {
        content()

        if (enableDivider) {
            DTVerticalDivider(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                alpha = 0.5f
            )
        }
    }
}