package org.smartregister.fct.datatable.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.launch
import org.smartregister.fct.datatable.data.controller.DataTableController
import org.smartregister.fct.datatable.domain.feature.DTColumn
import org.smartregister.fct.datatable.domain.feature.DTFilterableType
import org.smartregister.fct.datatable.domain.model.DataFilterColumn
import org.smartregister.fct.datatable.domain.model.DataFilterTypeColumn
import org.smartregister.fct.datatable.presentation.ui.view.serialNoCellWidth

@Composable
internal fun ColumnFilter(
    controller: DataTableController,
    columns: List<DTColumn>,
    columnWidthMapState: SnapshotStateMap<Int, Dp>,
) {

    val scope = rememberCoroutineScope()
    Row(Modifier.fillMaxHeight()) {

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(serialNoCellWidth)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            DTVerticalDivider(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                alpha = 0.5f
            )
        }

        columns.forEach { dtColumn ->

            DataBox(
                index = dtColumn.index,
                columnWidthMapState = columnWidthMapState
            ) {

                val dataFilterTypeColumn = controller
                    .filterColumns[dtColumn.index]
                    ?.collectAsState()
                    ?.value

                if (dataFilterTypeColumn is DataFilterTypeColumn && controller is DTFilterableType) {

                    ConstraintLayout(Modifier.fillMaxSize()) {

                        val (filterRef, typeRef) = createRefs()

                        DataFilterTextField(
                            modifier = Modifier.constrainAs(filterRef) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                end.linkTo(typeRef.start)
                                bottom.linkTo(parent.bottom)
                                width = Dimension.fillToConstraints
                            },
                            controller = controller,
                            dtColumn = dataFilterTypeColumn
                        )

                        ColumnFilterTypePopupMenu(
                            typeRef = typeRef,
                            controller = controller,
                            dataFilterTypeColumn = dataFilterTypeColumn,
                            onSelected = { value ->
                                scope.launch {
                                    controller.filterColumns[dtColumn.index]?.emit(value)
                                    if (value.value.trim()
                                            .isNotEmpty() && value.filterType.label != dataFilterTypeColumn.filterType.label
                                    ) {
                                        controller.filter()
                                    }
                                }
                            }
                        )
                    }
                } else if (dataFilterTypeColumn is DataFilterColumn) {
                    DataFilterTextField(
                        modifier = Modifier.fillMaxSize(),
                        controller = controller,
                        dtColumn = dataFilterTypeColumn
                    )
                }
            }
        }
    }
}