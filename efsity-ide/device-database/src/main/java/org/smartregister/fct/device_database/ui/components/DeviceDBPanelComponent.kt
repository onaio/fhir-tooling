package org.smartregister.fct.device_database.ui.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.device_database.data.persistence.DeviceDBConfigPersistence
import org.smartregister.fct.device_database.domain.model.ColumnInfo
import org.smartregister.fct.device_database.domain.model.DBInfo
import org.smartregister.fct.device_database.domain.model.QueryRequest
import org.smartregister.fct.device_database.domain.model.TableInfo
import org.smartregister.fct.engine.domain.model.PackageInfo
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.logger.FCTLogger

internal class DeviceDBPanelComponent(componentContext: ComponentContext) : QueryDependency, ComponentContext by componentContext {

    private var selectedDBInfo = DeviceDBConfigPersistence.sidePanelDBInfo

    private var _listOfTables = MutableStateFlow(DeviceDBConfigPersistence.tablesMap[selectedDBInfo.name] ?: listOf())
    val listOfTables: StateFlow<List<TableInfo>> = _listOfTables

    private var _loadingTables = MutableStateFlow(false)
    val loadingTables: StateFlow<Boolean> = _loadingTables

    private var _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        if (_listOfTables.value.isEmpty()) {
            componentScope.launch {
                getRequiredParam (false) { activeDevice, selectedPackage ->
                    fetchTables(selectedDBInfo, activeDevice, selectedPackage.packageId)
                }
            }
        }
    }

    fun reFetchTables() {
        if (_loadingTables.value) return
        componentScope.launch {
            getRequiredParam { activeDevice, selectedPackage ->
                fetchTables(selectedDBInfo, activeDevice, selectedPackage.packageId)
            }
        }
    }

    fun updateDatabase(dbInfo: DBInfo) {
        if (selectedDBInfo.name == dbInfo.name) return
        componentScope.launch {
            selectedDBInfo = dbInfo
            DeviceDBConfigPersistence.sidePanelDBInfo = dbInfo
            val existingTables = DeviceDBConfigPersistence.tablesMap[dbInfo.name]!!
            if (existingTables.isEmpty()) {
                reFetchTables()
            } else {
               _listOfTables.emit(existingTables)
           }
        }
    }

    private fun fetchTables(dbInfo: DBInfo, device: Device, packageId: String) {
        componentScope.launch {
            _loadingTables.emit(true)

            val result = device.runAppDBQuery(
                arg = QueryRequest(
                    database = dbInfo.name,
                    query = "SELECT * FROM sqlite_schema WHERE type='table' AND name NOT LIKE 'sqlite_%'",
                ).asJSONString()
            )

            _loadingTables.emit(false)
            if (result.isSuccess) {
                _listOfTables.emit(parseResultToTables(result))
                DeviceDBConfigPersistence.tablesMap[dbInfo.name] = _listOfTables.value
            } else {
                showError(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
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

    private suspend fun showError(message: String) {
        _error.emit(message)
        delay(200)
        _error.emit(null)
    }

    private fun parseResultToTables(result: Result<JSONObject>): List<TableInfo> {
        return try {
            val jsonArray = result.getOrThrow().getJSONArray("data")
            jsonArray
                .filterIsInstance<JSONObject>()
                .map {
                TableInfo(
                    name = it.getString("tbl_name"),
                    columnsInfo = decodeColumns(it.getString("sql"))
                )
            }
        } catch (ex: Exception) {
            FCTLogger.e(ex)
            listOf()
        }

    }

    private fun decodeColumns(tableDetails: String) : List<ColumnInfo> {

        val columnsInfo = mutableListOf<ColumnInfo>()
        val result = "`?\\w+`?\\s(INTEGER|TEXT|BLOB|REAL)\\s?(PRIMARY\\sKEY)?\\s?(AUTOINCREMENT)?\\s?(NOT\\sNULL)?".toRegex().findAll(tableDetails)

        result.forEachIndexed { index, matchResult ->
            val tokens = matchResult.value.trim().split(" ")

            val columnName = tokens[0].replace("`", "").trim()
            val columnType = tokens[1]
            var hasPrimaryKey = false
            var isNullable = true

            if (tokens.size > 2) {
                val updatedText = matchResult.value.replace(tokens[0], "").replace(tokens[1], "")

                if (updatedText.contains("PRIMARY KEY")) {
                    hasPrimaryKey = true
                }
                if (updatedText.contains("NOT NULL")) {
                    isNullable = false
                }
            }

            columnsInfo.add(
                ColumnInfo(
                    name = columnName,
                    type = columnType,
                    hasPrimaryKey = hasPrimaryKey,
                    isNullable = isNullable
                )
            )
        }

        return columnsInfo
    }
}