package org.smartregister.fct.fm

import okio.Path.Companion.toPath
import org.koin.core.context.GlobalContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.smartregister.fct.engine.setup.ModuleSetup
import org.smartregister.fct.engine.util.Platform
import org.smartregister.fct.engine.util.PlatformType
import org.smartregister.fct.fm.data.communication.InterCommunication
import org.smartregister.fct.fm.data.datasource.InAppFileSystem
import org.smartregister.fct.fm.data.datasource.MacFileSystem
import org.smartregister.fct.fm.data.datasource.UnixFileSystem
import org.smartregister.fct.fm.data.datasource.WindowsFileSystem
import org.smartregister.fct.fm.domain.datasource.FileSystem
import org.smartregister.fct.logger.FCTLogger

class FileManagerModuleSetup : ModuleSetup {

    private val fileManagerModule = module {
        single<FileSystem>(createdAtStart = true) {
            val platform = Platform.getPlatform()
            when (platform) {
                PlatformType.Mac -> {
                    MacFileSystem()
                }
                PlatformType.Windows -> {
                    WindowsFileSystem()
                }
                else -> {
                    UnixFileSystem()
                }
            }
        }
        single<FileSystem>(named("inApp"), true) { InAppFileSystem() }
        single<InterCommunication> { InterCommunication() }
    }

    override suspend fun setup() {
        FCTLogger.d("Loading... File Manager Module")
        checkAndCreateRootDir()
        GlobalContext.get().loadModules(listOf(fileManagerModule))
        FCTLogger.d("File Manager Module Loaded")
    }

    private fun checkAndCreateRootDir() {
        val okioFileSystem = okio.FileSystem.SYSTEM
        val rootPath = InAppFileSystem.ROOT_PATH.toPath()

        if (!okioFileSystem.exists(rootPath)) {
            okioFileSystem.createDirectory(rootPath)
        }
    }
}