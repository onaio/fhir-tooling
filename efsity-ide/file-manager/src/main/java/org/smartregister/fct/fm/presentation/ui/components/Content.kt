package org.smartregister.fct.fm.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okio.Path
import org.smartregister.fct.common.data.controller.ConfirmationDialogController
import org.smartregister.fct.common.presentation.ui.dialog.DialogType
import org.smartregister.fct.common.presentation.ui.dialog.rememberAlertDialog
import org.smartregister.fct.common.presentation.ui.dialog.rememberConfirmationDialog
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.getKoinInstance
import org.smartregister.fct.fm.data.communication.InterCommunication
import org.smartregister.fct.fm.domain.model.ContextMenu
import org.smartregister.fct.fm.domain.model.ContextMenuPath
import org.smartregister.fct.fm.domain.model.ContextMenuType
import org.smartregister.fct.fm.presentation.components.FileManagerComponent

@Composable
internal fun Content(
    component: FileManagerComponent,
    //activePath: Path,
) {
    val activePathContent by component.getActivePathContent().collectAsState()
    val verticalScrollState = rememberScrollState()
    val visible by component.visibleItem.collectAsState()

    val deleteErrorDialog = rememberAlertDialog<Throwable>(
        title = "Error",
        dialogType = DialogType.Error
    ) { _, ex ->
        val error = ex?.message ?: "Unknown Error"
        Text(error)
    }

    val deleteDialog = rememberConfirmationDialog<ContextMenuPath> { _, contextMenuPath ->
        val contextMenu = contextMenuPath!!.menu
        val path = contextMenuPath.path
        val result = component.onContextMenuClick(contextMenu, path)
        if (result.isFailure) {
            deleteErrorDialog.show(result.exceptionOrNull())
        }
    }

    ConstraintLayout(Modifier.fillMaxSize()) {
        val (contentRef, pathRef) = createRefs()

        Box(
            modifier = Modifier.constrainAs(contentRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(pathRef.top)
                height = Dimension.fillToConstraints
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(verticalScrollState),
            ) {
                Items(
                    component = component,
                    visible = visible,
                    activePathContent = activePathContent,
                    deleteDialog = deleteDialog
                )
                Spacer(Modifier.height(12.dp))
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = verticalScrollState
                )
            )
        }

        Breadcrumb(component, pathRef)
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Items(
    component: FileManagerComponent,
    visible: Boolean,
    activePathContent: List<Path>,
    deleteDialog: ConfirmationDialogController<ContextMenuPath>
) {

    Spacer(Modifier.height(12.dp))
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        FlowRow(
            modifier = Modifier.fillMaxSize(),
        ) {
            activePathContent.forEach { path ->
                ContentItem(
                    scope = component.componentScope,
                    path = path,
                    contextMenuList = component.getContextMenuList(),
                    onDoubleClick = { component.onDoubleClick(it) },
                    onContextMenuClick = { menu, _ ->
                        handleContextMenuClick(
                            component = component,
                            scope = component.componentScope,
                            deleteDialog = deleteDialog,
                            menu = menu,
                            path = path
                        )
                    }
                )
            }
        }
    }
}

private fun handleContextMenuClick(
    component: FileManagerComponent,
    scope: CoroutineScope,
    deleteDialog: ConfirmationDialogController<ContextMenuPath>,
    menu: ContextMenu,
    path: Path
) {

    scope.launch {
        when (menu.menuType) {
            ContextMenuType.Delete -> {
                val deleteWhat =
                    if (path.toFile().isDirectory) "Folder" else "File"
                deleteDialog.show(
                    title = "Delete $deleteWhat",
                    message = "Are you sure you want to ${path.name} $deleteWhat",
                    ContextMenuPath(menu, path)
                )
            }

            ContextMenuType.CopyToInternal -> copyToInternal(path)
            else -> component.onContextMenuClick(menu, path)
        }
    }
}

private suspend fun copyToInternal(path: Path) {
    val interCommunication = getKoinInstance<InterCommunication>()
    interCommunication.pathReceived.emit(path)
}
