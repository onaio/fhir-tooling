package org.smartregister.fct.sm.domain.model


internal data class SMModel(
    val id: String,
    val name: String,
    val mapPath: String,
    val sourcePath: String
) {
    @kotlinx.serialization.Transient
    private var lastOpenPath: String? = null

    fun updateLastOpenPath(path: String?) {
        lastOpenPath = path
    }

    fun getLastOpenPath(): String {
        return lastOpenPath ?: mapPath
    }
}
