package org.smartregister.fct.fm.domain.model

import org.smartregister.fct.fm.data.enums.FileSystemType

sealed class FileManagerMode(val activeDirPath: String?) {
    data class View(
        val defaultDirPath: String? = null,
        val onFileSelected: ((FileSystemType, String, String) -> Unit)? = null,
        val onPathSelected: ((FileSystemType, String, String) -> Unit)? = null,
        val extensions: List<String> = listOf(),
    ) : FileManagerMode(defaultDirPath)
    data class Edit(
        val defaultDirPath: String? = null,
    ) : FileManagerMode(defaultDirPath)
}