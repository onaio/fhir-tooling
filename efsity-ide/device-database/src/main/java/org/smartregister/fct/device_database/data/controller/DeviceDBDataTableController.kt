package org.smartregister.fct.device_database.data.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.smartregister.fct.datatable.data.controller.DataTableController
import org.smartregister.fct.datatable.data.enums.OrderBy
import org.smartregister.fct.datatable.data.filtertype.EqualFilterType
import org.smartregister.fct.datatable.domain.feature.DTColumn
import org.smartregister.fct.datatable.domain.feature.DTCountable
import org.smartregister.fct.datatable.domain.feature.DTFilterColumn
import org.smartregister.fct.datatable.domain.feature.DTPagination
import org.smartregister.fct.datatable.domain.feature.DTRefreshable
import org.smartregister.fct.datatable.domain.model.Data
import org.smartregister.fct.datatable.domain.model.DataFilterTypeColumn
import org.smartregister.fct.datatable.domain.model.DataRow
import kotlin.math.max

internal abstract class DeviceDBDataTableController(
    scope: CoroutineScope,
    private val initialQuery: String,
    data: Data,
    totalRecords: Int,
    initialLimit: Int
) : DataTableController(
    scope = scope,
    data = data
), DTRefreshable, DTCountable, DTPagination{

    private var _limit = MutableStateFlow(initialLimit)
    val limit: StateFlow<Int> = _limit

    var dataColumnFilter: List<DTFilterColumn> = listOf()
    var sortConfig: Pair<DTColumn, OrderBy>? = null

    private var offset: Int = 0
    private var count = totalRecords

    override suspend fun refreshData() {
        process(execQuery())
    }

    override fun totalRecords(): Int = count

    fun setTotalRecords(totalRecords: Int) {
        this.count = totalRecords
    }

    override fun setOffset(offset: Int) {
        this.offset = max(0, offset)
    }

    override fun getOffset(): Int = offset

    override fun setLimit(newLimit: Int) {
        if (newLimit < 1) return
        scope.launch { _limit.emit(newLimit) }
    }

    override fun getLimit(): Int = _limit.value

    override suspend fun changeLimit(newLimit: Int) {
        if (newLimit < 1 && totalRecords() > 0) return
        val localLimit = getLimit()
        setLimit(newLimit)

        execQuery(limit = newLimit).let {
            if (it.isFailure) {
                setLimit(localLimit)
            }
            process(it)
        }
    }

    override fun canGoFirstPage() = ifIsNotLoading { getOffset() > 0 }

    override fun canGoPreviousPage() = ifIsNotLoading { getOffset() > 0 }

    override fun canGoNextPage() = ifIsNotLoading { (getOffset() + getLimit()) < totalRecords() }

    override fun canGoLastPage() = ifIsNotLoading { (getOffset() + getLimit()) < totalRecords() }

    override suspend fun gotoFirstPage() {
        val localOffset = getOffset()
        setOffset(0)
        execQuery().let {
            if (it.isFailure) {
                setOffset(localOffset)
            }
            process(it)
        }
    }

    override suspend fun gotoPreviousPage() {
        val localOffset = getOffset()
        setOffset(localOffset - getLimit())
        execQuery().let {
            if (it.isFailure) {
                setOffset(localOffset)
            }
            process(it)
        }
    }

    override suspend fun gotoNextPage() {
        val localOffset = getOffset()
        setOffset(localOffset + getLimit())
        execQuery().let {
            if (it.isFailure) {
                setOffset(localOffset)
            }
            process(it)
        }
    }

    override suspend fun gotoLastPage() {
        val localOffset = getOffset()
        setOffset(totalRecords() - getLimit())
        execQuery().let {
            if (it.isFailure) {
                setOffset(localOffset)
            }
            process(it)
        }
    }

    suspend fun execQuery(
        sqlQuery: String = buildQuery(),
        offset: Int = getOffset(),
        limit: Int = getLimit()
    ) : Result<List<DataRow>>{

        setLoading(true)

        return runQuery(
            query = sqlQuery,
            offset = offset,
            limit = limit
        )
    }

    private fun buildQuery() : String {
        return dataColumnFilter
            .map {
                if (it is DataFilterTypeColumn) {
                    it.filterType.create(it)
                } else {
                    EqualFilterType.create(it)
                }
            }.let {
                if (it.isNotEmpty()) {
                    "$initialQuery WHERE ${it.joinToString(" AND ")}"
                } else {
                    initialQuery
                }
            }.let {  sqlQuery ->
                sortConfig?.let {
                    "$sqlQuery ORDER BY ${it.first.name} ${it.second.name}"
                } ?: sqlQuery
            }
    }

    abstract suspend fun runQuery(query: String, offset: Int, limit: Int) : Result<List<DataRow>>

}