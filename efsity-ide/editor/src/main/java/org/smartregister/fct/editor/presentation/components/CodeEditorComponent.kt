package org.smartregister.fct.editor.presentation.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.engine.data.enums.FileType
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.util.compactJson
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.prettyJson
import org.smartregister.fct.logger.FCTLogger

class CodeEditorComponent(
    componentContext: ComponentContext,
    text: String = "",
    fileType: FileType? = null,
    readOnly: Boolean = false,
) : InstanceKeeper.Instance, KoinComponent, ComponentContext by componentContext {

    private val appSettingManager: AppSettingManager by inject()
    private val appSetting = appSettingManager.appSetting

    private val _error = MutableStateFlow<String?>(null)
    internal val error: StateFlow<String?> = _error

    private val _info = MutableStateFlow<String?>(null)
    internal val info: StateFlow<String?> = _info

    private val _readOnly = MutableStateFlow(readOnly)
    internal val readOnly: StateFlow<Boolean> = _readOnly

    private val _fileType = MutableStateFlow(fileType)
    internal val fileType: StateFlow<FileType?> = _fileType

    private val _lineNumbers = MutableStateFlow(getLineNumbers(text))
    internal val lineNumbers: StateFlow<String> = _lineNumbers

    private val _textField = MutableStateFlow(TextFieldValue(AnnotatedString(text)))
    val textField: StateFlow<TextFieldValue> = _textField

    init {
        setText(text)
    }

    internal fun setTextField(textFieldValue: TextFieldValue) {
        componentScope.launch {
            _textField.emit(textFieldValue)
            _lineNumbers.emit(getLineNumbers(textFieldValue.text))
        }
    }

    fun setText(text: String) {
        if (text.trim().isNotEmpty()) {
            setTextField(
                TextFieldValue(
                    AnnotatedString(text),
                    selection = TextRange(0, 0)
                )
            )
        } else {
            setTextField(TextFieldValue(text))
        }
    }

    fun getText() = _textField.value.text

    fun setFileType(fileType: FileType?) {
        componentScope.launch {
            _fileType.emit(fileType)
        }
    }

    fun getFileType() = _fileType.value

    fun compactJson() {
        if (_textField.value.text.isNotEmpty() && _fileType.value == FileType.Json) {
            try {
                setText(_textField.value.text.compactJson())
            } catch (ex: Exception) {
                FCTLogger.e(ex)
                showError(ex.message)
            }
        }
    }

    fun formatJson() {
        if (_textField.value.text.isNotEmpty() && _fileType.value == FileType.Json) {
            try {
                setText(_textField.value.text.prettyJson(appSetting.codeEditorConfig.indent))
            } catch (ex: Exception) {
                FCTLogger.e(ex)
                showError(ex.message)
            }
        }
    }

    fun showInfo(text: String?) {
        componentScope.launch {
            _info.emit(text)
        }
    }

    fun showError(text: String?) {
        componentScope.launch {
            _error.emit(text)
        }
    }

    fun getAppSettings() = appSetting

    private fun getLineNumbers(text: String): String {
        return List(
            "\n".toRegex().findAll(text).toList().size + 1
        ) { index ->
            "${index + 1}"
        }.joinToString("\n")
    }
}