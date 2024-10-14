package org.smartregister.fct.editor.presentation.ui.view

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.launch
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.editor.data.transformation.JsonTransformation
import org.smartregister.fct.editor.data.transformation.SMVisualTransformation
import org.smartregister.fct.editor.presentation.components.CodeEditorComponent
import org.smartregister.fct.editor.presentation.ui.components.Toolbar
import org.smartregister.fct.engine.data.enums.FileType
import org.smartregister.fct.engine.util.componentScope

@Composable
fun CodeEditor(
    modifier: Modifier = Modifier,
    component: CodeEditorComponent,
    toolbarOptions: (@Composable RowScope.() -> Unit)? = null,
    enableFileImport: Boolean = true,
    fetchFileImport: ((text: String) -> Unit)? = null,
    enableDBImport: Boolean = true,
    fetchDBImport: ((text: String) -> Unit)? = null,
) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(component) {
        focusRequester.requestFocus()
    }

    Aurora(component) {

        it.showSnackbar(component.info.collectAsState().value) {
            component.showInfo(null)
        }

        it.showErrorSnackbar(component.error.collectAsState().value) {
            component.showError(null)
        }

        Column(Modifier.fillMaxSize()) {
            Toolbar(
                component = component,
                toolbarOptions = toolbarOptions,
                enableFileImport = enableFileImport,
                fetchFileImport = fetchFileImport,
                enableDBImport = enableDBImport,
                fetchDBImport = fetchDBImport
            )
            Box(
                modifier = modifier.fillMaxSize()
            ) {
                val verticalScrollState = rememberScrollState()

                ConstraintLayout(Modifier.fillMaxSize()) {

                    val (lineNoColumn, editorRef) = createRefs()

                    Box(modifier = Modifier.widthIn(min = 30.dp).fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f))
                        .padding(horizontal = 5.dp, vertical = 4.dp).constrainAs(lineNoColumn) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }) {
                        Text(
                            modifier = Modifier.align(Alignment.TopStart)
                                .verticalScroll(verticalScrollState),
                            text = component.lineNumbers.collectAsState().value,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            ),
                        )
                    }

                    BasicTextField(
                        value = component.textField.collectAsState().value,
                        onValueChange = { textField ->
                            component.setTextField(textField)
                        },
                        readOnly = component.readOnly.collectAsState().value,
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .constrainAs(editorRef) {
                                start.linkTo(lineNoColumn.end)
                                top.linkTo(parent.top)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                                width = Dimension.fillToConstraints
                                height = Dimension.fillToConstraints
                            }
                            .padding(horizontal = 5.dp, vertical = 3.dp)
                            .pointerHoverIcon(PointerIcon.Text)
                            .onPreviewKeyEvent { keyEvent ->
                                when {
                                    keyEvent.isCtrlPressed && keyEvent.key == Key.F && keyEvent.type == KeyEventType.KeyUp -> {
                                        //showToolbox.value = true
                                        true
                                    }

                                    keyEvent.isCtrlPressed && keyEvent.isAltPressed && keyEvent.key == Key.K && keyEvent.type == KeyEventType.KeyUp -> {
                                        component.compactJson()
                                        true
                                    }

                                    keyEvent.isCtrlPressed && keyEvent.isAltPressed && keyEvent.key == Key.L && keyEvent.type == KeyEventType.KeyDown -> {
                                        component.formatJson()
                                        true
                                    }

                                    keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp -> {
                                        component.componentScope.launch {
                                            val currentLineNumber =
                                                getLine(component.textField.value)
                                            val lastLineNumber =
                                                getTotalNumberOfLine(component.textField.value)
                                            if (currentLineNumber >= lastLineNumber) {
                                                verticalScrollState.scrollTo(verticalScrollState.value + 20)
                                            }
                                        }
                                        true
                                    }

                                    else -> false
                                }
                            }
                            .verticalScroll(verticalScrollState),
                        visualTransformation = getTransformation(
                            fileType = component.fileType.collectAsState().value,
                            isDarkTheme = component.getAppSettings().isDarkTheme,
                            colorScheme = MaterialTheme.colorScheme
                        ),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontFamily = FontFamily.Monospace
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),

                        )

                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = verticalScrollState
                    )
                )
            }
        }
    }
}

fun getLine(textFieldValue: TextFieldValue): Int {
    val text = textFieldValue.text
    val selection = textFieldValue.selection.start
    val lineList = mutableListOf<Int>()
    text.forEachIndexed { index: Int, c: Char ->
        if (c == '\n') {
            lineList.add(index)
        }
    }

    if (lineList.isEmpty()) return 1

    lineList.forEachIndexed { index, lineEndIndex ->
        if (selection <= lineEndIndex) {
            return index + 1
        }
    }

    return lineList.size + 1
}

private fun getTotalNumberOfLine(textFieldValue: TextFieldValue): Int {
    return "\n".toRegex().findAll(textFieldValue.text).toList().size
}

private fun getTransformation(
    fileType: FileType?,
    isDarkTheme: Boolean,
    colorScheme: ColorScheme
): VisualTransformation {

    return when (fileType) {
        FileType.StructureMap -> SMVisualTransformation("", isDarkTheme, colorScheme)
        FileType.Json -> JsonTransformation("", isDarkTheme, colorScheme)
        else -> VisualTransformation.None
    }
}