package org.smartregister.fct.datatable.domain.feature

interface DTRefreshable {

    suspend fun refreshData()
}