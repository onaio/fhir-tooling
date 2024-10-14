package org.smartregister.fct.common.presentation.ui.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.smartregister.fct.aurora.presentation.ui.components.TextButton
import org.smartregister.fct.common.data.controller.SingleFieldDialogController
import java.util.UUID

typealias TextFieldValidation = (String) -> Pair<Boolean, String>

@Composable
fun rememberSingleFieldDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    okButtonLabel: String = "OK",
    cancelButtonLabel: String = "Cancel",
    isCancellable: Boolean = true,
    placeholder: String = "",
    maxLength: Int = 40,
    validations: List<TextFieldValidation> = listOf(),
    key: Any? = UUID.randomUUID().toString(),
    onResult: suspend CoroutineScope.(String, SingleFieldDialogController) -> Unit
): SingleFieldDialogController {

    val isShowDialog = remember { mutableStateOf(false) }
    val input = remember { mutableStateOf("") }

    val singleFieldDialogController = remember {
        SingleFieldDialogController {
            input.value = ""
            isShowDialog.value = true
        }
    }

    SingleFieldDialog(
        singleFieldDialogController = singleFieldDialogController,
        isShowDialog = isShowDialog,
        input = input,
        modifier = modifier,
        title = title,
        okButtonLabel = okButtonLabel,
        cancelButtonLabel = cancelButtonLabel,
        isCancellable = isCancellable,
        placeholder = placeholder,
        maxLength = maxLength,
        validations = validations,
        key = key,
        onResult = onResult
    )

    return singleFieldDialogController
}

@Composable
internal fun SingleFieldDialog(
    singleFieldDialogController: SingleFieldDialogController,
    isShowDialog: MutableState<Boolean>,
    input: MutableState<String>,
    modifier: Modifier = Modifier,
    title: String? = null,
    okButtonLabel: String = "OK",
    cancelButtonLabel: String = "Cancel",
    isCancellable: Boolean = true,
    placeholder: String = "",
    maxLength: Int = 40,
    validations: List<TextFieldValidation>,
    key: Any? = UUID.randomUUID().toString(),
    onResult: suspend CoroutineScope.(String, SingleFieldDialogController) -> Unit
) {

    val isError = remember { mutableStateOf(false) }
    val errorText = remember { mutableStateOf("") }
    val focusResult = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    if (isShowDialog.value) {
        AlertDialog(
            modifier = modifier.width(400.dp),
            title = {
                title?.run {
                    Text(this)
                }
            },
            text = {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(focusResult),
                    value = input.value,
                    onValueChange = {
                        if (it.length <= maxLength) input.value = it
                        isError.value = input.value.trim().isEmpty()
                        errorText.value = ""
                        checkErrors(
                            text = input.value,
                            validations = validations,
                            isError = isError,
                            errorText = errorText,
                        )
                    },
                    isError = isError.value,
                    supportingText = {
                        if (isError.value) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = errorText.value,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true,
                    placeholder = {
                        Text(placeholder)
                    }
                )
            },
            onDismissRequest = {
                isShowDialog.value = !isCancellable
            },
            confirmButton = {
                TextButton(
                    label = okButtonLabel,
                    enable = input.value.isNotEmpty() && errorText.value.isEmpty() && !isError.value,
                    onClick = {
                        isShowDialog.value = false
                        scope.launch {
                            onResult(input.value, singleFieldDialogController)
                        }
                    }
                )
            },
            dismissButton = {
                if (isCancellable) {
                    TextButton(
                        label = cancelButtonLabel,
                        onClick = {
                            isShowDialog.value = false
                        }
                    )
                }
            }
        )
        scope.launch {
            focusResult.requestFocus()
        }
    }
}

private fun checkErrors(
    text: String,
    validations: List<TextFieldValidation>,
    errorText: MutableState<String>,
    isError: MutableState<Boolean>
) {
    if (text.isNotEmpty()) {

        validations.forEach { validation ->
            val result = validation(text)
            if (!result.first) {
                errorText.value = result.second
                isError.value = true
                return@forEach
            }
        }
    }
}

