package org.smartregister.fct.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.smartregister.fct.common.data.manager.SubWindowManager
import org.smartregister.fct.common.domain.model.ResizeOption
import org.smartregister.fct.common.presentation.component.RootComponent
import org.smartregister.fct.common.presentation.ui.components.HorizontalSplitPane
import org.smartregister.fct.common.presentation.ui.components.VerticalSplitPane
import org.smartregister.fct.presentation.ui.components.BottomWindow
import org.smartregister.fct.presentation.ui.components.LeftNavigation
import org.smartregister.fct.presentation.ui.components.MainRoot
import org.smartregister.fct.presentation.ui.components.RightNavigation
import org.smartregister.fct.presentation.ui.components.RightWindow

@Composable
@Preview
fun App(
    rootComponent: RootComponent,
    subWindowManager: SubWindowManager
) {

    val enableBottomWindow by subWindowManager.enableBottomWindow.collectAsState()
    val bottomWindowViewMode by subWindowManager.bottomWindowViewMode.collectAsState()
    val enableRightWindow by subWindowManager.enableRightWindow.collectAsState()
    val rightWindowViewMode by subWindowManager.rightWindowViewMode.collectAsState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        val (leftNav, mainContent, rightNav) = createRefs()

        VerticalSplitPane(
            modifier = Modifier
                .constrainAs(mainContent) {
                    start.linkTo(leftNav.end)
                    top.linkTo(parent.top)
                    end.linkTo(rightNav.start)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.matchParent
                },
            resizeOption = ResizeOption.Flexible(
                sizeRatio = 0.7f,
                minSizeRatio = 0.2f,
                maxSizeRatio = 0.9f,
                viewMode = bottomWindowViewMode
            ),
            enableBottomContent = enableBottomWindow,
            topContent = {

                HorizontalSplitPane(
                    modifier = Modifier.fillMaxSize(),
                    resizeOption = ResizeOption.Flexible(
                        sizeRatio = 0.75f,
                        minSizeRatio = 0.2f,
                        maxSizeRatio = 0.9f,
                        viewMode = rightWindowViewMode
                    ),
                    enableRightContent = enableRightWindow,
                    leftContent = {
                        MainRoot(rootComponent)
                    },
                    rightContent = {
                        RightWindow(subWindowManager)
                    }
                )
            },
            bottomContent = {
                BottomWindow(subWindowManager)
            }
        )

        LeftNavigation(rootComponent = rootComponent, leftNav = leftNav)
        RightNavigation(subWindowManager, rightNav)
    }

}