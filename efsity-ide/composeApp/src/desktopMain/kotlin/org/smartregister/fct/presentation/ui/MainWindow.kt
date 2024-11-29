package org.smartregister.fct.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.LightDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.LocalContentColor
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import org.koin.compose.koinInject
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.common.util.LocalWindowState
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.presentation.component.RootComponentImpl
import org.smartregister.fct.presentation.theme.AuroraTheme
import org.smartregister.fct.presentation.ui.components.WindowsActionButtons
import org.smartregister.fct.util.CustomWindowDecorationAccessing
import org.smartregister.fct.util.ProvideWindowSpotContainer
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.geom.RoundRectangle2D


@Composable
private fun FrameWindowScope.CustomWindowFrame(
    onRequestMinimize: (() -> Unit)?,
    onRequestClose: () -> Unit,
    onRequestToggleMaximize: ((WindowPlacement) -> Unit)?,
    titleContent: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onBackground,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            SnapDraggableToolbar(
                window = window,
                onRequestMinimize = onRequestMinimize,
                onRequestClose = onRequestClose,
                onRequestToggleMaximize = onRequestToggleMaximize,
                windowState = LocalWindowState.current,
                content = titleContent
            )
            content()
        }
    }
}

@Composable
fun isWindowMaximized(): Boolean {
    return LocalWindowState.current.placement == WindowPlacement.Maximized
}

@Composable
fun FrameWindowScope.SnapDraggableToolbar(
    window: ComposeWindow,
    onRequestMinimize: (() -> Unit)?,
    onRequestToggleMaximize: ((WindowPlacement) -> Unit)?,
    onRequestClose: () -> Unit,
    windowState: WindowState,
    content: @Composable RowScope.() -> Unit,
) {
    ProvideWindowSpotContainer(
        windowState = windowState
    ) {
        if (CustomWindowDecorationAccessing.isSupported) {
            FrameContent(window, onRequestMinimize, onRequestToggleMaximize, onRequestClose, content)
        } else {
            WindowDraggableArea {
                FrameContent(window, onRequestMinimize, onRequestToggleMaximize, onRequestClose, content)
            }
        }
    }
}

@Composable
private fun FrameContent(
    window: ComposeWindow,
    onRequestMinimize: (() -> Unit)?,
    onRequestToggleMaximize: ((WindowPlacement) -> Unit)?,
    onRequestClose: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {

    Box {
        ConstraintLayout(
            Modifier.
                fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(0.2f))
                .height(50.dp),
        ) {

            val (leftRef, rightRef) = createRefs()

            Row(
                modifier = Modifier.padding(horizontal = 6.dp).constrainAs(leftRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(rightRef.start)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.preferredWrapContent
                },
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )

            Box(
                modifier = Modifier.constrainAs(rightRef) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            ) {
                WindowsActionButtons(
                    window,
                    onRequestClose,
                    onRequestMinimize,
                    onRequestToggleMaximize,
                )
            }
        }

        HorizontalDivider(Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun MainWindow(
    state: WindowState,
    onCloseRequest: () -> Unit,
    onRequestMinimize: (() -> Unit)? = {
        state.isMinimized = true
    },
    title: String = "Untitled",
    appIcon: Painter,
    titleContent: @Composable RowScope.() -> Unit,
    rootComponent: RootComponentImpl,
    content: @Composable FrameWindowScope.() -> Unit,
) {
    //two-way binding
    val windowController = remember {
        WindowController()
    }

    Window(
        state = state,
        undecorated = true,
        icon = appIcon,
        title = title,
        onCloseRequest = onCloseRequest,
    ) {
        window.minimumSize = java.awt.Dimension(800, 600)
        var m by remember { mutableStateOf(window.placement) }

        window.addComponentListener(object: ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                window.shape = RoundRectangle2D.Double(
                    0.0,
                    0.0,
                    window.width.toDouble(),
                    window.height.toDouble(),
                    if (m == WindowPlacement.Maximized) 0.0 else 8.0,
                    if (m == WindowPlacement.Maximized) 0.0 else 8.0,
                )
            }
        })

        CompositionLocalProvider(
            LocalWindowController provides windowController,
            LocalWindowState provides state,
        ) {


            val appSettingManager: AppSettingManager = koinInject()
            val isDarkTheme by appSettingManager.isDarkTheme.collectAsState()

            val contextMenuRepresentation = if (isDarkTheme) {
                DarkDefaultContextMenuRepresentation
            } else {
                LightDefaultContextMenuRepresentation
            }

            CompositionLocalProvider(LocalContextMenuRepresentation provides contextMenuRepresentation) {
                AuroraTheme(
                    isDarkModel = isDarkTheme
                ) {

                    Surface(
                        shape = if (m == WindowPlacement.Maximized) RectangleShape else RoundedCornerShape(4.dp),
                        color = Color.Transparent,
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    ) {
                        CustomWindowFrame(
                            onRequestMinimize = onRequestMinimize,
                            onRequestClose = onCloseRequest,
                            onRequestToggleMaximize = {
                                m = it
                            },
                            titleContent = titleContent
                        ) {
                            Aurora(
                                componentContext = rootComponent
                            ) {
                                content()
                            }
                        }
                    }
                }
            }

        }
    }
}

class WindowController {}

private val LocalWindowController =
    compositionLocalOf<WindowController> { error("window controller not provided") }

