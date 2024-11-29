package org.smartregister.fct.datatable.domain.feature

interface DTPagination {

    fun setOffset(offset: Int)

    fun getOffset(): Int

    fun setLimit(newLimit: Int)

    fun getLimit(): Int

    suspend fun changeLimit(newLimit: Int)

    fun canGoFirstPage() : Boolean

    fun canGoPreviousPage() : Boolean

    fun canGoNextPage(): Boolean

    fun canGoLastPage(): Boolean

    suspend fun gotoFirstPage()

    suspend fun gotoPreviousPage()

    suspend fun gotoNextPage()

    suspend fun gotoLastPage()
}