package org.smartregister.fct.common.presentation.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.smartregister.fct.aurora.presentation.ui.components.getLottieFireComposition
import org.smartregister.fct.common.data.controller.LoaderDialogController
import androidx.compose.ui.window.Dialog as MatDialog

@Composable
fun rememberLoaderDialogController(
    message: String? = null,
    cancelable: Boolean = false,
    onShow: () -> Unit = {},
    onHide: () -> Unit = {}
): LoaderDialogController {

    val isShowDialog = remember { mutableStateOf(false) }

    val loaderDialogController = remember {
        LoaderDialogController(
            onShow = {
                isShowDialog.value = true
                onShow()
            },
            onHide = {
                isShowDialog.value = false
                onHide()
            }
        )
    }

    LoaderDialog(
        isShowDialog = isShowDialog,
        message = message,
        cancelable = cancelable,
    )

    return loaderDialogController
}

@Composable
internal fun LoaderDialog(
    isShowDialog: MutableState<Boolean>,
    message: String?,
    cancelable: Boolean,
) {

    if (isShowDialog.value) {
        MatDialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            onDismissRequest = { isShowDialog.value = !cancelable }
        ) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
            ) {

                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.height(80.dp),
                        painter = rememberLottiePainter(
                            composition = getLottieFireComposition(),
                            iterations = Compottie.IterateForever
                        ),
                        contentDescription = null
                    )

                    message?.run {
                        Spacer(Modifier.height(12.dp))
                        Text(message)
                    }

                }

            }
        }
    }
}