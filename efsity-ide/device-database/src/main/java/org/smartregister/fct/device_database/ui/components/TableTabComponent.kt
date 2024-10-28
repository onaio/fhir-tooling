package org.smartregister.fct.device_database.ui.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.device_database.data.controller.TableResultDataController
import org.smartregister.fct.device_database.domain.model.QueryRequest
import org.smartregister.fct.device_database.domain.model.QueryResponse
import org.smartregister.fct.device_database.domain.model.TableInfo
import org.smartregister.fct.engine.domain.model.PackageInfo

internal class TableTabComponent(
    componentContext: ComponentContext,
    val tableInfo: TableInfo,
    private val database: String,
) : QueryTabBaseComponent(componentContext), QueryDependency {

    private var _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _tableResultDataController = MutableStateFlow<TableResultDataController?>(null)
    val tableResultDataController: StateFlow<TableResultDataController?> = _tableResultDataController

    private val query = "SELECT * FROM ${tableInfo.name}"
    private val limit = 50

    init {
        runQuery()
    }

    fun runQuery() {

        CoroutineScope(Dispatchers.Default).launch {
            getRequiredParam { device, pkg ->
                _loading.emit(true)
                val result = device.runAppDBQuery(
                    arg = QueryRequest(
                        database = database,
                        query = query,
                        limit = limit
                    ).asJSONString()
                )

                _loading.emit(false)
                _tableResultDataController.emit(
                    TableResultDataController(
                        initialLimit = limit,
                        initialQuery = query,
                        queryResponse = QueryResponse.build(
                            result = result,
                            columnsInfo = tableInfo.columnsInfo
                        ),
                        database = database,
                        componentContext = this@TableTabComponent,
                        tableInfo = tableInfo
                    )
                )
            }
        }
    }

    override suspend fun getRequiredParam(
        showErrors: Boolean,
        info: suspend (Device, PackageInfo) -> Unit
    ) {
        if (componentContext is QueryDependency) {
            componentContext.getRequiredParam(showErrors, info)
        }
    }
}