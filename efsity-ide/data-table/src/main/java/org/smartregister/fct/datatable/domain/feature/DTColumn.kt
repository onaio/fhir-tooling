package org.smartregister.fct.datatable.domain.feature

interface DTColumn {
    val index: Int
    val name: String
    val sortable: Boolean
    val editable: Boolean
    val isPrimary: Boolean
}