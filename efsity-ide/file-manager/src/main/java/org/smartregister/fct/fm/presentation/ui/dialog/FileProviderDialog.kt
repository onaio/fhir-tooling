package org.smartregister.fct.fm.presentation.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.aurora.presentation.ui.components.Tabs
import org.smartregister.fct.common.data.controller.DialogController
import org.smartregister.fct.common.presentation.ui.dialog.rememberDialog
import org.smartregister.fct.engine.data.enums.FileType
import org.smartregister.fct.fm.data.enums.FileSystemType
import org.smartregister.fct.fm.domain.model.FileManagerMode
import org.smartregister.fct.fm.presentation.ui.components.InAppFileManager
import org.smartregister.fct.fm.presentation.ui.components.SystemFileManager

@Composable
fun rememberFileProviderDialog(
    componentContext: ComponentContext,
    title: String = "File Provider",
    fileType: FileType? = null,
    onDismiss: ((DialogController<String>) -> Unit)? = null,
    onFilePath: ((FileSystemType, String, String) -> Unit)? = null,
    onFileContent: ((FileSystemType, String, String) -> Unit)? = null
): DialogController<String> {

    val dialogController = rememberDialog(
        width = 1200.dp,
        height = 800.dp,
        title = title,
        onDismiss = onDismiss,
    ) { controller, defaultDirPath ->

        FileProviderDialog(
            defaultDirPath = defaultDirPath,
            fileType = fileType,
            onFilePath = onFilePath,
            onFileContent = onFileContent,
            fileProviderController = controller,
            componentContext = componentContext
        )
    }

    return dialogController
}

@Composable
private fun FileProviderDialog(
    defaultDirPath: String? = null,
    fileType: FileType? = null,
    onFilePath: ((FileSystemType, String, String) -> Unit)? = null,
    onFileContent: ((FileSystemType, String, String) -> Unit)? = null,
    fileProviderController: DialogController<String>,
    componentContext: ComponentContext
) {

    val labelSystemFileManager = "System File Manager"
    val labelInAppFileManager = "In App File Manager"

    val onFileSelected = onFileContent?.let {
        val listener: (FileSystemType, String, String) -> Unit = { fileSystemType, dirPath, fileContent ->
            fileProviderController.hide()
            onFileContent.invoke(fileSystemType, dirPath, fileContent)
        }
        listener
    }

    val onPathSelected = onFilePath?.let {
        val listener: (FileSystemType, String, String) -> Unit = { fileSystemType, dirPath, filePath ->
            fileProviderController.hide()
            onFilePath.invoke(fileSystemType, dirPath, filePath)
        }
        listener
    }

    val mode = FileManagerMode.View(
        defaultDirPath = defaultDirPath,
        onFileSelected = onFileSelected,
        onPathSelected = onPathSelected,
        extensions = fileType?.extension?.let(::listOf) ?: listOf()
    )

    Tabs(
        tabs = listOf(
            labelSystemFileManager,
            labelInAppFileManager,
        ),
        title = { it },
        onSelected = { tabIndex, _ ->
            when (tabIndex) {
                0 -> SystemFileManager(componentContext, mode)

                1 -> InAppFileManager(
                    componentContext, mode
                )
            }
        }
    )
}