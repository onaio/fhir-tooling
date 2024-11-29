package org.smartregister.fct.pm.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchPackage(searchText: MutableState<String>) {

    val bgColor = MaterialTheme.colorScheme.surfaceContainer
    val fgColor = MaterialTheme.colorScheme.onSurface

    BasicTextField(
        value = searchText.value,
        modifier = Modifier.fillMaxWidth(),
        onValueChange = { searchText.value = it},
        textStyle = TextStyle(
            color = fgColor,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        ),
        cursorBrush = SolidColor(fgColor),
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = searchText.value,
                visualTransformation = VisualTransformation.None,
                innerTextField = innerTextField,
                placeholder = {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                enabled = true,
                isError = false,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = bgColor,
                    unfocusedContainerColor = bgColor,
                    cursorColor = fgColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                interactionSource = remember { MutableInteractionSource() },
                contentPadding = PaddingValues(8.dp),
            )
        }
    )

    HorizontalDivider()
}