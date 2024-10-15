package org.smartregister.fct.datatable.domain.feature

interface DTFilterable {

    suspend fun applyFilter(dataColumnFilter: List<DTFilterColumn>)
}