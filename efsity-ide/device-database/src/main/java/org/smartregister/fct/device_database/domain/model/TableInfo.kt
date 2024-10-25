package org.smartregister.fct.device_database.domain.model

internal data class TableInfo(
    val name: String,
    val columnsInfo: List<ColumnInfo>
)
