package org.smartregister.fct.serverconfig.presentation.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.lt.compose_views.menu_fab.MenuFabItem
import com.lt.compose_views.menu_fab.MenuFloatingActionButton
import fct.server_config.generated.resources.Res
import fct.server_config.generated.resources.export_configs
import fct.server_config.generated.resources.import_configs
import fct.server_config.generated.resources.new_config
import kotlinx.coroutines.launch
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.common.data.controller.SingleFieldDialogController
import org.smartregister.fct.serverconfig.presentation.components.ServerConfigPanelComponent
import org.smartregister.fct.serverconfig.util.asString

context (BoxScope, ServerConfigPanelComponent)
@Composable
internal fun MultiItemFloatingActionButton(
    titleDialogController: SingleFieldDialogController
) {

    val scope = rememberCoroutineScope()
    val menuItems = mutableStateListOf<MenuFabItem>().apply {
        add(CreateFabMenu(Res.string.new_config.asString(), Icons.Filled.Add))
        add(CreateFabMenu(Res.string.import_configs.asString(), Icons.Filled.Download))
        add(CreateFabMenu(Res.string.export_configs.asString(), Icons.Filled.Upload))
    }

    MenuFloatingActionButton(
        modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 24.dp),
        srcIcon = Icons.Outlined.Add,
        fabBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
        items = menuItems,
    ) {
        scope.launch {
            when (it.label) {
                Res.string.new_config.asString() -> titleDialogController.show()
                Res.string.import_configs.asString() -> showImportConfigDialog()
                Res.string.export_configs.asString() -> showExportConfigDialog()
            }
        }
    }
}

@Composable
private fun CreateFabMenu(
    label: String,
    icon: ImageVector,
) = MenuFabItem(
    fabBackgroundColor = MaterialTheme.colorScheme.tertiary,
    labelBackgroundColor = MaterialTheme.colorScheme.surface,
    labelTextColor = MaterialTheme.colorScheme.onSurface,
    label = label,
    icon = {
        Icon(
            modifier = Modifier.size(16.dp),
            icon = icon,
            tint = MaterialTheme.colorScheme.onTertiary
        )
    }
)