package org.smartregister.fct.logcat.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import org.smartregister.fct.logger.model.LogLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LogLevelFilterMenu(
    logLevelFilter: MutableState<LogLevel?>
) {

    var expanded by remember { mutableStateOf(false) }

    val options = LogLevel
        .entries
        .toMutableList<LogLevel?>()
        .apply {
            add(0, null)
        }
        .toList()

    var selectedValue by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(300.dp).padding(0.dp).pointerHoverIcon(PointerIcon.Hand)
    ) {

        val colors = OutlinedTextFieldDefaults.colors()
        val interactionSource = remember { MutableInteractionSource() }

        BasicTextField(
            value = selectedValue.getOptionName(),
            onValueChange = { },
            readOnly = true,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                lineHeight = TextUnit(10f, TextUnitType.Sp)
            ),
            enabled = false,
            modifier = Modifier.menuAnchor().fillMaxWidth().height(32.dp)
                .pointerHoverIcon(PointerIcon.Hand),
            decorationBox = @Composable { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = selectedValue.getOptionName(),
                    visualTransformation = VisualTransformation.None,
                    innerTextField = innerTextField,
                    //placeholder = placeholder,
                    //label = label,
                    //leadingIcon = leadingIcon,
                    contentPadding = PaddingValues(start = 8.dp),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    //prefix = prefix,
                    //suffix = suffix,
                    //supportingText = supportingText,
                    //singleLine = singleLine,
                    enabled = true,
                    singleLine = true,
                    //isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    container = {
                        OutlinedTextFieldDefaults.ContainerBox(
                            enabled = true,
                            isError = false,
                            interactionSource = interactionSource,
                            colors,
                            OutlinedTextFieldDefaults.shape,
                        )
                    }
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(text = selectionOption.getOptionName())
                    },
                    onClick = {
                        expanded = false
                        selectedValue = selectionOption
                        logLevelFilter.value = selectionOption
                    }
                )
            }
        }

    }
}

private fun LogLevel?.getOptionName(): String {
    return this?.name ?: "No Filter"
}