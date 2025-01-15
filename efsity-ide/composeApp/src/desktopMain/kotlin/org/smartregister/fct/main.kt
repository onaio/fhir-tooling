package org.smartregister.fct

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import fct.composeapp.generated.resources.Res
import fct.composeapp.generated.resources.app_icon
import fct.composeapp.generated.resources.splash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.startKoin
import org.smartregister.fct.adb.ADBModuleSetup
import org.smartregister.fct.apiclient.ApiClientModuleSetup
import org.smartregister.fct.common.CommonModuleSetup
import org.smartregister.fct.common.data.locals.LocalRootComponent
import org.smartregister.fct.common.data.locals.LocalSnackbarHost
import org.smartregister.fct.common.data.locals.LocalSubWindowManager
import org.smartregister.fct.cql.CQLModuleSetup
import org.smartregister.fct.engine.EngineModuleSetup
import org.smartregister.fct.fm.FileManagerModuleSetup
import org.smartregister.fct.pm.PMModuleSetup
import org.smartregister.fct.presentation.component.RootComponentImpl
import org.smartregister.fct.presentation.theme.AuroraTheme
import org.smartregister.fct.presentation.ui.App
import org.smartregister.fct.presentation.ui.MainWindow
import org.smartregister.fct.presentation.ui.components.StatusBar
import org.smartregister.fct.presentation.ui.components.TitleBar
import org.smartregister.fct.rules.RuleModuleSetup
import org.smartregister.fct.sm.SMModuleSetup
import org.smartregister.fct.util.runOnUiThread
import org.smartregister.fct.workflow.WorkflowModuleSetup
import java.awt.Toolkit


fun main() = application {

    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val screenWidth = screenSize.width
    val screenHeight = screenSize.height
    val scope = rememberCoroutineScope()
    val lifecycle = LifecycleRegistry()
    var rootComponent by remember { mutableStateOf<RootComponentImpl?>(null) }

    if (rootComponent != null) {

        val subWindowViewModel = LocalSubWindowManager.current

        val windowState = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = (screenWidth - 300).dp,
            height = (screenHeight - 200).dp
        )

        LifecycleController(lifecycle, windowState)

        CompositionLocalProvider(LocalRootComponent provides rootComponent) {
            MainWindow(
                state = windowState,
                title = "FhirCore Toolkit",
                appIcon = painterResource(Res.drawable.app_icon),
                onCloseRequest = ::exitApplication,
                titleContent = {
                    TitleBar(
                        componentContext = rootComponent!!,
                        subWindowManager = subWindowViewModel
                    )
                },
                rootComponent = rootComponent!!
            ) {
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(LocalSnackbarHost.current)
                    },
                    bottomBar = { StatusBar() },
                    containerColor = Color.Transparent,
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        App(
                            rootComponent = rootComponent!!,
                            subWindowManager = subWindowViewModel
                        )
                    }
                }
            }
        }
    } else {

        startKoin { }
        LoadingWindow()

        initSubModules(scope) {
            rootComponent = runOnUiThread {
                RootComponentImpl(
                    componentContext = DefaultComponentContext(lifecycle = lifecycle)
                )
            }
        }
    }
}

@Composable
private fun LoadingWindow() {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        width = 600.dp,
        height = 350.dp
    )

    Window(
        state = windowState,
        undecorated = true,
        onCloseRequest = {},
        icon = painterResource(Res.drawable.app_icon)
    ) {
        AuroraTheme {
            Box {
                Image(
                    painter = painterResource(Res.drawable.splash),
                    contentDescription = null
                )

                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "..:: Loading Modules ::...",
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

private fun initSubModules(scope: CoroutineScope, loaded: () -> Unit) {

    scope.launch {

        // load engine module first
        EngineModuleSetup().setup()

        listOf(
            CommonModuleSetup(),
            ADBModuleSetup(),
            PMModuleSetup(),
            FileManagerModuleSetup(),
            SMModuleSetup(),
            ApiClientModuleSetup(),
            RuleModuleSetup(),
            WorkflowModuleSetup(),
            CQLModuleSetup(),
        ).map {
            scope.async(Dispatchers.Default) {
                it.setup()
            }
        }.awaitAll()

        loaded()
    }
}