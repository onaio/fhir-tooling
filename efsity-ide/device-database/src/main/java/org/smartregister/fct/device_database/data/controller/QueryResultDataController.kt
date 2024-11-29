package org.smartregister.fct.device_database.data.controller

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.datatable.domain.model.Data
import org.smartregister.fct.datatable.domain.model.DataRow
import org.smartregister.fct.device_database.domain.model.QueryRequest
import org.smartregister.fct.device_database.domain.model.QueryResponse

internal open class QueryResultDataController(
    val initialLimit: Int,
    initialQuery: String,
    val queryResponse: QueryResponse,
    val database: String,
    componentContext: ComponentContext
) : DeviceDBDataTableController(
    scope = CoroutineScope(Dispatchers.Default),
    initialQuery = initialQuery,
    data = Data(
        columns = queryResponse.columns,
        rows = queryResponse.data
    ),
    totalRecords = queryResponse.count,
    initialLimit = initialLimit,
), ComponentContext by componentContext {

    override suspend fun runQuery(query: String, offset: Int, limit: Int): Result<List<DataRow>> {

        val activeDevice = DeviceManager.getActiveDevice()
        val selectedPackage = DeviceManager.getActivePackage().value

        return if (activeDevice == null) {
            Result.failure(NullPointerException("No Device Selected"))
        } else if (selectedPackage == null) {
            Result.failure(NullPointerException("No package selected"))
        } else {
            val result = activeDevice.runAppDBQuery(
                arg = QueryRequest(
                    database = database,
                    query = query,
                    offset = offset,
                    limit = limit
                ).asJSONString()
            )

            val response = buildResponse(result)
            return if (response.error == null) {
                setOffset(offset)
                setTotalRecords(response.count)
                Result.success(response.data)
            } else {
                Result.failure(result.exceptionOrNull() ?: UnknownError("Query Error"))
            }
        }
    }

    open fun buildResponse(result: Result<JSONObject>): QueryResponse {
        return QueryResponse.build(result)
    }
}