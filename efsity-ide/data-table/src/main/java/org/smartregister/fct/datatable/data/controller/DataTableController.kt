package org.smartregister.fct.datatable.data.controller

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.KeyboardArrowDown
import org.smartregister.fct.aurora.auroraiconpack.KeyboardArrowUp
import org.smartregister.fct.datatable.data.enums.OrderBy
import org.smartregister.fct.datatable.domain.feature.DTColumn
import org.smartregister.fct.datatable.domain.feature.DTFilterColumn
import org.smartregister.fct.datatable.domain.feature.DTFilterable
import org.smartregister.fct.datatable.domain.feature.DTSortable
import org.smartregister.fct.datatable.domain.model.Data
import org.smartregister.fct.datatable.domain.model.DataRow
import org.smartregister.fct.logger.FCTLogger

abstract class DataTableController(
    val scope: CoroutineScope,
    data: Data
) {

    private var _loading = MutableStateFlow(false)
    internal val loading: StateFlow<Boolean> = _loading

    private var _error = MutableStateFlow<String?>(null)
    internal val error: StateFlow<String?> = _error

    private var _info = MutableStateFlow<String?>(null)
    internal val info: StateFlow<String?> = _info

    private val _selectedRowIndex = MutableStateFlow(-1)
    internal val selectedRowIndex: StateFlow<Int> = _selectedRowIndex

    internal val tempFilterValue = mutableMapOf<Int, String>()

    internal val columns = MutableStateFlow(data.columns)
    internal val filterColumns: Map<Int, MutableStateFlow<DTFilterColumn>>

    private val _activeSortColumnInfo = MutableStateFlow<Triple<Int, ImageVector, OrderBy>?>(null)
    internal val activeSortColumnInfo: StateFlow<Triple<Int, ImageVector, OrderBy>?> =
        _activeSortColumnInfo

    private var _records = MutableStateFlow(data.rows)
    internal val records: StateFlow<List<DataRow>> = _records

    init {
        filterColumns = mutableMapOf<Int, MutableStateFlow<DTFilterColumn>>().apply {
            columns.value.filterIsInstance<DTFilterColumn>().forEach {
                put(it.index, MutableStateFlow(it))
            }
        }
    }

    internal suspend fun updateSelectedRowIndex(rowIndex: Int) {
        _selectedRowIndex.emit(rowIndex)
    }

    private fun getFilteredColumns(): List<DTFilterColumn> {
        return filterColumns
            .entries
            .map { it.value.value }
            .filter { it.value.trim().isNotEmpty() }
    }

    fun isLoading() = _loading.value

    suspend fun setLoading(isLoading: Boolean) {
        _loading.emit(isLoading)
    }

    inline fun ifIsNotLoading(block: () -> Boolean): Boolean {
        return if (isLoading()) false else block()
    }

    internal suspend fun filter() {
        if (this is DTFilterable) {
            this.applyFilter(getFilteredColumns())
        }
    }

    internal suspend fun sort(dtColumn: DTColumn, orderBy: OrderBy) {
        if (this is DTSortable) {
            val result = applySort(dtColumn, orderBy)
            if (result.isSuccess) {
                val icon =
                    if (orderBy == OrderBy.Asc) AuroraIconPack.KeyboardArrowUp else AuroraIconPack.KeyboardArrowDown
                _activeSortColumnInfo.emit(
                    Triple(dtColumn.index, icon, orderBy)
                )
            } else {
                _activeSortColumnInfo.emit(null)
            }

            process(result)
        }
    }

    suspend fun process(result: Result<List<DataRow>>) {
        updateSelectedRowIndex(-1)
        if (result.isSuccess) {
            updateRecords(result.getOrThrow())
        } else {
            showError(result.exceptionOrNull()?.message)
        }
    }

    suspend fun updateRecords(newRecords: List<DataRow>) {
        _loading.emit(false)
        _records.emit(newRecords)
    }

    suspend fun showError(message: String?) {
        if (message != null) FCTLogger.e(message)
        _loading.emit(false)
        _error.emit(message)
        delay(200)
        _error.emit(null)
    }

    suspend fun showInfo(message: String?) {
        _loading.emit(false)
        _info.emit(message)
        delay(200)
        _info.emit(null)
    }
}