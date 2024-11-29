package org.smartregister.fct.datatable.domain.feature

import org.smartregister.fct.datatable.data.filtertype.EqualFilterType
import org.smartregister.fct.datatable.data.filtertype.MatchAnyFilterType
import org.smartregister.fct.datatable.data.filtertype.MatchEndFilterType
import org.smartregister.fct.datatable.data.filtertype.MatchStartFilterType

interface DTFilterableType : DTFilterable {

    fun getFilterTypes(dtColumn: DTColumn): List<DTFilterType> = listOf(
        EqualFilterType,
        MatchStartFilterType,
        MatchAnyFilterType,
        MatchEndFilterType,
    )

    fun getDefaultFilterType(dtColumn: DTColumn): DTFilterType = EqualFilterType
}