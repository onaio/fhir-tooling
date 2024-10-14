package org.smartregister.fct.datatable.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.ExpandAll
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.common.util.windowWidthResizePointer
import org.smartregister.fct.datatable.data.controller.DataTableController
import org.smartregister.fct.datatable.data.enums.OrderBy
import org.smartregister.fct.datatable.domain.feature.DTColumn
import org.smartregister.fct.datatable.domain.feature.DTSortable
import org.smartregister.fct.datatable.presentation.ui.view.defaultDataCellWidth
import org.smartregister.fct.datatable.presentation.ui.view.serialNoCellWidth

@Composable
internal fun ColumnHeader(
    controller: DataTableController,
    columns: List<DTColumn>,
    columnWidthMapState: SnapshotStateMap<Int, Dp>,
    columnLeadingIcon: (@Composable BoxScope.(DTColumn) -> Unit)? = null
) {

    val scope = rememberCoroutineScope()
    Row(Modifier.fillMaxHeight()) {

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(serialNoCellWidth)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "S.No",
                style = MaterialTheme.typography.titleSmall
            )

            DTVerticalDivider(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                alpha = 0.5f
            )
        }

        val activeSortColumnInfo by controller.activeSortColumnInfo.collectAsState()

        columns.forEach { dtColumn ->

            val orderBy = activeSortColumnInfo?.takeIf {
                it.first == dtColumn.index
            }?.let {
                if (it.third == OrderBy.Asc) OrderBy.Desc else OrderBy.Asc
            } ?: OrderBy.Asc

            val sortIcon = activeSortColumnInfo?.takeIf {
                it.first == dtColumn.index
            }?.second ?: AuroraIconPack.ExpandAll

            var rawX by remember {
                mutableStateOf(
                    columnWidthMapState[dtColumn.index] ?: defaultDataCellWidth
                )
            }

            DataBox(
                modifier = Modifier.clickable {
                    if (dtColumn.sortable && controller is DTSortable) {
                        scope.launch {
                            controller.sort(dtColumn, orderBy)
                        }
                    }
                },
                index = dtColumn.index,
                columnWidthMapState = columnWidthMapState
            ) {



                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = dtColumn.name,
                    style = MaterialTheme.typography.titleSmall
                )

                columnLeadingIcon?.invoke(this, dtColumn)

                if (dtColumn.sortable && controller is DTSortable) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(18.dp)
                            .align(Alignment.CenterEnd),
                        icon = sortIcon,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .pointerHoverIcon(windowWidthResizePointer)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    rawX = columnWidthMapState[dtColumn.index]!!
                                }
                            ) { _, dragAmount ->
                                rawX += dragAmount.toDp()

                                if (rawX > 40.dp) {
                                    columnWidthMapState[dtColumn.index] = rawX.coerceAtLeast(40.dp)
                                }
                            }
                        }
                )
            }
        }


    }
}