package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.animation.core.EaseOutElastic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.util.pxToDp

@Composable
fun <T> HorizontalButtonStrip(
    modifier: Modifier = Modifier,
    options: List<T>,
    label: (T) -> String,
    isExpanded: Boolean = false,
    initialSelectedIndex: Int? = null,
    onInitialSelected: (T.(Int) -> Unit)? = null,
    selectedButtonBackground: Color = colorScheme.primary,
    selectedLabelColor: Color = colorScheme.onPrimary,
    stripBackgroundColor: Color = colorScheme.surfaceContainer,
    key: Any? = null,
    onClick: T.(Int) -> Unit,
) {

    if (initialSelectedIndex != null) {
        require(initialSelectedIndex in 0..options.size.minus(1)) {
            throw IndexOutOfBoundsException("{initialSelectedIndex} should be greater than or equal to 0 and less than from {options} length")
        }
    }

    val firstButtonShape = remember {
        RoundedCornerShape(
            topStart = auroraButtonShape.topStart,
            topEnd = CornerSize(0.dp),
            bottomStart = auroraButtonShape.bottomStart,
            bottomEnd = CornerSize(0.dp)
        )
    }

    val lastButtonShape = remember {
        RoundedCornerShape(
            topStart = CornerSize(0.dp),
            topEnd = auroraButtonShape.topEnd,
            bottomStart = CornerSize(0.dp),
            bottomEnd = auroraButtonShape.bottomEnd
        )
    }

    val middleButtonShape = remember { RectangleShape }

    val boxModifier = if (isExpanded) Modifier.fillMaxWidth() else Modifier.wrapContentWidth()
    val buttonModifier: RowScope.() -> Modifier = {
        if (isExpanded) {
            Modifier.weight(1f)
        } else Modifier
    }

    val callOnInitialSelection = remember { mutableStateOf(false) }

    if (initialSelectedIndex != null && !callOnInitialSelection.value) {
        callOnInitialSelection.value = true
        onInitialSelected?.invoke(options[initialSelectedIndex], initialSelectedIndex)
    }

    val buttonProps = remember { mutableStateMapOf<Int, Pair<Float, Float>>() }
    var selectedIndex by remember(key) { mutableStateOf(initialSelectedIndex) }

    val sliderOffsetX = buttonProps[selectedIndex]?.first ?: 0f
    val sliderWidth = buttonProps[selectedIndex]?.second ?: 0f

    val sliderAnimatedOffsetX by animateFloatAsState(
        targetValue = sliderOffsetX,
        animationSpec = tween(
            durationMillis = 1000,
            easing = EaseOutElastic
        )
    )

    Box(modifier) {
        Card(
            modifier = boxModifier.height(40.dp),
            shape = auroraButtonShape,
            colors = CardDefaults.cardColors(
                containerColor = stripBackgroundColor
            ),
            border = BorderStroke(
                width = 1.dp,
                color = colorScheme.onSurface.copy(0.3f)
            )
        ) {
            Box {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(sliderWidth.pxToDp())
                        .offset(sliderAnimatedOffsetX.pxToDp())
                        .background(selectedButtonBackground),
                )

                Row(
                    modifier = boxModifier
                ) {
                    options.forEachIndexed { index, item ->

                        val shape = when (index) {
                            0 -> firstButtonShape
                            (options.size - 1) -> lastButtonShape
                            else -> middleButtonShape
                        }

                        TextButton(
                            modifier = buttonModifier()
                                .onGloballyPositioned { layoutCoordinates ->
                                    buttonProps[index] = Pair(
                                        layoutCoordinates.positionInParent().x,
                                        layoutCoordinates.size.width.toFloat()
                                    )
                                },
                            label = label(item),
                            labelColor = if (index == selectedIndex) selectedLabelColor else null,
                            shape = shape,

                            onClick = {
                                selectedIndex = index
                                item.onClick(index)
                            }
                        )
                    }
                }
            }
        }
    }
}