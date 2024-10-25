package org.smartregister.fct.datatable.data.filtertype

import androidx.compose.ui.graphics.vector.ImageVector
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Equal
import org.smartregister.fct.datatable.domain.feature.DTFilterColumn
import org.smartregister.fct.datatable.domain.feature.DTFilterType

data object EqualFilterType : DTFilterType {

    override val label: String = "Equal"
    override val icon: ImageVector = AuroraIconPack.Equal

    override fun create(dtFilterColumn: DTFilterColumn): String {
        return "${dtFilterColumn.name} = '${dtFilterColumn.value}'"
    }
}