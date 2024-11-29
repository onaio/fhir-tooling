package org.smartregister.fct.workflow.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import org.smartregister.fct.editor.presentation.ui.view.CodeEditor
import org.smartregister.fct.workflow.presentation.components.BaseWorkflowComponent
import org.smartregister.fct.workflow.util.WorkflowConfig

@Composable
internal fun WorkflowEditor(component: BaseWorkflowComponent) {

    CodeEditor(
        component = component.codeEditorComponent,
        fetchFileImport = component::updateOpenedFileContent,
        fetchDBImport = component::updateOpenedFileContent,
        toolbarOptions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = WorkflowConfig.getFileName(
                        component.openPath.collectAsState().value ?: ""
                    )
                )
            }
        }
    )
}