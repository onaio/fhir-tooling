package org.smartregister.fct.common.domain.model

import androidx.constraintlayout.compose.platform.annotation.FloatRange

internal const val MIN_SIZE_RATIO = 0.01f
internal const val MAX_SIZE_RATIO = 0.99f

/**
 * Resize option for vertical and horizontal split pane
 *
 * @property sizeRatio use to define how much split the container withIn {minSizeRatio} and {maxSizeRatio} by default
 * @property minSizeRatio use to define minimum threshold for top/left container
 * @property maxSizeRatio use to define maximum threshold for bottom/right container
 */
sealed class ResizeOption(
    val sizeRatio: Float,
    val minSizeRatio: Float,
    val maxSizeRatio: Float,
    val viewMode: ViewMode
) {

    class Fixed(
        @FloatRange(from = MIN_SIZE_RATIO.toDouble(), to = MAX_SIZE_RATIO.toDouble())
        sizeRatio: Float = 0.5f,
        viewMode: ViewMode = ViewMode.Dock,
    ) : ResizeOption(
        sizeRatio = sizeRatio,
        minSizeRatio = sizeRatio,
        maxSizeRatio = sizeRatio,
        viewMode = viewMode
    )

    class Flexible(
        val savedKey: Any? = null,
        @FloatRange(from = MIN_SIZE_RATIO.toDouble(), to = MAX_SIZE_RATIO.toDouble())
        sizeRatio: Float = 0.5f,
        minSizeRatio: Float = MIN_SIZE_RATIO,
        maxSizeRatio: Float = MAX_SIZE_RATIO,
        viewMode: ViewMode = ViewMode.Dock,
    ) : ResizeOption(
        sizeRatio = ResizeOptionContainer.getOrPut(savedKey, sizeRatio),
        minSizeRatio = minSizeRatio,
        maxSizeRatio = maxSizeRatio,
        viewMode = viewMode,
    ) {
        fun updateValue(value: Float) {
            savedKey?.run {
                ResizeOptionContainer.put(savedKey, value)
            }
        }
    }
}

private object ResizeOptionContainer {

    private val savedRatio = mutableMapOf<Any, Float>()

    fun getOrPut(savedKey: Any?, value: Float): Float {
        return savedRatio[savedKey] ?: savedKey?.let {
            savedRatio[savedKey] = value
            value
        } ?: value
    }

    fun put(savedKey: Any, value: Float) {
        savedRatio[savedKey] = value
    }
}