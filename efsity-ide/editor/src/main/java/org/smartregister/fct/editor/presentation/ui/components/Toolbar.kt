package org.smartregister.fct.editor.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.automirrored.outlined.Segment
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Database
import org.smartregister.fct.aurora.auroraiconpack.FolderOpen
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.device_database.ui.presentation.dialog.rememberDBDataProviderDialog
import org.smartregister.fct.editor.presentation.components.CodeEditorComponent
import org.smartregister.fct.editor.util.EditorConfig
import org.smartregister.fct.engine.data.enums.FileType
import org.smartregister.fct.fm.data.enums.FileSystemType
import org.smartregister.fct.fm.presentation.ui.dialog.rememberFileProviderDialog


@Composable
internal fun Toolbar(
    component: CodeEditorComponent,
    toolbarOptions: (@Composable RowScope.() -> Unit)? = null,
    enableFileImport: Boolean = true,
    fetchFileImport: ((text: String) -> Unit)? = null,
    enableDBImport: Boolean = true,
    fetchDBImport: ((text: String) -> Unit)? = null,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(39.dp)
            .background(MaterialTheme.colorScheme.surface.copy(0.8f))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (component.fileType.collectAsState().value == FileType.Json) {
            CompactJsonIconButton(component)
            Spacer(Modifier.width(8.dp))
            FormatJsonIconButton(component)
            Spacer(Modifier.width(8.dp))
        }

        CopyAllIconButton(component)
        Spacer(Modifier.width(8.dp))

        if (enableFileImport || fetchFileImport != null) {
            ImportFromFileSystem(component, fetchFileImport)
            Spacer(Modifier.width(8.dp))
        }

        if (enableDBImport || fetchDBImport != null) {
            ImportFromDatabase(component, fetchDBImport)
            Spacer(Modifier.width(8.dp))
        }

        toolbarOptions?.invoke(this)
    }
    HorizontalDivider()
}

@Composable
private fun CompactJsonIconButton(component: CodeEditorComponent) {
    Tooltip(
        tooltip = "Compact JSON\nCtrl+Alt+K",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            icon = Icons.AutoMirrored.Outlined.Notes,
            onClick = {
                component.compactJson()
            }
        )
    }
}

@Composable
private fun FormatJsonIconButton(component: CodeEditorComponent) {
    Tooltip(
        tooltip = "Format JSON\nCtrl+Alt+L",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            icon = Icons.AutoMirrored.Outlined.Segment,
            onClick = {
                component.formatJson()
            }
        )
    }
}

@Composable
private fun CopyAllIconButton(component: CodeEditorComponent) {
    val clipboardManager = LocalClipboardManager.current

    Tooltip(
        tooltip = "Copy All Content",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            iconModifier = Modifier.height(16.dp),
            icon = Icons.Outlined.ContentCopy,
            onClick = {
                clipboardManager.setText(AnnotatedString(component.getText()))
                component.showInfo("Content copied")
            }
        )
    }
}

@Composable
private fun ImportFromFileSystem(
    component: CodeEditorComponent,
    fetchFileImport: ((text: String) -> Unit)? = null,
) {

    val fileProviderDialog = rememberFileProviderDialog(
        componentContext = component,
        title = "Import",
        onFileContent = { fileSystemType, dirPath, fileContent ->
            if (fileSystemType == FileSystemType.System) {
                EditorConfig.activePath = dirPath
            }
            if (fetchFileImport != null) {
                fetchFileImport(fileContent)
            } else {
                component.setText(fileContent)
            }
        }
    )

    Tooltip(
        tooltip = "Import From File System",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            icon = AuroraIconPack.FolderOpen,
            onClick = {
                fileProviderDialog.show(EditorConfig.activePath)
            }
        )
    }
}

@Composable
private fun ImportFromDatabase(
    component: CodeEditorComponent,
    fetchDBImport: ((text: String) -> Unit)? = null,
) {

    val dbDataProviderDialog = rememberDBDataProviderDialog(
        componentContext = component,
        title = "Import",
        defaultQuery = "SELECT * FROM ResourceEntity LIMIT 1",
    ) {

        if (fetchDBImport != null) {
            fetchDBImport(it)
        } else {
            component.setText(it)
        }
    }

    Tooltip(
        tooltip = "Import From Database",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            icon = AuroraIconPack.Database,
            onClick = {
                dbDataProviderDialog.show()
            }
        )
    }
}