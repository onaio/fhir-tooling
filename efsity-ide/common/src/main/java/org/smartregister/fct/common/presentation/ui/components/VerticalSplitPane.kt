package org.smartregister.fct.common.presentation.ui.components

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.HorizontalDivider
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
import org.smartregister.fct.common.util.windowHeightResizePointer

/**
 *  Split the content vertically with fixed and flexible resizing options
 *
 *  @param modifier use to apply for composable property
 *  @param resizeOption provide flexible and fixed resizing options
 *  @param topContent provide top space for composable view
 *  @param bottomContent provide bottom space for composable view
 */
@Composable
fun VerticalSplitPane(
    modifier: Modifier = Modifier.fillMaxSize(),
    resizeOption: ResizeOption = ResizeOption.Flexible(),
    topContent: @Composable BoxScope.() -> Unit,
    bottomContent: @Composable BoxScope.() -> Unit,
    enableBottomContent: Boolean = true,
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

    var top by remember { mutableStateOf(resizeOption.sizeRatio) }
    val bottom = 1f - top
    var rawY by remember { mutableStateOf(top) }

    val draggableArea = remember { 4.dp }
    var containerHeight by remember { mutableStateOf(0f) }
    val dividerOffsetY = (containerHeight * top)
    val topViewHeight = (containerHeight * top) + (draggableArea.dpToPx() / 2f)
    val bottomViewHeight = (containerHeight * bottom) - (draggableArea.dpToPx() / 2f)
    val bottomViewOffsetY = (containerHeight * top) + (draggableArea.dpToPx() / 2f)

    Box(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                containerHeight = layoutCoordinates.size.height.toFloat()
            }
    ) {

        val topModifier = if (enableBottomContent && resizeOption.viewMode == ViewMode.Dock) {
            Modifier.height(topViewHeight.pxToDp())
        } else {
            Modifier.fillMaxHeight()
        }

        Box(
            modifier = topModifier
                .fillMaxWidth(),
            content = topContent
        )

        if (enableBottomContent) {

            Box(
                modifier = Modifier
                    .offset(y = bottomViewOffsetY.pxToDp())
                    .height(bottomViewHeight.pxToDp())
                    .fillMaxWidth(),
                content = bottomContent
            )

            HorizontalDivider(
                modifier = Modifier
                    .height(draggableArea)
                    .offset(y = dividerOffsetY.pxToDp())
                    .pointerHoverIcon(windowHeightResizePointer)
                    .pointerInput(Unit) {

                        detectVerticalDragGestures(
                            onDragStart = {
                                rawY = top
                            }
                        ) { _, dragAmount ->
                            rawY += dragAmount / containerHeight

                            if (rawY >= resizeOption.minSizeRatio && rawY <= resizeOption.maxSizeRatio) {
                                val y = top + dragAmount / containerHeight
                                top = y.coerceIn(
                                    resizeOption.minSizeRatio,
                                    resizeOption.maxSizeRatio
                                )
                            }
                        }
                    }
            )

            if (resizeOption is ResizeOption.Flexible) {
                resizeOption.updateValue(top)
            }
        }
    }
}