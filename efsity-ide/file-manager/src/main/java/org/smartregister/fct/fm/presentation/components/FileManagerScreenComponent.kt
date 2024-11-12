package org.smartregister.fct.fm.presentation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.Path
import org.smartregister.fct.common.presentation.component.ScreenComponent
import org.smartregister.fct.common.util.windowTitle
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.logger.FCTLogger

class FileManagerScreenComponent(
    componentContext: ComponentContext
) : ScreenComponent, ComponentContext by componentContext {

    private val _jsonContent = MutableStateFlow<String?>(null)
    val jsonContent: StateFlow<String?> = _jsonContent
    val error = MutableValue("")

    init {
        componentScope.launch {
            windowTitle.emit("File Manager")
        }
    }

    fun uploadResource(path: Path) {
        componentScope.launch {
            try {
                val fileContent = path.toFile().readText()
                _jsonContent.emit(fileContent)
            } catch (ex: Exception) {
                FCTLogger.e(ex)
                error.value = ex.message ?: "File parse error"
            }
        }
    }

    fun resetContent() {
        componentScope.launch {
            _jsonContent.emit(null)
        }
    }
}