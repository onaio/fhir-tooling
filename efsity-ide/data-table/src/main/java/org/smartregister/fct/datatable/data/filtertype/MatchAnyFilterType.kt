package org.smartregister.fct.datatable.data.filtertype

import androidx.compose.ui.graphics.vector.ImageVector
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.JoinInner
import org.smartregister.fct.datatable.domain.feature.DTFilterColumn
import org.smartregister.fct.datatable.domain.feature.DTFilterType

data object MatchAnyFilterType : DTFilterType {

    override val label: String = "Match Any"
    override val icon: ImageVector = AuroraIconPack.JoinInner

    override fun create(dtFilterColumn: DTFilterColumn): String {
        return "${dtFilterColumn.name} LIKE '%${dtFilterColumn.value}%'"
    }
}