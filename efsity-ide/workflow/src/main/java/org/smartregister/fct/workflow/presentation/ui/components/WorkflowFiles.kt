package org.smartregister.fct.workflow.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.flow.collectLatest
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Description
import org.smartregister.fct.aurora.auroraiconpack.NoteAdd
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.PanelHeading
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.common.presentation.ui.dialog.rememberConfirmationDialog
import org.smartregister.fct.common.presentation.ui.dialog.rememberSingleFieldDialog
import org.smartregister.fct.common.util.fileNameValidation
import org.smartregister.fct.workflow.presentation.components.BaseWorkflowComponent
import org.smartregister.fct.workflow.util.WorkflowConfig

@Composable
internal fun WorkflowFiles(
    component: BaseWorkflowComponent,
) {

    val openPath by component.openPath.collectAsState()
    val config = component.workflow.config
    val planDefinitionPath = config.planDefinitionPath
    val subjectPath = config.subjectPath
    val listOfPath = remember(component.workflow.id) {
        mutableStateListOf<String>().apply {
            addAll(config.otherResourcesPath)
        }
    }

    LaunchedEffect(component.workflow.id) {
        config.observableOtherResourcePath.collectLatest {
            listOfPath.clear()
            listOfPath.addAll(it)
        }
    }

    val deleteWorkflowFileDialog = rememberConfirmationDialog<String> { _, path ->
        component.deleteWorkflowFile(path!!)
    }

    val newWorkflowFileDialog = rememberSingleFieldDialog(
        title = "New Workflow File", validations = listOf(fileNameValidation)
    ) { fileName, _ ->
        component.createNewWorkflowFile(fileName)
    }

    Box(Modifier.width(300.dp)) {
        Column(
            modifier = Modifier.fillMaxSize().background(colorScheme.surfaceContainer.copy(0.5f))
        ) {
            PanelHeading(text = "Workflow Files",
                rightIcon = AuroraIconPack.NoteAdd,
                onRightIconClick = {
                    newWorkflowFileDialog.show()
                })
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                item {
                    listOf(planDefinitionPath, subjectPath).forEach { path ->
                        Row(modifier = Modifier.fillMaxWidth()
                            .background(itemBackground(component, path))
                            .clickable {component.openPath(path) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(Modifier.width(12.dp))
                            Icon(
                                modifier = Modifier.size(20.dp), icon = AuroraIconPack.Description
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                text = WorkflowConfig.getFileName(path)
                            )
                        }

                        HorizontalDivider()
                    }
                }

                items(listOfPath) { path ->

                    ConstraintLayout(modifier = Modifier.fillMaxWidth()
                        .background(itemBackground(component, path))
                        .clickable {
                            component.openPath(path)
                        }
                    ) {
                        val (titleRef, deleteBtnRef) = createRefs()

                        Row(
                            modifier = Modifier.constrainAs(titleRef) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                end.linkTo(deleteBtnRef.start, 12.dp)
                                bottom.linkTo(parent.bottom)
                                width = Dimension.fillToConstraints
                            }, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(Modifier.width(12.dp))
                            Icon(
                                modifier = Modifier.size(20.dp), icon = AuroraIconPack.Description
                            )
                            Tooltip(
                                tooltip = WorkflowConfig.getFileName(path),
                                tooltipPosition = TooltipPosition.Bottom()
                            ) {
                                Text(
                                    modifier = Modifier.padding(12.dp),
                                    text = WorkflowConfig.getFileName(path),
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false
                                )
                            }
                        }

                        Row(modifier = Modifier.constrainAs(deleteBtnRef) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end, 8.dp)
                            bottom.linkTo(parent.bottom)
                        }) {
                            SmallIconButton(
                                icon = Icons.Outlined.Delete,
                                onClick = {
                                    deleteWorkflowFileDialog.show(
                                        title = "Delete Workflow File",
                                        message = "Are you sure you want to delete this ${WorkflowConfig.getFileName(path)} file?",
                                        data = path
                                    )
                                },
                                tooltip = "Delete",
                                tooltipPosition = TooltipPosition.Bottom()
                            )
                        }
                    }

                    HorizontalDivider()
                }
            }
        }
        VerticalDivider(Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun itemBackground(component: BaseWorkflowComponent, path: String): Color {
    return if (path == component.openPath.collectAsState().value) colorScheme.surface.copy(0.8f) else colorScheme.surfaceContainer
}