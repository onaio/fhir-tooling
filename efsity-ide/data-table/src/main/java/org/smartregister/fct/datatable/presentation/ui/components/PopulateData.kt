package org.smartregister.fct.datatable.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.aurora.util.doubleClick
import org.smartregister.fct.datatable.data.controller.DataTableController
import org.smartregister.fct.datatable.domain.feature.DTColumn
import org.smartregister.fct.datatable.domain.feature.DTEditable
import org.smartregister.fct.datatable.domain.feature.DTPagination
import org.smartregister.fct.datatable.domain.model.DataCell
import org.smartregister.fct.datatable.domain.model.DataRow
import org.smartregister.fct.datatable.presentation.ui.view.serialNoCellWidth
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.text_viewer.ui.dialog.rememberTextViewerDialog

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun PopulateData(
    controller: DataTableController,
    componentContext: ComponentContext,
    columns: List<DTColumn>,
    columnWidthMapState: SnapshotStateMap<Int, Dp>,
    dataRowBGOdd: Color,
    dataRowBGEven: Color,
    dtWidth: Dp,
    customContextMenuItems: ((Int, DTColumn, Int, DataRow, DataCell) -> List<ContextMenuItem>)? = null
) {

    val scope = rememberCoroutineScope()
    val horizontalScrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current
    val state = rememberLazyListState()
    val textViewerDialog = rememberTextViewerDialog(componentContext)
    val textViewerDialogFormatted = rememberTextViewerDialog(componentContext, formatOnStart = true)
    val data by controller.records.collectAsState()
    var dataRowHover by remember { mutableStateOf(-1) }
    var dataCellHover by remember { mutableStateOf(-1) }
    val selectedRow by controller.selectedRowIndex.collectAsState()

    if (data.isNotEmpty() && data.first().data.isNotEmpty() && (data.first().data.size - 1) <= columns.size) {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.horizontalScroll(horizontalScrollState),
                state = state
            ) {

                itemsIndexed(data) { rowIndex, dataRow ->

                    val dataRowBG = if (dataRowHover == rowIndex) {
                        colorScheme.surfaceContainer
                    } else {
                        if (rowIndex % 2 == 0) dataRowBGEven else dataRowBGOdd
                    }

                    var rowModifier = Modifier.height(40.dp).background(dataRowBG)

                    if (selectedRow == rowIndex) {
                        rowModifier = rowModifier.border(border = BorderStroke(
                            width = 1.dp,
                            color = colorScheme.onSurface.copy(0.4f)
                        ))
                    }

                    Row(
                        modifier = rowModifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(serialNoCellWidth)
                                .background(colorScheme.surfaceContainer),
                        ) {

                            val serialNo = if (controller is DTPagination) {
                                controller.getOffset() + rowIndex + 1
                            } else {
                                rowIndex + 1
                            }

                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "$serialNo"
                            )

                            DTVerticalDivider(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd),
                                alpha = 0.5f
                            )
                        }

                        dataRow.data.forEachIndexed { cellIndex, dataCell ->

                            val dataCellBorder =
                                if (rowIndex == dataRowHover && cellIndex == dataCellHover) BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                ) else null

                            DataBox(
                                modifier = Modifier
                                    .onPointerEvent(PointerEventType.Enter) {
                                        dataRowHover = rowIndex
                                        dataCellHover = cellIndex
                                    }
                                    .onPointerEvent(PointerEventType.Exit) {
                                        dataRowHover = -1
                                        dataCellHover = -1
                                    },
                                index = dataCell.index,
                                columnWidthMapState = columnWidthMapState,
                                enableDivider = cellIndex == columns.size.minus(1)
                            ) {


                                val editMode = remember { mutableStateOf(false) }

                                val text = dataCell.data?.let {
                                    if (it.length > 100) {
                                        "${it.substring(0, 100)}..."
                                    } else it
                                } ?: "NULL"

                                if (editMode.value) {
                                    if (controller is DTEditable) {
                                        DataCellTextField(
                                            editMode = editMode,
                                            controller = controller,
                                            placeholder = "",
                                            dataCell = dataCell,
                                            dataRow = dataRow,
                                            dataRows = data,
                                        )
                                    }
                                } else {
                                    Surface(
                                        modifier = Modifier.fillMaxSize().background(Color.Transparent),
                                        border = dataCellBorder,
                                        color = Color.Transparent
                                    ) {
                                        Tooltip(
                                            modifier = Modifier.fillMaxSize(),
                                            tooltip = text,
                                            tooltipPosition = TooltipPosition.Top(space = 10),
                                        ) {

                                            val extraContextMenuItems = try {
                                                customContextMenuItems?.invoke(
                                                    dataCell.index,
                                                    columns[dataCell.index],
                                                    rowIndex,
                                                    dataRow,
                                                    dataCell
                                                ) ?: listOf()
                                            } catch (ex: Exception) {
                                                FCTLogger.e(ex)
                                                listOf()
                                            }

                                            ContextMenuArea(
                                                items = {
                                                    scope.launch {
                                                        controller.updateSelectedRowIndex(rowIndex)
                                                    }
                                                    listOf(
                                                        ContextMenuItem("Copy") {
                                                            clipboardManager.setText(
                                                                AnnotatedString(dataCell.data ?: "")
                                                            )
                                                        },
                                                        ContextMenuItem("View") {
                                                            if (columns[dataCell.index].name == "serializedResource") {
                                                                textViewerDialogFormatted.show(dataCell.data ?: "")
                                                            } else {
                                                                textViewerDialog.show(dataCell.data ?: "")
                                                            }
                                                        },
                                                    ) + extraContextMenuItems
                                                },
                                            ) {
                                                Text(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clickable {
                                                            scope.launch {
                                                                controller.updateSelectedRowIndex(rowIndex)
                                                            }
                                                        }
                                                        .doubleClick(scope) {
                                                            scope.launch {
                                                                controller.updateSelectedRowIndex(rowIndex)
                                                            }
                                                            editMode.value =
                                                                dataCell.editable && controller is DTEditable
                                                        }
                                                        .padding(horizontal = 8.dp),
                                                    text = text,
                                                    softWrap = false,
                                                    lineHeight = 32.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (rowIndex == data.size.minus(1)) {
                        DTHorizontalDivider(dtWidth)
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )

            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                adapter = rememberScrollbarAdapter(
                    scrollState = horizontalScrollState
                )
            )
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DataCellTextField(
    modifier: Modifier = Modifier,
    editMode: MutableState<Boolean>,
    controller: DTEditable,
    placeholder: String,
    dataCell: DataCell,
    dataRow: DataRow,
    dataRows: List<DataRow>
) {

    val scope = rememberCoroutineScope()
    val focusRequester = remember(dataCell) { FocusRequester() }
    var editText by remember(dataCell) {
        mutableStateOf(
            TextFieldValue(
                text = dataCell.data ?: "",
            )
        )
    }

    LaunchedEffect(dataCell) {
        editText = editText.copy(
            selection = TextRange(0, editText.text.length)
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize().background(Color.Transparent),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        ),
        color = Color.Transparent
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxSize()
                .onFocusChanged {
                    editMode.value = it.isFocused
                }
                .focusRequester(focusRequester)
                .onGloballyPositioned {
                    focusRequester.requestFocus()
                }
                .onPreviewKeyEvent { keyEvent ->
                    when {
                        keyEvent.key == Key.Escape && keyEvent.type == KeyEventType.KeyUp -> {
                            editMode.value = false
                            true
                        }

                        keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp -> {
                            editMode.value = false
                            if (editText.text != dataCell.data) {
                                controller.update(
                                    dataCell.copy(
                                        data = editText.text
                                    ),
                                    dataRow,
                                    dataRows
                                )
                            }
                            true
                        }

                        else -> false
                    }
                },
            value = editText,
            onValueChange = { textFieldValue ->
                editText = textFieldValue
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                color = MaterialTheme.colorScheme.onSurface,
            ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            decorationBox = @Composable { innerTextField ->
                // places leading icon, text field with label and placeholder, trailing icon
                TextFieldDefaults.DecorationBox(
                    value = editText.text,
                    visualTransformation = VisualTransformation.None,
                    innerTextField = innerTextField,
                    placeholder = {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                        )
                    },
                    label = null,
                    leadingIcon = null,
                    trailingIcon = null,
                    prefix = null,
                    suffix = null,
                    supportingText = null,
                    shape = TextFieldDefaults.shape,
                    singleLine = true,
                    enabled = true,
                    isError = false,
                    interactionSource = remember { MutableInteractionSource() },
                    contentPadding = PaddingValues(8.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        )
    }

}