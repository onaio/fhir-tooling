package org.smartregister.fct.common.data.helper

import androidx.compose.ui.text.input.OffsetMapping

private const val TAB_WIDTH_IN_SPACES = 4

internal class TabOffsetMapping(private val originalTabPositions: List<Int>) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        return offset + originalTabPositions.count { it < offset } * (TAB_WIDTH_IN_SPACES - 1)
    }

    override fun transformedToOriginal(offset: Int): Int {
        var resultOffset = offset
        for (i in originalTabPositions) {
            val newOffset = resultOffset - (TAB_WIDTH_IN_SPACES - 1)
            if (i in newOffset..resultOffset) {
                return i
            } else if (i < newOffset) {
                resultOffset = newOffset
            }
        }
        return resultOffset
    }
}