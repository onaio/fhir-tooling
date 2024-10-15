package org.smartregister.fct.sm.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import org.smartregister.fct.editor.presentation.ui.view.CodeEditor
import org.smartregister.fct.sm.presentation.component.StructureMapScreenComponent
import org.smartregister.fct.sm.util.SMConfig

@Composable
internal fun SourceEditor(component: StructureMapScreenComponent) {

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
                    text = SMConfig.getFileName(component.openPath.collectAsState().value ?: "")
                )
            }
        }
    )
}