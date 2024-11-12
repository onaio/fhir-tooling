package org.smartregister.fct.common.presentation.ui.dialog


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.common.data.controller.DialogController
import androidx.compose.ui.window.Dialog as MatDialog

enum class DialogType {
    Default, Error
}

@Composable
fun <T> rememberDialog(
    title: String,
    width: Dp = 300.dp,
    height: Dp? = null,
    cancelable: Boolean = true,
    cancelOnTouchOutside: Boolean = true,
    dialogType: DialogType = DialogType.Default,
    onDismiss: (DialogController<T>.() -> Unit)? = null,
    key: Any? = null,
    content: @Composable (ColumnScope.(DialogController<T>, T?) -> Unit)
): DialogController<T> {

    val scope = rememberCoroutineScope()

    val dialogController = remember(key) {
        DialogController<T>(
            onShow = {

                scope.launch {
                    data.emit(it)
                    isShowDialog.emit(true)
                }
            },
            onHide = {
                isShowDialog.value = false
                onDismiss?.invoke(this)
            }
        )
    }

    Dialog(
        dialogController = dialogController,
        isShowDialog = dialogController.isShowDialog,
        dialogType = dialogType,
        title = title,
        width = width,
        height = height,
        cancelable = cancelable,
        cancelOnTouchOutside = cancelOnTouchOutside,
        onDismiss = onDismiss,
        data = dialogController.data.collectAsState().value,
        content = content
    )

    return dialogController
}

@Composable
internal fun <T> Dialog(
    dialogController: DialogController<T>,
    isShowDialog: MutableStateFlow<Boolean>,
    dialogType: DialogType,
    title: String,
    width: Dp,
    height: Dp?,
    cancelable: Boolean,
    cancelOnTouchOutside: Boolean,
    onDismiss: (DialogController<T>.() -> Unit)?,
    data: T?,
    content: @Composable (ColumnScope.(DialogController<T>, T?) -> Unit)
) {

    if (isShowDialog.collectAsState().value) {
        val scope = rememberCoroutineScope()

        val borderColor =
            if (dialogType == DialogType.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
        val titleBackground =
            if (dialogType == DialogType.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface
        val titleForeground =
            if (dialogType == DialogType.Error) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSecondaryContainer

        MatDialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            onDismissRequest = {
                scope.launch {
                    isShowDialog.emit(!cancelOnTouchOutside)
                }
                if (cancelOnTouchOutside) onDismiss?.invoke(dialogController)
            }
        ) {

            /*val density = LocalDensity.current
            val configuration = LocalWindowState.current
            var offset by remember { mutableStateOf(Offset.Zero) }
            var horizontalRange = remember { Pair(0f, 0f) }
            var verticalRange = remember { Pair(0f, 0f) }*/
            var rootModifier = Modifier.width(width)
            if (height != null) rootModifier = rootModifier.height(height)

            Card(
                modifier = rootModifier
                /*.offset(x = offset.x.dp, y = offset.y.dp)
                .onGloballyPositioned {
                    val dialogWidth = it.size.width
                    val dialogHeight = it.size.height
                    val screenWidth = configuration.size.width
                    val screenHeight = configuration.size.height
                    val safeDelta = 20f

                    val minMaxHorizontal = (with(density) { screenWidth.toPx() } / 2f - dialogWidth / 2f) - safeDelta
                    val minMaxVertical = (with(density) { screenHeight.toPx() } / 2f - dialogHeight / 2f) - safeDelta

                    horizontalRange = Pair(
                        minMaxHorizontal * -1f,
                        minMaxHorizontal
                    )

                    verticalRange = Pair(
                        minMaxVertical * -1f,
                        minMaxVertical
                    )
                }*/,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = borderColor
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier.background(titleBackground)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    /*.pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->

                            val x = (offset.x + dragAmount.x).coerceIn(
                                horizontalRange.first,
                                horizontalRange.second
                            )

                            val y = (offset.y + dragAmount.y).coerceIn(
                                verticalRange.first,
                                verticalRange.second
                            )

                            offset = Offset(
                                x = x,
                                y = y
                            )
                        }
                    }*/
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = title,
                        color = titleForeground,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (cancelable) {
                        SmallIconButton(
                            mainModifier = Modifier.size(24.dp).align(Alignment.CenterEnd),
                            iconModifier = Modifier.size(18.dp),
                            icon = Icons.Outlined.Close,
                            tint = titleForeground,
                            onClick = {
                                scope.launch {
                                    isShowDialog.emit(false)
                                    onDismiss?.invoke(dialogController)
                                }
                            }
                        )
                    }
                }
                HorizontalDivider()

                var bodyModifier =
                    Modifier.background(MaterialTheme.colorScheme.background).fillMaxWidth()
                if (height != null) bodyModifier = bodyModifier.height(height)

                Column(
                    modifier = bodyModifier,
                    content = {
                        content(dialogController, data)
                    }
                )
            }
        }
    }
}