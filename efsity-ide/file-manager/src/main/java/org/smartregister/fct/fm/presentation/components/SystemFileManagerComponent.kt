package org.smartregister.fct.fm.presentation.components

import com.arkivanov.decompose.ComponentContext
import okio.Path
import org.smartregister.fct.fm.domain.datasource.FileSystem
import org.smartregister.fct.fm.domain.model.Applicable
import org.smartregister.fct.fm.domain.model.ContextMenu
import org.smartregister.fct.fm.domain.model.ContextMenuType
import org.smartregister.fct.fm.domain.model.FileManagerMode
import org.smartregister.fct.logger.FCTLogger

internal class SystemFileManagerComponent(
    componentContext: ComponentContext,
    fileSystem: FileSystem,
    mode: FileManagerMode
) :
    FileManagerComponent(componentContext, fileSystem, mode) {

    fun getRootDirs() = fileSystem.rootDirs()

    override fun getContextMenuList(): List<ContextMenu> {
        return when (mode) {
            is FileManagerMode.Edit -> listOf(
                ContextMenu(ContextMenuType.CopyToInternal, Applicable.Both),
                ContextMenu(ContextMenuType.Upload, Applicable.File()),
            )

            is FileManagerMode.View -> {
                listOf(
                    ContextMenu(
                        ContextMenuType.SelectFile,
                        Applicable.File(mode.extensions)
                    )
                )
            }
        }
    }

    override suspend fun onContextMenuClick(contextMenu: ContextMenu, path: Path): Result<Unit> {
        return when (contextMenu.menuType) {
            ContextMenuType.SelectFile -> onPathSelected(path)
            ContextMenuType.Upload -> {
                if (componentContext is FileManagerScreenComponent) {
                    componentContext.uploadResource(path)
                }
                return Result.success(Unit)
            }
            else -> {
                val error = "type ${contextMenu.menuType} is not handled"
                FCTLogger.w(error)
                Result.failure(IllegalStateException(error))
            }
        }
    }
}

