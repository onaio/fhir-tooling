package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/*@Composable
fun OutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    readOnly: Boolean = false,
) {

    Mat3OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            label?.let { Text(text = label) }
        },
        readOnly = readOnly,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
    )
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    label: String? = null,
    //label: @Composable (() -> Unit)? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    //colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {

    val colors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
    )

    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {

        val textColorMethod = colors::class.members.first { it.name == "textColor" }
        val state: State<Color> = textColorMethod.call(
            colors,
            enabled,
            isError,
            interactionSource,
            currentComposer,
            0,
        ) as State<Color>
        state.value
        //colors.textColor(enabled, isError, interactionSource).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    val textSelectionColorsField = colors.javaClass.getDeclaredField("textSelectionColors")
    textSelectionColorsField.isAccessible = true
    val textSelectionColors = textSelectionColorsField.get(colors) as TextSelectionColors

    CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
        BasicTextField(
            value = value,
            modifier = if (label != null) {
                modifier
                    // Merge semantics at the beginning of the modifier chain to ensure padding is
                    // considered part of the text field.
                    .semantics(mergeDescendants = true) {}
            } else {
                modifier
            },
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            decorationBox = @Composable { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    placeholder = {
                        placeholder?.let {
                            Text(
                                text = placeholder,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                            )
                        }
                    },
                    label = label?.let {
                        {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    singleLine = singleLine,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    contentPadding = contentPadding,
                    container = {
                        OutlinedTextFieldDefaults.ContainerBox(
                            enabled,
                            isError,
                            interactionSource,
                            colors,
                            shape
                        )
                    }
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    //label: @Composable (() -> Unit)? = null,
    label: String? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    //colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    val colors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
    )

    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {

        val textColorMethod = colors::class.members.first { it.name == "textColor" }
        val state: State<Color> = textColorMethod.call(
            colors,
            enabled,
            isError,
            interactionSource,
            currentComposer,
            0,
        ) as State<Color>
        state.value
        //colors.textColor(enabled, isError, interactionSource).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    val textSelectionColorsField = colors.javaClass.getDeclaredField("textSelectionColors")
    textSelectionColorsField.isAccessible = true
    val textSelectionColors = textSelectionColorsField.get(colors) as TextSelectionColors

    CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
        BasicTextField(
            value = value,
            modifier = if (label != null) {
                modifier
                    // Merge semantics at the beginning of the modifier chain to ensure padding is
                    // considered part of the text field.
                    .semantics(mergeDescendants = true) {}
            } else {
                modifier
            },
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,

            decorationBox = @Composable { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value.text,
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    placeholder = placeholder,
                    label = label?.let {
                        {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    singleLine = singleLine,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    contentPadding = contentPadding,
                    container = {
                        OutlinedTextFieldDefaults.ContainerBox(
                            enabled,
                            isError,
                            interactionSource,
                            colors,
                            shape
                        )
                    }
                )
            }
        )
    }
}