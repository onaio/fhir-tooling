package org.smartregister.fct.datatable.data.filtertype

import androidx.compose.ui.graphics.vector.ImageVector
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.JoinLeft
import org.smartregister.fct.datatable.domain.feature.DTFilterColumn
import org.smartregister.fct.datatable.domain.feature.DTFilterType

data object MatchStartFilterType : DTFilterType {

    override val label: String = "Match Start"
    override val icon: ImageVector = AuroraIconPack.JoinLeft

    override fun create(dtFilterColumn: DTFilterColumn): String {
        return "${dtFilterColumn.name} LIKE '${dtFilterColumn.value}%'"
    }
}