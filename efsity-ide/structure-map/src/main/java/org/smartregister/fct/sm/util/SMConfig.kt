package org.smartregister.fct.sm.util

import okio.Path
import okio.Path.Companion.toPath
import org.smartregister.fct.engine.util.capitalizeWords
import org.smartregister.fct.fm.util.FileUtil
import org.smartregister.fct.sm.domain.model.SMModel
import java.io.File

internal object SMConfig {

    var activeStructureMap: SMModel? = null

    private val STRUCTURE_MAP_FILE_PATH =
        "${FileUtil.PRIVATE_ROOT}${File.separator}sm${File.separator}"

    fun getStructureMapDirPath(smId: String): String {
        return "$STRUCTURE_MAP_FILE_PATH$smId${File.separator}"
    }

    fun getStructureMapFilePath(smId: String): Path {
        return "${getStructureMapDirPath(smId)}${File.separator}structure_map.map".toPath()
    }

    fun getSourceFilePath(smId: String): Path {
        return "${getStructureMapDirPath(smId)}${File.separator}source.json".toPath()
    }

    fun getFileName(path: String): String {
        return FileUtil.getFilename(path).replace("_", " ").capitalizeWords()
    }

}