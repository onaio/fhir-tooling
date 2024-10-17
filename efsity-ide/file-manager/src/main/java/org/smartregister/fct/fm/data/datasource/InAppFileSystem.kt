package org.smartregister.fct.fm.data.datasource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import okio.Path
import okio.Path.Companion.toPath
import org.smartregister.fct.fm.domain.datasource.FileSystem
import org.smartregister.fct.fm.domain.model.Directory

internal class InAppFileSystem : FileSystem {

    override fun home(): Path {
        return ROOT_PATH.toPath()
    }

    override fun commonDirs(): List<Directory> = listOf(
        Directory(
            icon = Icons.Outlined.Home,
            name = "Home",
            path = home()
        )
    )

    override fun rootDirs(): List<Directory> = listOf()

    companion object {
        const val ROOT_PATH = "root"
    }
}