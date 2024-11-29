package org.smartregister.fct.workflow.presentation.ui.screen

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.common.presentation.ui.dialog.rememberLoaderDialogController
import org.smartregister.fct.common.presentation.ui.dialog.rememberSingleFieldDialog
import org.smartregister.fct.common.util.fileNameValidation
import org.smartregister.fct.sm.presentation.ui.components.rememberTransformationResultDialog
import org.smartregister.fct.workflow.data.enums.WorkflowType
import org.smartregister.fct.workflow.presentation.components.WorkflowScreenComponent
import org.smartregister.fct.workflow.presentation.ui.components.BlankWorkspace
import org.smartregister.fct.workflow.presentation.ui.components.ShowAllWorkflows
import org.smartregister.fct.workflow.presentation.ui.components.WorkflowContainer

context (BoxScope)
@Composable
fun WorkflowScreen(component: WorkflowScreenComponent) {

    val activeWorkflowComponent by component.activeWorkflowComponent.collectAsState()
    val workflowResult by component.workflowResult.collectAsState()
    val loaderController = rememberLoaderDialogController()
    val transformationResultDialog = rememberTransformationResultDialog(component)

    if (component.showLoader.collectAsState().value) {
        loaderController.show()
    } else {
        loaderController.hide()
    }

    if (workflowResult != null) {
        component.setWorkflowResult(null)
        transformationResultDialog.show(workflowResult)
    }

    val newWorkflowDialog = rememberSingleFieldDialog(
        title = "New Workflow",
        validations = listOf(fileNameValidation)
    ) { workflowName, controller ->
        component.createNewWorkflow(
            name = workflowName,
            type = controller.getExtra<WorkflowType>()!!
        )
    }

    activeWorkflowComponent?.run {

        Aurora(component) {

            it.showErrorSnackbar(component.error.collectAsState().value) {
                component.showError(null)
            }
            it.showSnackbar(component.info.collectAsState().value) {
                component.showInfo(null)
            }

            WorkflowContainer(this@run, newWorkflowDialog)
        }

    } ?: BlankWorkspace(component, newWorkflowDialog)

    ShowAllWorkflows(component)
}

