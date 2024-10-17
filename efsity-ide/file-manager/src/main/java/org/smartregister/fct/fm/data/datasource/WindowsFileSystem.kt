package org.smartregister.fct.fm.data.datasource

import okio.Path
import okio.Path.Companion.toPath
import org.smartregister.fct.fm.domain.datasource.FileSystem
import org.smartregister.fct.fm.domain.model.Directory
import java.io.File
import javax.swing.filechooser.FileSystemView

internal class WindowsFileSystem : FileSystem {

    override fun defaultActivePath(): Path {
        return rootDirs().first().path
    }

    override fun rootDirs(): List<Directory> = File.listRoots().map {
        Directory(
            name = "${FileSystemView.getFileSystemView().getSystemTypeDescription(it)} (${it.toString().replace("\\", "")})",
            path = it.absolutePath.toPath()
        )
    }

}