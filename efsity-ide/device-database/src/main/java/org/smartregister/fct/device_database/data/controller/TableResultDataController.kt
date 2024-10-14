package org.smartregister.fct.device_database.data.controller

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.datatable.data.enums.OrderBy
import org.smartregister.fct.datatable.domain.feature.DTColumn
import org.smartregister.fct.datatable.domain.feature.DTEditable
import org.smartregister.fct.datatable.domain.feature.DTFilterColumn
import org.smartregister.fct.datatable.domain.feature.DTFilterableType
import org.smartregister.fct.datatable.domain.feature.DTSortable
import org.smartregister.fct.datatable.domain.model.DataCell
import org.smartregister.fct.datatable.domain.model.DataRow
import org.smartregister.fct.device_database.domain.model.ColumnInfo
import org.smartregister.fct.device_database.domain.model.QueryRequest
import org.smartregister.fct.device_database.domain.model.QueryResponse
import org.smartregister.fct.device_database.domain.model.TableInfo
import org.smartregister.fct.engine.util.componentScope

internal class TableResultDataController(
    componentContext: ComponentContext,
    queryResponse: QueryResponse,
    initialQuery: String,
    initialLimit: Int,
    database: String,
    val tableInfo: TableInfo,
) : QueryResultDataController(
    initialQuery = initialQuery,
    componentContext = componentContext,
    initialLimit = initialLimit,
    queryResponse = queryResponse,
    database = database
), DTSortable, DTFilterableType, DTEditable {

    override suspend fun applyFilter(dataColumnFilter: List<DTFilterColumn>) {
        this.dataColumnFilter = dataColumnFilter
        process(execQuery())
    }

    override suspend fun applySort(dtColumn: DTColumn, orderBy: OrderBy): Result<List<DataRow>> {
        this.sortConfig = Pair(dtColumn, orderBy)
        return execQuery()
    }

    override fun update(dataCell: DataCell, dataRow: DataRow, dataRows: List<DataRow>) {

        componentScope.launch {
            val primaryColumn = getPrimaryColumn()

            if (primaryColumn == null) {
                showError("No primary column found")
                return@launch
            }

            val primaryValue = dataRow.data.first {
                it.column.name == primaryColumn.name
            }

            val activeDevice = DeviceManager.getActiveDevice()
            val selectedPackage = DeviceManager.getActivePackage().value

            if (activeDevice == null) {
                showError("No Device Selected")
                return@launch
            } else if (selectedPackage == null) {
                showError("No package selected")
                return@launch
            }

            setLoading(true)

            val query = "UPDATE ${tableInfo.name} SET ${dataCell.column.name}='${dataCell.data?.replace("'","''")}' WHERE ${primaryColumn.name}='${primaryValue.data}'"

            val result = activeDevice.runAppDBQuery(
                arg = QueryRequest(
                    database = database,
                    query = query,
                    limit = 1
                ).asJSONString()
            )

            if (result.isSuccess) {
                showInfo("Record updated successfully")
                val updatedRows = dataRows.toMutableList()
                updatedRows[dataRow.index] = dataRow.copy(
                    data = dataRow.data.map {
                        if (it.index == dataCell.index) {
                            dataCell
                        } else {
                            it
                        }
                    }
                )
                updateRecords(updatedRows)
            } else {
                showError(result.exceptionOrNull()?.message ?: "Query Error")
            }
        }

    }

    override fun buildResponse(result: Result<JSONObject>): QueryResponse {
        return QueryResponse.build(
            result = result,
            columnsInfo = tableInfo.columnsInfo
        )
    }

    private fun getPrimaryColumn(): ColumnInfo? {
        return tableInfo.columnsInfo.firstOrNull {
            it.hasPrimaryKey
        }
    }
}