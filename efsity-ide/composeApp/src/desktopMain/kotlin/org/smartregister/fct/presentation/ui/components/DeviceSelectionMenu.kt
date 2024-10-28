package org.smartregister.fct.presentation.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.adb.domain.usecase.DeviceManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DeviceSelectionMenu() {

    val scope = rememberCoroutineScope()
    val devices by DeviceManager.getAllDevices().collectAsState(initial = listOf(null))
    var expanded by remember { mutableStateOf(false) }
    val selectedValue by DeviceManager.listenActiveDevice().collectAsState()

    val dropdownModifier =
        if (selectedValue == null) Modifier.width(230.dp) else Modifier.wrapContentSize()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = dropdownModifier.padding(0.dp).pointerHoverIcon(PointerIcon.Hand)
    ) {

        val colors = OutlinedTextFieldDefaults.colors()
        val interactionSource = remember { MutableInteractionSource() }

        BasicTextField(
            value = selectedValue.getInfo(),
            onValueChange = { /*type = it*/ },
            readOnly = true,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                lineHeight = TextUnit(10f, TextUnitType.Sp)
            ),
            enabled = false,
            modifier = dropdownModifier.menuAnchor().height(32.dp)
                .pointerHoverIcon(PointerIcon.Hand),
            decorationBox = @Composable { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = selectedValue.getInfo(),
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
            devices.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(text = selectionOption.getOptionName())
                    },
                    onClick = {
                        expanded = false
                        scope.launch { DeviceManager.setActiveDevice(selectionOption) }
                    }
                )
            }
        }

    }
}

private fun Device?.getOptionName(): String {
    return this?.getDeviceInfo()?.name ?: "No Device"
}

private fun Device?.getInfo(): String {
    return this
        ?.getDeviceInfo()
        ?.getAllBasicDetail() ?: "No Device"
}