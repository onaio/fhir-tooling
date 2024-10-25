package org.smartregister.fct.datatable.domain.model

import org.smartregister.fct.datatable.domain.feature.DTColumn

data class Data(
    val columns: List<DTColumn>,
    val rows: List<DataRow>
)
