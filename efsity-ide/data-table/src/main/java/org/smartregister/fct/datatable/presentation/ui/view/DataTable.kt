package org.smartregister.fct.datatable.presentation.ui.view

import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.datatable.data.controller.DataTableController
import org.smartregister.fct.datatable.domain.feature.DTColumn
import org.smartregister.fct.datatable.domain.feature.DTFilterable
import org.smartregister.fct.datatable.domain.model.DataCell
import org.smartregister.fct.datatable.domain.model.DataRow
import org.smartregister.fct.datatable.presentation.ui.components.ColumnFilter
import org.smartregister.fct.datatable.presentation.ui.components.ColumnHeader
import org.smartregister.fct.datatable.presentation.ui.components.DTHorizontalDivider
import org.smartregister.fct.datatable.presentation.ui.components.PopulateData
import org.smartregister.fct.datatable.presentation.ui.components.TopBar

internal val defaultDataCellWidth = 200.dp
internal val serialNoCellWidth = 60.dp


@Composable
fun DataTable(
    componentContext: ComponentContext,
    controller: DataTableController,
    columnLeadingIcon: (@Composable BoxScope.(DTColumn) -> Unit)? = null,
    customContextMenuItems: ((Int, DTColumn, Int, DataRow, DataCell) -> List<ContextMenuItem>)? = null
) {

    val error by controller.error.collectAsState()
    val info by controller.info.collectAsState()
    val horizontalScrollState = rememberScrollState()
    //val verticalScrollState = rememberScrollState()
    val dataRowBGOdd = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f)
    val dataRowBGEven = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f)
    var dtWidth by remember { mutableStateOf(0.dp) }
    val columns by controller.columns.collectAsState()

    Aurora(
        componentContext = componentContext
    ) {

        with(it) {
            showErrorSnackbar(error)
            showSnackbar(info)
        }

        val columnWidthMapState = remember {
            mutableStateMapOf<Int, Dp>().apply {
                columns.forEachIndexed { index, _ ->
                    put(index, defaultDataCellWidth)
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(controller)
            DTHorizontalDivider(dtWidth, alpha = 0.5f)
            Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f))
                        .horizontalScroll(horizontalScrollState)
                        .onGloballyPositioned { layoutCoords ->
                            dtWidth = layoutCoords.size.width.dp
                        }
                ) {
                    ColumnHeader(
                        controller = controller,
                        columns = columns,
                        columnWidthMapState = columnWidthMapState,
                        columnLeadingIcon = columnLeadingIcon
                    )
                }
                if (controller is DTFilterable) {
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f))
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        ColumnFilter(
                            controller = controller,
                            columns = columns,
                            columnWidthMapState = columnWidthMapState
                        )
                    }
                }
                DTHorizontalDivider(dtWidth, alpha = 0.5f)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                    //.horizontalScroll(horizontalScrollState)
                ) {
                    PopulateData(
                        controller = controller,
                        componentContext = componentContext,
                        columns = columns,
                        columnWidthMapState = columnWidthMapState,
                        dataRowBGOdd = dataRowBGOdd,
                        dataRowBGEven = dataRowBGEven,
                        dtWidth = dtWidth,
                        customContextMenuItems = customContextMenuItems
                    )
                }
            }
        }
    }
}