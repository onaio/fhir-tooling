package org.smartregister.fct.common.presentation.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.smartregister.fct.common.data.controller.ConfirmationDialogController

private data class Content<T>(
    val title: String? = null,
    val message: String = "",
    val data: T? = null,
)

@Composable
fun <T> rememberConfirmationDialog(
    modifier: Modifier = Modifier,
    confirmButtonLabel: String = "Yes",
    cancelButtonLabel: String = "No",
    isCancellable: Boolean = true,
    icon: ImageVector? = null,
    onDismiss: ((ConfirmationDialogController<T>) -> Unit)? = null,
    onShow: ((ConfirmationDialogController<T>) -> Unit)? = null,
    onConfirmed: suspend CoroutineScope.(ConfirmationDialogController<T>, T?) -> Unit
): ConfirmationDialogController<T> {

    val isShowDialog = remember { mutableStateOf(false) }
    var content by remember { mutableStateOf(Content<T>()) }
    val scope = rememberCoroutineScope()

    val confirmationDialogController = remember {
        ConfirmationDialogController<T>(
            onShow = { title, message, data ->
                content = Content(
                    title = title,
                    message = message,
                    data = data
                )
                isShowDialog.value = true
                onShow?.invoke(this)
            },
            onHide = {
                isShowDialog.value = false
                onDismiss?.invoke(this)
            }
        )
    }

    ConfirmationDialog(
        isShowDialog = isShowDialog,
        controller = confirmationDialogController,
        modifier = modifier,
        title = content.title,
        message = content.message,
        confirmButtonLabel = confirmButtonLabel,
        cancelButtonLabel = cancelButtonLabel,
        isCancellable = isCancellable,
        icon = icon,
        onDismiss = onDismiss,
        onConfirmed = {
            scope.launch {
                onConfirmed(confirmationDialogController, content.data)
            }
        }
    )

    return confirmationDialogController
}

@Composable
fun <T> ConfirmationDialog(
    isShowDialog: MutableState<Boolean>,
    controller: ConfirmationDialogController<T>,
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String,
    confirmButtonLabel: String,
    cancelButtonLabel: String,
    isCancellable: Boolean,
    icon: ImageVector?,
    onDismiss: ((ConfirmationDialogController<T>) -> Unit)?,
    onConfirmed: (ConfirmationDialogController<T>) -> Unit
) {

    if (isShowDialog.value) {
        AlertDialog(
            modifier = modifier,
            icon = {
                icon?.run {
                    Icon(this, contentDescription = null)
                }

            },
            title = {
                title?.run {
                    Text(this)
                }

            },
            text = {
                Text(message)
            },
            onDismissRequest = {
                isShowDialog.value = !isCancellable
                if (isCancellable) onDismiss?.invoke(controller)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isShowDialog.value = false
                        onConfirmed(controller)
                    }
                ) {
                    Text(confirmButtonLabel)
                }
            },
            dismissButton = {
                if (isCancellable) {
                    TextButton(
                        onClick = {
                            isShowDialog.value = false
                            onDismiss?.invoke(controller)
                        }
                    ) {
                        Text(cancelButtonLabel)
                    }
                }
            }
        )
    }
}