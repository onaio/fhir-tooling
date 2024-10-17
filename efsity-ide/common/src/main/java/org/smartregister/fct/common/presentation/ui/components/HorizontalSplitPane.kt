package org.smartregister.fct.common.presentation.ui.components

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.util.dpToPx
import org.smartregister.fct.aurora.util.pxToDp
import org.smartregister.fct.common.domain.model.MAX_SIZE_RATIO
import org.smartregister.fct.common.domain.model.MIN_SIZE_RATIO
import org.smartregister.fct.common.domain.model.ResizeOption
import org.smartregister.fct.common.domain.model.ViewMode
import org.smartregister.fct.common.util.windowWidthResizePointer

/**
 *  Split the content vertically with fixed and flexible resizing options
 *
 *  @param modifier use to apply for composable property
 *  @param resizeOption provide flexible and fixed resizing options
 *  @param leftContent provide left space for composable view
 *  @param rightContent provide right space for composable view
 */
@Composable
fun HorizontalSplitPane(
    modifier: Modifier = Modifier.fillMaxSize(),
    resizeOption: ResizeOption = ResizeOption.Flexible(),
    leftContent: @Composable BoxScope.() -> Unit,
    rightContent: @Composable BoxScope.() -> Unit,
    enableRightContent: Boolean = true,
) {

    require(resizeOption.minSizeRatio in MIN_SIZE_RATIO..MAX_SIZE_RATIO) {
        throw IllegalStateException("minSizeRatio should be in ${MIN_SIZE_RATIO}f to ${MAX_SIZE_RATIO}f range.")
    }

    require(resizeOption.maxSizeRatio in resizeOption.minSizeRatio..MAX_SIZE_RATIO) {
        throw IllegalStateException("maxSizeRatio should be greater or equal to minSizeRatio and should be less than ${MAX_SIZE_RATIO}f")
    }

    require(resizeOption.sizeRatio in resizeOption.minSizeRatio..resizeOption.maxSizeRatio) {
        throw IllegalStateException("sizeRatio should be in minSizeRatio to maxSizeRatio range.")
    }

    var left by remember { mutableStateOf(resizeOption.sizeRatio) }
    val right = 1f - left
    var rawX by remember { mutableStateOf(left) }

    val draggableArea = remember { 4.dp }
    var containerWidth by remember { mutableStateOf(0f) }
    val dividerOffsetX = (containerWidth * left)
    val leftViewWidth = (containerWidth * left) + (draggableArea.dpToPx() / 2f)
    val rightViewWidth = (containerWidth * right) - (draggableArea.dpToPx() / 2f)
    val rightViewOffsetX = (containerWidth * left) + (draggableArea.dpToPx() / 2f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutCoordinates ->
                containerWidth = layoutCoordinates.size.width.toFloat()
            }
    ) {

        val leftModifier = if (enableRightContent && resizeOption.viewMode == ViewMode.Dock) {
            Modifier
                .width(leftViewWidth.pxToDp())
                .fillMaxHeight()
        } else {
            Modifier.fillMaxSize()
        }

        Box(
            modifier = leftModifier,
            content = leftContent
        )

        if (enableRightContent) {

            Box(
                modifier = Modifier
                    .offset(rightViewOffsetX.pxToDp())
                    .width(rightViewWidth.pxToDp())
                    .fillMaxHeight(),
                content = rightContent
            )

            VerticalDivider(
                modifier = Modifier
                    .width(draggableArea)
                    .offset(dividerOffsetX.pxToDp())
                    .pointerHoverIcon(windowWidthResizePointer)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                rawX = left
                            }
                        ) { _, dragAmount ->
                            rawX += dragAmount / containerWidth

                            if (rawX >= resizeOption.minSizeRatio && rawX <= resizeOption.maxSizeRatio) {
                                val x = left + dragAmount / containerWidth
                                left = x.coerceIn(
                                    resizeOption.minSizeRatio,
                                    resizeOption.maxSizeRatio
                                )
                            }
                        }
                    }
            )

            if (resizeOption is ResizeOption.Flexible) {
                resizeOption.updateValue(left)
            }
        }
    }
}