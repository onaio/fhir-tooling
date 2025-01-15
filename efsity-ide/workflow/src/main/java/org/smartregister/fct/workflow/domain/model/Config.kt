package org.smartregister.fct.workflow.domain.model

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.Serializable

@Serializable
internal data class Config(
    val planDefinitionPath: String,
    val subjectPath: String,
    val otherResourcesPath: MutableList<String>
) {

    @kotlinx.serialization.Transient
    private var lastOpenPath: String? = null

    @kotlinx.serialization.Transient
    private val _observableOtherResourcePath = MutableSharedFlow<List<String>>()

    @kotlinx.serialization.Transient
    val observableOtherResourcePath: SharedFlow<List<String>> = _observableOtherResourcePath

    suspend fun addOtherResourcePath(path: String) {
        otherResourcesPath.add(path)
        _observableOtherResourcePath.emit(otherResourcesPath)
    }

    suspend fun removeOtherResourcePath(path: String) {
        _observableOtherResourcePath.emit(
            otherResourcesPath.apply {
                remove(path)
            }
        )
    }

    fun updateLastOpenPath(path: String?) {
        lastOpenPath = path
    }

    fun getLastOpenPath(): String {
        return lastOpenPath ?: planDefinitionPath
    }
}
