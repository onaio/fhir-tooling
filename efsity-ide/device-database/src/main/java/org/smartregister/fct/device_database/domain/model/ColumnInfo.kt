package org.smartregister.fct.device_database.domain.model

data class ColumnInfo internal constructor(
    val name: String,
    val type: String,
    val hasPrimaryKey: Boolean,
    val isNullable: Boolean
)
