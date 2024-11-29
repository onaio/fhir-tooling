package org.smartregister.fct.datatable.domain.model

import org.smartregister.fct.datatable.domain.feature.DTFilterColumn

data class DataFilterColumn(
    override val index: Int,
    override val name: String,
    override val sortable: Boolean,
    override val editable: Boolean,
    override val isPrimary: Boolean,
    override val value: String,
) : DTFilterColumn
