package org.smartregister.fct.datatable.domain.model

import org.smartregister.fct.datatable.domain.feature.DTColumn

data class DataCell(
    val index: Int,
    val data: String?,
    val editable: Boolean,
    val column: DTColumn,
)
