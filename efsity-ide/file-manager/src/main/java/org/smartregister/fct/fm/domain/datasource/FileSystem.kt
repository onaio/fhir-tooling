package org.smartregister.fct.fm.domain.datasource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.SmartDisplay
import okio.Path.Companion.toPath
import org.smartregister.fct.fm.domain.model.Directory
import java.io.File

internal interface FileSystem {

    fun home() = System.getProperty("user.home").toPath()
    fun downloads() = "${home()}${File.separator}Downloads".toPath()
    fun documents() = "${home()}${File.separator}Documents".toPath()
    fun music() = "${home()}${File.separator}Music".toPath()
    fun pictures() = "${home()}${File.separator}Pictures".toPath()
    fun videos() = "${home()}${File.separator}Videos".toPath()
    fun defaultActivePath() = home()

    fun commonDirs(): List<Directory> = listOf(
        Directory(
            icon = Icons.Outlined.Download,
            name = "Downloads",
            path = downloads()
        ),
        Directory(
            icon = Icons.Outlined.Description,
            name = "Documents",
            path = documents()
        ),
        Directory(
            icon = Icons.Outlined.MusicNote,
            name = "Music",
            path = music()
        ),
        Directory(
            icon = Icons.Outlined.Photo,
            name = "Pictures",
            path = pictures()
        ),
        Directory(
            icon = Icons.Outlined.SmartDisplay,
            name = "Videos",
            path = videos()
        ),
    )

    fun rootDirs(): List<Directory>
}