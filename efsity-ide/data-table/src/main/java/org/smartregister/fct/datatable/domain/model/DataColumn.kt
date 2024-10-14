package org.smartregister.fct.datatable.domain.model

import org.smartregister.fct.datatable.domain.feature.DTColumn

data class DataColumn(
    override val index: Int,
    override val name: String,
    override val sortable: Boolean,
    override val editable: Boolean,
    override val isPrimary: Boolean
) : DTColumn