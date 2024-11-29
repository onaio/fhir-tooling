package org.smartregister.fct.serverconfig.presentation.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.engine.domain.model.ServerConfig
import org.smartregister.fct.serverconfig.presentation.components.ConfigDialogComponent
import org.smartregister.fct.serverconfig.presentation.components.ServerConfigPanelComponent

context (ConfigDialogComponent, ServerConfigPanelComponent, BoxScope)
@Composable
internal fun ImportExportConfigList(configs: List<ServerConfig>) {

    // TODO add scrollbar indicator
    LazyColumn(Modifier.fillMaxSize()) {

        items(configs) { item ->

            var isChecked by remember { mutableStateOf(false) }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = it
                        addOrRemoveConfig(it, item)
                    }
                )
                Spacer(Modifier.width(8.dp))
                Text(item.title)
            }
        }
    }
}