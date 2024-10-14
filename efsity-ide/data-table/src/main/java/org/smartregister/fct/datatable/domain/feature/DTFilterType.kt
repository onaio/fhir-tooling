package org.smartregister.fct.datatable.domain.feature

import androidx.compose.ui.graphics.vector.ImageVector

interface DTFilterType {
    val label: String
    val icon: ImageVector
    fun create(dtFilterColumn: DTFilterColumn) : String
}
