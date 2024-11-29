package org.smartregister.fct.fm.data.datasource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import org.smartregister.fct.fm.domain.datasource.FileSystem
import org.smartregister.fct.fm.domain.model.Directory

internal class UnixFileSystem : FileSystem {

    override fun commonDirs(): List<Directory> = listOf(
        Directory(
            icon = Icons.Outlined.Home,
            name = "Home",
            path = home()
        )
    ) + super.commonDirs()

    override fun rootDirs(): List<Directory> = listOf()

}