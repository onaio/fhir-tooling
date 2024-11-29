package org.smartregister.fct.datatable.domain.feature

import org.smartregister.fct.datatable.domain.model.DataCell
import org.smartregister.fct.datatable.domain.model.DataRow

interface DTEditable {
    fun update(dataCell: DataCell, dataRow: DataRow, dataRows: List<DataRow>)
}