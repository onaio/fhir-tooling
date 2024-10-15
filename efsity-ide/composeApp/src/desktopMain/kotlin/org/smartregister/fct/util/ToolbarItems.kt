package org.smartregister.fct.util

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowState
import java.awt.Rectangle
import java.awt.Shape
import java.awt.Window
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

private val LocalWindowHitSpots =
    compositionLocalOf<MutableMap<Any, Pair<Rectangle, Int>>> { error("LocalWindowHitSpots not provided") }

@Composable
private fun FrameWindowScope.getCurrentWindowSize(): DpSize {
    var windowSize by remember {
        mutableStateOf(DpSize(window.width.dp, window.height.dp))
    }
    //observe window size
    DisposableEffect(window) {
        val listener = object : ComponentAdapter() {
            override fun componentResized(p0: ComponentEvent?) {
                windowSize = DpSize(window.width.dp, window.height.dp)
            }
        }
        window.addComponentListener(listener)
        onDispose {
            window.removeComponentListener(listener)
        }
    }
    return windowSize
}

/**
 * dp as int
 */
/*context (Density)*/
private fun Int.toAwtUnitSize(density: Density) = with(density) {
    toDp().value.toInt()
}

private fun placeHitSpots(
    window: Window,
    spots: Map<Shape, Int>,
    height: Int,
) {
    CustomWindowDecorationAccessing.setCustomDecorationEnabled(window, true)
    CustomWindowDecorationAccessing.setCustomDecorationTitleBarHeight(
        window,
        height,
    )
    CustomWindowDecorationAccessing.setCustomDecorationHitTestSpotsMethod(window, spots)
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FrameWindowScope.ProvideWindowSpotContainer(
    windowState: WindowState,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val windowSize = getCurrentWindowSize()
    val containerSize = with(density) {
        LocalWindowInfo.current.containerSize.let {
            DpSize(it.width.toDp(), it.height.toDp())
        }
    }
    // we pass it to composition local provider
    val spotInfoState = remember {
        mutableStateMapOf<Any, Pair<Rectangle, Int>>()
    }
    var toolbarHeight by remember {
        mutableStateOf(0)
    }

    val spotsWithInfo = spotInfoState.toMap()
    var shouldRestorePlacement by remember(window) {
        mutableStateOf(true)
    }
    //if any of this keys change we will re position hit spots
    LaunchedEffect(
        spotsWithInfo,
        toolbarHeight,
        window,
        windowSize,
        containerSize,
    ) {
        //
        if (CustomWindowDecorationAccessing.isSupported) {
            val startOffset = (windowSize - containerSize) / 2
            val startWidthOffsetInDp = startOffset.width.value.toInt()
//          val startHeightInDp=delta.height.value.toInt() //it seems no need here
            val spots: Map<Shape, Int> = spotsWithInfo.values.associate { (rect, spot) ->
                Rectangle(rect.x + startWidthOffsetInDp, rect.y, rect.width, rect.height) to spot
            }
            //it seems after activating hit spots window class will change its placement
            //we only want to restore placement whe windows is loaded for first time
            if (shouldRestorePlacement) {
                //this block only called once for each window
                val lastPlacement = windowState.placement
                placeHitSpots(window, spots, toolbarHeight)
                window.placement = lastPlacement
                shouldRestorePlacement = false
            } else {
                placeHitSpots(window, spots, toolbarHeight)
            }

        }
    }
    CompositionLocalProvider(
        LocalWindowHitSpots provides spotInfoState
    ) {
        Box(Modifier.onGloballyPositioned {
            toolbarHeight = with(density) {
                it.size.height.toAwtUnitSize(this)
            }
        }) {
            content()
        }
    }
}