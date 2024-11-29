package org.smartregister.fct.sm.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.common.presentation.ui.dialog.rememberLoaderDialogController
import org.smartregister.fct.common.presentation.ui.dialog.rememberSingleFieldDialog
import org.smartregister.fct.common.util.fileNameValidation
import org.smartregister.fct.sm.presentation.component.StructureMapScreenComponent
import org.smartregister.fct.sm.presentation.ui.components.BlankWorkspace
import org.smartregister.fct.sm.presentation.ui.components.MainContainer
import org.smartregister.fct.sm.presentation.ui.components.ShowAllStructureMaps
import org.smartregister.fct.sm.presentation.ui.components.rememberTransformationResultDialog

@Composable
fun StructureMapScreen(component: StructureMapScreenComponent) {

    val activeStructureMap by component.activeStructureMap.collectAsState()
    val workflowResult by component.structureMapResult.collectAsState()
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
        title = "New StructureMap",
        validations = listOf(fileNameValidation)
    ) { workflowName, _ ->
        component.createNewStructureMap(workflowName)
    }

    activeStructureMap?.run {

        Aurora(component) {

            it.showErrorSnackbar(component.error.collectAsState().value) {
                component.showError(null)
            }
            it.showSnackbar(component.info.collectAsState().value) {
                component.showInfo(null)
            }

            MainContainer(component, newWorkflowDialog)
        }

    } ?: BlankWorkspace(component, newWorkflowDialog)

    ShowAllStructureMaps(component)
}