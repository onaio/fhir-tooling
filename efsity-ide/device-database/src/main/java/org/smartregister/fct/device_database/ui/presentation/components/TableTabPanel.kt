package org.smartregister.fct.device_database.ui.presentation.components

import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.KeyVertical
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.LinearIndicator
import org.smartregister.fct.common.presentation.ui.dialog.rememberResourceUploadDialog
import org.smartregister.fct.datatable.presentation.ui.view.DataTable
import org.smartregister.fct.device_database.ui.components.TableTabComponent

@Composable
internal fun TableTabPanel(
    componentContext: ComponentContext,
    tableTabComponent: TableTabComponent
) {

    val isLoading by tableTabComponent.loading.collectAsState()
    val tableRequestDataController by tableTabComponent.tableResultDataController.collectAsState()

    Column {
        if (isLoading) {
            LinearIndicator()
        }

        if (tableRequestDataController != null) {

            val queryResponse = tableRequestDataController!!.queryResponse

            CheckQueryResponseError(
                queryResponse = queryResponse,
                reloadAction = {
                    tableTabComponent.runQuery()
                }
            )
            if (queryResponse.error == null) {

                val resourceUploadDialog = rememberResourceUploadDialog(
                    componentContext = componentContext
                )

                DataTable(
                    controller = tableRequestDataController!!,
                    componentContext = tableTabComponent,
                    columnLeadingIcon = {
                        if (it.isPrimary) {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .size(18.dp)
                                    .align(Alignment.CenterStart),
                                icon = AuroraIconPack.KeyVertical,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    },
                    customContextMenuItems = { colIndex, dtColumn, rowIndex, dtRow, dataCell ->
                        mutableListOf<ContextMenuItem>().apply {

                            if (dtColumn.name == "serializedResource") {
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
}