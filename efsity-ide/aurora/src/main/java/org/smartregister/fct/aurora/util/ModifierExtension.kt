package org.smartregister.fct.aurora.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.event.MouseEvent

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.doubleClick(scope: CoroutineScope, onClick: () -> Unit) : Modifier {
    return this.onPointerEvent(PointerEventType.Press) {

        when (it.awtEventOrNull?.button) {
            MouseEvent.BUTTON1 -> when (it.awtEventOrNull?.clickCount) {
                1 -> {}
                2 -> {
                    scope.launch {
                        delay(200)
                        onClick()
                    }
                }
            }
        }
    }
}