package org.smartregister.fct.datatable.data.filtertype

import androidx.compose.ui.graphics.vector.ImageVector
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.JoinRight
import org.smartregister.fct.datatable.domain.feature.DTFilterColumn
import org.smartregister.fct.datatable.domain.feature.DTFilterType

data object MatchEndFilterType : DTFilterType {

    override val label: String = "Match End"
    override val icon: ImageVector = AuroraIconPack.JoinRight

    override fun create(dtFilterColumn: DTFilterColumn): String {
        return "${dtFilterColumn.name} LIKE '%${dtFilterColumn.value}'"
    }
}