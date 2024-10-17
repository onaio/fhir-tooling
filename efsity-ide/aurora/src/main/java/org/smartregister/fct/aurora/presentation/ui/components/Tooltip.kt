package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.areAnyPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Tooltip(
    modifier: Modifier = Modifier,
    tooltip: String,
    delayMillis: Int = 500,
    tooltipPosition: TooltipPosition,
    content: @Composable BoxScope.() -> Unit
) {
    var contentSize by remember { mutableStateOf(IntSize.Zero) }
    var tooltipOffset by remember { mutableStateOf(IntOffset.Zero) }
    var isVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var job: Job? by remember { mutableStateOf(null) }

    fun startShowing() {
        if (job?.isActive == true) {  // Don't restart the job if it's already active
            return
        }
        job = scope.launch {
            delay(delayMillis.toLong())
            isVisible = true
        }
    }

    fun hide() {
        job?.cancel()
        job = null
        isVisible = false
    }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                contentSize = it.size
            }
            .onPointerEvent(PointerEventType.Enter) {
                if (!isVisible && !it.buttons.areAnyPressed) {
                    startShowing()
                }
            }
            .onPointerEvent(PointerEventType.Exit) {
                hide()
            }
            .onPointerEvent(PointerEventType.Press, pass = PointerEventPass.Initial) {
                hide()
            }
    ) {

        Box {
            content()

            Popup(
                offset = tooltipOffset,
                onDismissRequest = { isVisible = false }
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(300))
                ) {

                    Box(
                        Modifier
                            .onGloballyPositioned {
                                val width = it.size.width
                                val height = it.size.height

                                when (tooltipPosition) {
                                    is TooltipPosition.Left -> {
                                        tooltipOffset = IntOffset(
                                            x = tooltipPosition.space - (10 + width),
                                            y = (contentSize.height / 2) - height / 2
                                        )
                                    }

                                    is TooltipPosition.Top -> {
                                        tooltipOffset = IntOffset(
                                            x = (contentSize.width / 2) - width / 2,
                                            y = tooltipPosition.space - (contentSize.height / 2 + height)
                                        )
                                    }

                                    is TooltipPosition.Right -> {
                                        tooltipOffset = IntOffset(
                                            x = contentSize.width + tooltipPosition.space,
                                            y = (contentSize.height / 2) - height / 2
                                        )
                                    }

                                    is TooltipPosition.Bottom -> {
                                        tooltipOffset = IntOffset(
                                            x = (contentSize.width / 2) - width / 2,
                                            y = contentSize.height + tooltipPosition.space
                                        )
                                    }
                                }
                            }
                    ) {
                        Surface(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = tooltip,
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                    fontFamily = FontFamily.Monospace
                                ),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class TooltipPosition(val space: Int) {

    class Left(
        space: Int = 0
    ) : TooltipPosition(space)

    class Top(
        space: Int = 0
    ) : TooltipPosition(space)

    class Right(
        space: Int = 10
    ) : TooltipPosition(space)

    class Bottom(
        space: Int = 10
    ) : TooltipPosition(space)
}

private fun Modifier.onPointerEvent(
    eventType: PointerEventType,
    pass: PointerEventPass = PointerEventPass.Main,
    onEvent: AwaitPointerEventScope.(event: PointerEvent) -> Unit
) = pointerInput(eventType, pass, onEvent) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(pass)
            if (event.type == eventType) {
                onEvent(event)
            }
        }
    }
}