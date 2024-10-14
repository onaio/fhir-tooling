package org.smartregister.fct.sm.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import org.hl7.fhir.r4.model.Resource
import org.smartregister.fct.editor.presentation.components.CodeEditorComponent
import org.smartregister.fct.engine.data.enums.FileType
import org.smartregister.fct.engine.util.encodeResourceToString
import org.smartregister.fct.engine.util.logicalId

internal class StructureMapResultTabComponent(
    componentContext: ComponentContext,
    val resource: Resource
) : ComponentContext by componentContext {

    val codeEditorComponent =
        instanceKeeper.getOrCreate("code-editor-component-${resource.logicalId}-${resource.hashCode()}") {
            CodeEditorComponent(
                componentContext = componentContext,
                text = resource.encodeResourceToString(),
                fileType = FileType.Json,
            ).apply {
                formatJson()
            }
        }
}