package org.smartregister.fct.datatable.domain.model

import org.smartregister.fct.datatable.domain.feature.DTFilterColumn
import org.smartregister.fct.datatable.domain.feature.DTFilterType

data class DataFilterTypeColumn(
    override val index: Int,
    override val name: String,
    override val sortable: Boolean,
    override val editable: Boolean,
    override val isPrimary: Boolean,
    override val value: String,
    val filterType: DTFilterType,
) : DTFilterColumn
