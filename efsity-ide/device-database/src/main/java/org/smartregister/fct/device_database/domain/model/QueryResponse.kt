package org.smartregister.fct.device_database.domain.model

import org.json.JSONObject
import org.smartregister.fct.datatable.data.filtertype.EqualFilterType
import org.smartregister.fct.datatable.domain.feature.DTColumn
import org.smartregister.fct.datatable.domain.model.DataCell
import org.smartregister.fct.datatable.domain.model.DataColumn
import org.smartregister.fct.datatable.domain.model.DataFilterTypeColumn
import org.smartregister.fct.datatable.domain.model.DataRow

data class QueryResponse(
    val count: Int = 0,
    val columns: List<DTColumn> = listOf(),
    val data: List<DataRow> = listOf(),
    val queryMethod: QueryMethod?,
    val error: String? = ""
) {

    companion object {
        fun build(result: Result<JSONObject>): QueryResponse {

            return if (result.isSuccess) {
                val jsonObject = result.getOrThrow()

                if (jsonObject.get("method") == "rawQuery") {
                    val count = jsonObject.getInt("count")
                    val columns = jsonObject
                        .getJSONArray("columnNames")
                        .filterIsInstance<String>()
                        .mapIndexed { index, columnName ->
                            DataColumn(
                                index = index,
                                name = columnName,
                                sortable = false,
                                editable = false,
                                isPrimary = false
                            )
                        }

                    val data = jsonObject
                        .getJSONArray("data")
                        .filterIsInstance<JSONObject>()
                        .mapIndexed { rowIndex, jsonObj ->
                            DataRow(
                                index = rowIndex,
                                data = columns.mapIndexed { index, col ->
                                    DataCell(
                                        index = index,
                                        data = getCellData(col.name, jsonObj),
                                        editable = col.editable,
                                        column = col,
                                    )
                                }
                            )
                        }

                    QueryResponse(
                        count = count,
                        columns = columns,
                        data = data,
                        error = null,
                        queryMethod = QueryMethod.RawQuery
                    )
                } else {
                    QueryResponse(
                        count = 0,
                        error = null,
                        queryMethod = QueryMethod.ExecSql
                    )
                }


            } else {
                QueryResponse(
                    error = result.exceptionOrNull()?.message ?: "Query Error",
                    queryMethod = null
                )
            }
        }

        fun build(
            result: Result<JSONObject>,
            columnsInfo: List<ColumnInfo>
        ): QueryResponse {

            return if (result.isSuccess) {
                val jsonObject = result.getOrThrow()
                val count = jsonObject.getInt("count")
                val columns = jsonObject
                    .getJSONArray("columnNames")
                    .filterIsInstance<String>()
                    .mapIndexed { index, columnName ->
                        val isPrimary = columnsInfo.firstOrNull { it.name == columnName }?.hasPrimaryKey ?: false
                        DataFilterTypeColumn(
                            index = index,
                            name = columnName,
                            sortable = true,
                            value = "",
                            filterType = EqualFilterType,
                            editable = !isPrimary,
                            isPrimary = isPrimary
                        )
                    }

                val data = jsonObject
                    .getJSONArray("data")
                    .filterIsInstance<JSONObject>()
                    .mapIndexed { rowIndex, jsonObj ->
                        DataRow(
                            index = rowIndex,
                            data = columns.mapIndexed { index, col ->
                                DataCell(
                                    index = index,
                                    data = getCellData(col.name, jsonObj),
                                    editable = col.editable,
                                    column = col
                                )
                            }
                        )
                    }

                QueryResponse(
                    count = count,
                    columns = columns,
                    data = data,
                    error = null,
                    queryMethod = QueryMethod.RawQuery
                )
            } else {
                QueryResponse(
                    error = result.exceptionOrNull()?.message ?: "Query Error",
                    queryMethod = QueryMethod.RawQuery
                )
            }
        }

        private fun getCellData(columnName: String, jsonObject: JSONObject): String? {
            return try {
                jsonObject.get(columnName).toString()
            } catch (ex: Exception) {
                null
            }
        }
    }
}