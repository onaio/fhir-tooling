package org.smartregister.fct.insights.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Sync
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.util.pxToDp
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.insights.domain.model.Insights
import java.lang.Float.max

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ResourceTypeCountChart(insights: Insights, onRefresh: () -> Unit) {

    val appSettingManager = koinInject<AppSettingManager>()
    val boundDelta = remember { 80f }
    val maxCount = remember { getMaxCount(insights) }
    var boardWidth by remember { mutableStateOf(1f) }
    var boardOffsetX by remember { mutableStateOf(0f) }
    var minBoardOffsetX by remember { mutableStateOf(0f) }
    var bound by remember { mutableStateOf(Bound.Zero) }
    val chartBaseLineColor = colorScheme.onSurface.copy(0.1f)
    val columnColor = colorScheme.tertiary
    val columnWidth = remember { 40f }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                boardWidth = max(insights.resourceTypeCount.size * 110f, it.size.width.toFloat())
                minBoardOffsetX = 0 - (boardWidth - it.size.width.toFloat())
                bound = Bound(
                    x = boundDelta,
                    y = boundDelta,
                    width = boardWidth - boundDelta,
                    height = it.size.height - boundDelta - 50f
                )
            }
    ) {

        Text(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
            text = "Resources Count Chart",
            style = typography.titleMedium
        )

        if (insights.hasEnoughResourceTypeCount()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentWidth(unbounded = true, align = Alignment.Start)
                    .onPointerEvent(PointerEventType.Scroll) {
                        val scrollX = (it.changes.first().scrollDelta.x.toInt() * 30).inv()
                        boardOffsetX = (boardOffsetX + scrollX).coerceIn(minBoardOffsetX, 0f)
                    }

            ) {

                Box (
                    modifier = Modifier
                        .offset(x = boardOffsetX.dp)
                        .width(boardWidth.pxToDp())
                        .fillMaxHeight()
                ) {

                    val fontFamilyResolver = LocalFontFamilyResolver.current
                    val density = LocalDensity.current
                    val layoutDirection = LocalLayoutDirection.current
                    val textColor = colorScheme.onSurface
                    val textStyle = remember { TextStyle() }

                    val textLayoutResultMap = remember(appSettingManager.appSetting.isDarkTheme) {
                        mutableStateMapOf<String, TextConfig>().apply {
                            insights.resourceTypeCount.entries.forEach {
                                val keyTextMeasurer = TextMeasurer(fontFamilyResolver, density, layoutDirection)
                                val valueTextMeasurer = TextMeasurer(fontFamilyResolver, density, layoutDirection)
                                put(it.key, TextConfig(
                                    keyTextMeasurer = keyTextMeasurer,
                                    keyTextLayoutResult = keyTextMeasurer.measure(ellipsisKey(it.key), textStyle),
                                    valueTextMeasurer = valueTextMeasurer,
                                    valueTextLayoutResult = valueTextMeasurer.measure(it.value.toString(), textStyle),
                                )
                                )
                            }
                        }
                    }

                    Canvas(Modifier.fillMaxSize()) {

                        translate(top = bound.y) {
                            repeat(6) {
                                drawPath(
                                    path = getBaseLinePath(it, bound),
                                    color = chartBaseLineColor,
                                    style = Stroke(
                                        width = 1f,
                                    )
                                )
                            }

                            insights.resourceTypeCount.entries.forEachIndexed { index, entry ->


                                val x = (index + 1) * 100f + bound.x
                                val bottomY = 5 * bound.height / 6
                                val topY = bottomY - (entry.value.toFloat() / maxCount.toFloat() * bottomY)

                                drawPath(
                                    path = Path().apply {
                                        //val x = index * 100f + bound.x + 50f
                                        //val y = 5 * bound.height / 6
                                        moveTo(x, bottomY)
                                        lineTo(x, topY)
                                    },
                                    color = columnColor,
                                    style = Stroke(
                                        width = columnWidth,
                                    )
                                )

                                translate(x - textLayoutResultMap[entry.key]!!.valueTextLayoutResult.size.width / 2f, topY -30) {
                                    drawText(
                                        textMeasurer = textLayoutResultMap[entry.key]!!.valueTextMeasurer,
                                        text = "${entry.value}",
                                        style = TextStyle(
                                            color = textColor
                                        )
                                    )
                                }

                                val valueTextSize = textLayoutResultMap[entry.key]!!.keyTextLayoutResult.size
                                val rect = Rect(0f, 0f, valueTextSize.width.toFloat(), valueTextSize.height.toFloat())
                                translate(x - valueTextSize.width.toFloat() , bottomY + 10f) {
                                    rotate(-45f, rect.topRight) {
                                        drawText(
                                            textMeasurer = textLayoutResultMap[entry.key]!!.keyTextMeasurer,
                                            text = ellipsisKey(entry.key),
                                            style = TextStyle(
                                                color = textColor
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    repeat(6) {
                        Text(
                            modifier = Modifier
                                .width(boundDelta.pxToDp())
                                .padding(end = 6.dp)
                                .offset(
                                    y = (it * bound.height / 6 + bound.y - 12f).pxToDp()
                                ),
                            text = "${maxCount - (maxCount / 5 * it )}",
                            textAlign = TextAlign.End
                        )
                    }
                }

            }
        } else {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Not enough data available to visualize chart",
            )
        }

        Box(Modifier.align(Alignment.TopEnd).padding(top = 12.dp, end = 12.dp)) {
            SmallIconButton(
                mainModifier = Modifier.size(30.dp),
                icon = AuroraIconPack.Sync,
                onClick = onRefresh,
                tooltip = "Refresh",
            )
        }
    }


}

private data class TextConfig(
    val keyTextMeasurer: TextMeasurer,
    val keyTextLayoutResult: TextLayoutResult,
    val valueTextMeasurer: TextMeasurer,
    val valueTextLayoutResult: TextLayoutResult,
)

private fun getBaseLinePath(index: Int, bound: Bound): Path {
    return Path().apply {

        val y = index * bound.height / 6
        moveTo(bound.x, y)
        lineTo(bound.width, y)
    }
}

private fun ellipsisKey(key: String) :String {
    return if (key.length > 14) {
        key.take(7) + "..." + key.takeLast(7)
    } else {
        key
    }
}

private fun getMaxCount(insights: Insights): Int {
    val maxResourceCount = insights.getMaxResourceTypeCount()
    val rem = maxResourceCount % 10
    val max = (maxResourceCount - rem) + (maxResourceCount - rem) / 3
    return max - when {
        max in 101..1000 -> (max % 100)
        max in 1001..5000 -> (max % 500)
        max > 5000 -> (max % 1000)
        else -> max % 10
    }
}

data class Bound(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    companion object {
        val Zero = Bound(0f, 0f, 0f, 0f)
    }
}