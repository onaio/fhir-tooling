package org.smartregister.fct.common.presentation.ui.container

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Dns
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.PanelHeading
import org.smartregister.fct.common.data.locals.AuroraLocal
import org.smartregister.fct.common.data.manager.AuroraManager
import org.smartregister.fct.common.data.manager.AuroraManagerImpl
import org.smartregister.fct.common.domain.model.Message
import org.smartregister.fct.common.presentation.component.ServerConfigProviderDialogComponent
import org.smartregister.fct.common.presentation.ui.dialog.rememberLoaderDialogController

@Composable
fun Aurora(
    componentContext: ComponentContext,
    modifier: Modifier = Modifier,
    fab: @Composable (() -> Unit)? = null,
    content: @Composable BoxScope.(AuroraManager) -> Unit
) {

    // create aurora manager instance
    val aurora = remember(componentContext) { AuroraManagerImpl(componentContext) }

    // full screen loader dialog controller
    val loaderController = rememberLoaderDialogController()
    val showLoader by aurora.showLoader.collectAsState()

    // control loader dialog visibility
    if (showLoader) loaderController.show() else loaderController.hide()


    Scaffold(
        modifier = modifier,
        floatingActionButton = { fab?.invoke() }
    ) {
        Box(
            modifier = modifier.padding(it)
        ) {
            CompositionLocalProvider(AuroraLocal provides aurora) {
                content(aurora)
            }

            ServerConfigsPanel(componentContext, aurora)
            Snackbar(aurora)
        }
    }

}

context (BoxScope)
@Composable
private fun Snackbar(aurora: AuroraManagerImpl) {

    val showSnackbar by aurora.showSnackbar.collectAsState()

    AnimatedVisibility(
        modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
        visible = showSnackbar != null,
        enter = slideInVertically { it / 2 } + fadeIn(),
        exit = slideOutVertically { it / 2 } + fadeOut()
    ) {

        val text = remember { showSnackbar!!.text }

        val bg = when (showSnackbar) {
            is Message.Error -> colorScheme.error
            else -> colorScheme.primary
        }

        Card(
            modifier = Modifier.widthIn(min = 200.dp, max = 600.dp),
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = bg
            )
        ) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = text,
            )
        }
    }

}

context (BoxScope)
@Composable
private fun ServerConfigsPanel(componentContext: ComponentContext, aurora: AuroraManagerImpl) {

    val serverConfigComponent = remember(componentContext) {
        ServerConfigProviderDialogComponent(componentContext)
    }

    val showServerConfigPanel by aurora.showServerConfigDialog.collectAsState()

    var alpha by remember { mutableStateOf(0f) }
    var offsetX by remember { mutableStateOf((-300).dp) }
    var isShow by remember { mutableStateOf(false) }

    val animatedAlpha by animateFloatAsState(alpha, tween(500))
    val animatedOffsetX by animateDpAsState(
        targetValue = offsetX,
        finishedListener = {
            isShow = showServerConfigPanel != null
        }
    )

    if (showServerConfigPanel != null) {
        isShow = true
        alpha = 0.3f
        offsetX = 0.dp
    } else {
        alpha = 0.0f
        offsetX = (-300).dp
    }

    if (isShow) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(animatedAlpha))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        alpha = 0f
                        aurora.resetServerConfig()
                    }
            )

            Box(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .offset(x = animatedOffsetX)
                    .background(colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                ) {
                    PanelHeading("Server Configs")
                    ServerConfigsList(aurora, serverConfigComponent)
                }

                VerticalDivider(
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
private fun ServerConfigsList(
    aurora: AuroraManagerImpl,
    component: ServerConfigProviderDialogComponent
) {

    val allConfigs by component.configs.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        items(allConfigs) { serverConfig ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp, end = 12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorScheme.surfaceContainer)
                    .clickable {
                        val callback = aurora.showServerConfigDialog.value?.second
                        aurora.resetServerConfig()
                        callback?.invoke(serverConfig)
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    modifier = Modifier.size(20.dp).padding(2.dp),
                    icon = AuroraIconPack.Dns
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = serverConfig.title
                        )
                        Text(
                            text = serverConfig.username,
                            style = typography.bodySmall
                        )
                    }

                }
            }
        }

        if (allConfigs.isEmpty()) {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    text = "No server configs available",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

