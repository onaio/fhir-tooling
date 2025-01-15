package org.smartregister.fct.fm.util

import okio.Path
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.smartregister.fct.logger.FCTLogger
import java.nio.charset.Charset

object FileUtil {

    fun getFileExtension(path: String): String {
        return FilenameUtils.getExtension(path)
    }

    fun getFilename(path: String) : String {
        return FilenameUtils.getBaseName(path)
    }

    fun writeFile(path: Path, data: String = "") {
        FileUtils.write(path.toFile(), data, Charset.defaultCharset())
    }

    fun readFile(path: Path) : String {
        return FileUtils.readFileToString(path.toFile(), Charset.defaultCharset())
    }

    fun deleteFolder(path: Path) {
        FileUtils.deleteDirectory(path.toFile())
    }

    fun deleteFile(path: Path) {
        try {
            FileUtils.delete(path.toFile())
        } catch (ex: Exception) {
            FCTLogger.e(ex)
        }
    }

    fun privateRootListFiles() : List<Path> {
        val privatePath = PRIVATE_ROOT.toPath()

        if (!privatePath.toFile().exists()) {
           privatePath.toFile().mkdir()
        }

        return listFiles(privatePath, true)
    }

    fun listFiles(path: Path, createIfNotFound: Boolean = false): List<Path> {

        if (!path.toFile().exists()) {

            if (createIfNotFound) {
                path.toFile().mkdirs()
            } else {
                return listOf()
            }
        }

        return FileUtils.listFiles(path.toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).map {
            it.toOkioPath()
        }
    }

    const val PRIVATE_ROOT = "private"
}