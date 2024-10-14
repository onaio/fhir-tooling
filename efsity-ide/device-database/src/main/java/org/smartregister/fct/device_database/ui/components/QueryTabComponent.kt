package org.smartregister.fct.device_database.ui.components

import androidx.compose.ui.text.input.TextFieldValue
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Resource
import org.json.JSONObject
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.device_database.data.controller.QueryResultDataController
import org.smartregister.fct.device_database.data.persistence.DeviceDBConfigPersistence
import org.smartregister.fct.device_database.domain.model.QueryRequest
import org.smartregister.fct.device_database.domain.model.QueryResponse
import org.smartregister.fct.engine.domain.model.PackageInfo
import org.smartregister.fct.engine.util.compactJson
import org.smartregister.fct.engine.util.decodeResourceFromString

class QueryTabComponent(
    componentContext: ComponentContext,
) : QueryTabBaseComponent(componentContext), QueryDependency {

    var selectedDBInfo = DeviceDBConfigPersistence.listOfDB[0]

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _query = MutableStateFlow(TextFieldValue(""))
    val query: StateFlow<TextFieldValue> = _query

    private val _queryResultDataController = MutableStateFlow<QueryResultDataController?>(null)
    internal val queryResultDataController: StateFlow<QueryResultDataController?> = _queryResultDataController

    private val limit = 50

    fun updateTextField(textFieldValue: TextFieldValue) {
        CoroutineScope(Dispatchers.Default).launch {
            _query.emit(textFieldValue)
        }
    }

    fun runQuery() {
        if (_query.value.text.trim().isEmpty() || _loading.value) return

        CoroutineScope(Dispatchers.Default).launch {

            getRequiredParam { device, pkg ->
                _loading.emit(true)
                val result = device.runAppDBQuery(
                    arg = QueryRequest(
                        database = selectedDBInfo.name,
                        query = _query.value.text,
                        limit = limit
                    ).asJSONString()
                )

                val queryResponse = QueryResponse.build(result)

                _loading.emit(false)
                _queryResultDataController.emit(
                    QueryResultDataController(
                        initialLimit = limit,
                        initialQuery = _query.value.text,
                        queryResponse = queryResponse,
                        database = selectedDBInfo.name,
                        componentContext = this@QueryTabComponent
                    )
                )
            }
        }
    }

    suspend fun updateRecordByResourceId(
        serializedResource: String,
        database: String = DeviceDBConfigPersistence.listOfDB[0].name,
    ): Result<JSONObject> {

        val minifyResource = try {
            serializedResource.compactJson()
        } catch (ex: Exception) {
            return Result.failure(ex)
        }

        val resourceId = try {
            minifyResource.decodeResourceFromString<Resource>().idPart
        } catch (ex: Exception) {
            return Result.failure(ex)
        }

        val activeDevice = DeviceManager.getActiveDevice()
        val selectedPackage = DeviceManager.getActivePackage().value

        if (activeDevice == null) {
            return Result.failure(IllegalStateException("No Device Selected"))
        } else if (selectedPackage == null) {
            return Result.failure(IllegalStateException("No package selected"))
        }

        val query =
            "UPDATE ResourceEntity SET serializedResource='${minifyResource.replace("'","''")}' WHERE resourceId='$resourceId'"

        return activeDevice.runAppDBQuery(
            arg = QueryRequest(
                database = database,
                query = query,
                limit = 1
            ).asJSONString()
        )
    }

    override suspend fun getRequiredParam(showErrors: Boolean, info: suspend (Device, PackageInfo) -> Unit) {
        val activeDevice = DeviceManager.getActiveDevice()
        val selectedPackage = DeviceManager.getActivePackage().value

        if (activeDevice == null) {
            if (showErrors) showError("No Device Selected")
            return
        } else if (selectedPackage == null) {
            if (showErrors) showError("No package selected")
            return
        }
        info(activeDevice, selectedPackage)
    }

    private suspend fun showError(message: String?) {
        _error.emit(message)
        delay(200)
        _error.emit(null)
    }
}