package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.launch
import org.smartregister.fct.aurora.util.pxToDp

@Composable
fun NumberDropDown(
    modifier: Modifier = Modifier,
    segments: List<Int>,
    placeholder: String = "",
    defaultSelectedIndex: Int? = null,
    defaultValue: Int? = null,
    key: Any? = null,
    errorHighlight: Boolean = true,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    onSelected: ((Int) -> Unit)? = null,
    onValueChanged: ((Int) -> Unit)? = null
) {

    require(min < max) {
        throw IllegalStateException("min value should be less than max value")
    }

    require(defaultSelectedIndex == null || defaultValue == null) {
        throw IllegalStateException("{defaultSelectedIndex} and {defaultValue} cannot be provided both at a time provide only one of them")
    }

    if (defaultSelectedIndex != null) {
        require(defaultSelectedIndex in 0..segments.size.minus(1)) {
            throw IndexOutOfBoundsException("{defaultSelectedIndex} should be greater than or equal to 0 and less than to {items}")
        }
    }

    if (defaultValue != null) {
        require(defaultValue in min..max) {
            throw IllegalStateException("defaultValue should be in range of min and max")
        }
    }

    segments.forEach {
        require(it in min..max) {
            throw IllegalStateException("all segment value should be in range of min and max")
        }
    }

    val scope = rememberCoroutineScope()
    var searchText by remember(key) { mutableStateOf(TextFieldValue(defaultValue?.toString() ?: "")) }
    var isError by remember(key) { mutableStateOf(false) }
    var expanded by remember(key) { mutableStateOf(false) }
    var textFieldWidth by remember(key) { mutableStateOf(0f) }
    val callOnInitialSelection = remember(key) { mutableStateOf(false) }

    if (!callOnInitialSelection.value) {
        callOnInitialSelection.value = true

        if (defaultSelectedIndex != null) {
            val item = segments[defaultSelectedIndex]
            searchText = TextFieldValue("$item")
        }

        if (defaultValue != null) {
            isError = !segments.any { it == defaultValue }
        }
    }

    Column {
        Box(
            modifier.height(38.dp).fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .onPreviewKeyEvent { keyEvent ->
                        when {
                            keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp -> {
                                onSelected?.invoke(searchText.text.toInt())
                                true
                            }
                            else -> false
                        }
                    }
                    .onGloballyPositioned { layoutCoordinates ->
                        textFieldWidth = layoutCoordinates.size.width.toFloat()
                    },
                value = searchText,
                onValueChange = {

                    if (it.text.trim() == "0") {
                        return@OutlinedTextField
                    }

                    if (it.text.trim().isEmpty()) {
                        searchText = it
                        return@OutlinedTextField
                    }

                    val value = it.text.toIntOrNull() ?: return@OutlinedTextField

                    searchText = if (value < min) {
                        TextFieldValue("$min", it.selection)
                    } else if (value > max) {
                        TextFieldValue("$max", it.selection)
                    } else {
                        it
                    }

                    scope.launch {
                        val isMatch = segments.any { item ->
                            searchText.text == "$item"
                        }

                        isError = searchText.text.trim().isNotEmpty() && !isMatch
                        onValueChanged?.invoke(searchText.text.toInt())
                    }
                },
                label = placeholder,
                isError = errorHighlight && isError
            )

            androidx.compose.material3.Button(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 4.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 4.dp
                ),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    expanded = !expanded
                },
                content = {
                    Icon(
                        icon = Icons.Outlined.ArrowDropDown
                    )
                }
            )
        }

        DropdownMenu(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .width(textFieldWidth.pxToDp())
                .requiredSizeIn(maxHeight = 400.dp),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            properties = PopupProperties(
                focusable = false
            )
        ) {

            segments.forEachIndexed { index, item ->
                DropdownMenuItem(
                    modifier = Modifier.height(40.dp),
                    text = {
                        Text(
                            text = "$item",
                            style = MaterialTheme.typography.titleSmall
                        )
                    },
                    onClick = {
                        searchText = TextFieldValue(
                            "$item",
                            selection = TextRange("$item".length)
                        )
                        expanded = false
                        isError = false
                        onValueChanged?.invoke(item)
                        onSelected?.invoke(item)
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                )
            }
        }
    }
}