package org.smartregister.fct.device_database.ui.presentation.components

import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.common.presentation.ui.dialog.rememberResourceUploadDialog
import org.smartregister.fct.datatable.presentation.ui.view.DataTable
import org.smartregister.fct.device_database.data.controller.QueryResultDataController
import org.smartregister.fct.device_database.domain.model.QueryMethod

@Composable
internal fun QueryResult(
    component: QueryResultDataController,
    componentContext: ComponentContext,
    onDataSelect: ((String) -> Unit)?
) {

    val queryResponse = component.queryResponse
    CheckQueryResponseError(queryResponse)

    if (queryResponse.queryMethod == QueryMethod.ExecSql) {
        if (queryResponse.error == null) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Query successfully executed",
            )
        }
    } else {
        if (queryResponse.error == null) {

            val resourceUploadDialog = rememberResourceUploadDialog(
                componentContext = componentContext
            )

            DataTable(
                controller = component,
                componentContext = componentContext,
                customContextMenuItems = { colIndex, dtColumn, rowIndex, dtRow, dataCell ->
                    mutableListOf<ContextMenuItem>().apply {

                        if (onDataSelect != null) {
                            add(
                                ContextMenuItem("Select") {
                                    onDataSelect(dataCell.data ?: "")
                                }
                            )
                        }

                        if (onDataSelect == null && dtColumn.name == "serializedResource") {
                            add(
                                ContextMenuItem("Upload on Server") {
                                    resourceUploadDialog.show(dataCell.data)
                                }
                            )
                        }
                    }
                },
            )
        }
    }

}