package org.smartregister.fct.fm.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.fm.domain.datasource.FileSystem
import org.smartregister.fct.fm.domain.model.FileManagerMode
import org.smartregister.fct.fm.presentation.components.SystemFileManagerComponent

@Composable
internal fun SystemFileManager(
    componentContext: ComponentContext,
    mode: FileManagerMode = FileManagerMode.Edit()
) {

    val fileSystem: FileSystem = koinInject()
    val component = remember {
        SystemFileManagerComponent(
            componentContext = componentContext,
            fileSystem = fileSystem,
            mode = mode
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (mode is FileManagerMode.Edit) Title("System File Manager")
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            CommonNavigation(
                activePath = component.getActivePath().collectAsState().value,
                commonDirs = component.getCommonDirs(),
                rootDirs = component.getRootDirs(),
                onDirectoryClick = { activePath ->
                    component.componentScope.launch {
                        component.setActivePath(activePath)
                    }
                },
            )

            Column {
                ContentOptions(component)
                Content(
                    component = component,
                )
            }

        }
    }
}