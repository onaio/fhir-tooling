package org.smartregister.fct.device_database.ui.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.device_database.data.persistence.DeviceDBConfigPersistence
import org.smartregister.fct.device_database.domain.model.DBInfo
import org.smartregister.fct.device_database.domain.model.TableInfo

@Composable
internal fun SidePanel(
    listOfTables: List<TableInfo>,
    onTableDoubleClick: (TableInfo) -> Unit,
    isTablesLoading: Boolean,
    refreshTables: () -> Unit,
    openNewTab: () -> Unit,
    onDBSelected: (DBInfo) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
    ) {
        PanelOptions(
            refreshTables = refreshTables,
            openNewTab = openNewTab,
        )
        HorizontalDivider()
        DatabaseDropdown(
            onDBSelected = onDBSelected,
            initialSelected = DeviceDBConfigPersistence.sidePanelDBInfo
        )
        TablesList(listOfTables, onTableDoubleClick, isTablesLoading)
    }
}