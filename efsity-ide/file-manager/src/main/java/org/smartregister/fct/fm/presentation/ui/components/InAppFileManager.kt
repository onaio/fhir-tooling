package org.smartregister.fct.fm.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import org.smartregister.fct.fm.domain.datasource.FileSystem
import org.smartregister.fct.fm.domain.model.FileManagerMode
import org.smartregister.fct.fm.presentation.components.InAppFileManagerComponent

@Composable
internal fun InAppFileManager(
    componentContext: ComponentContext,
    mode: FileManagerMode = FileManagerMode.Edit()
) {

    val fileSystem: FileSystem = koinInject(qualifier = named("inApp"))
    val component = remember {
        InAppFileManagerComponent(
            componentContext = componentContext,
            fileSystem = fileSystem,
            mode = mode
        )
    }

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        if (mode is FileManagerMode.Edit) Title("App File Manager")
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            CommonNavigation(
                activePath = component.getActivePath().collectAsState().value,
                commonDirs = component.getCommonDirs(),
                onDirectoryClick = { activePath ->
                    scope.launch {
                        component.setActivePath(activePath)
                    }
                },
            )

            Column(Modifier.fillMaxSize()) {
                DefaultContentOptions(component)
                Content(
                    component = component,
                )
            }
        }
    }

}


@Composable
private fun DefaultContentOptions(component: InAppFileManagerComponent) {

    ContentOptions(
        component = component
    ) {
        Spacer(Modifier.width(12.dp))
        CreateNewFolder(component)
    }
}

