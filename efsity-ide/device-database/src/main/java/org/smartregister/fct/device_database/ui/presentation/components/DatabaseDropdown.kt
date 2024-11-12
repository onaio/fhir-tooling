package org.smartregister.fct.device_database.ui.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.Dropdown
import org.smartregister.fct.device_database.data.persistence.DeviceDBConfigPersistence
import org.smartregister.fct.device_database.domain.model.DBInfo

@Composable
internal fun DatabaseDropdown(
    modifier: Modifier = Modifier,
    onDBSelected: (DBInfo) -> Unit,
    initialSelected: DBInfo? = null,
) {

    Box(modifier.padding(8.dp)) {
        Dropdown(
            options = DeviceDBConfigPersistence.listOfDB,
            label = { it.label },
            onSelected = onDBSelected,
            initialSelected = initialSelected
        )
    }
}