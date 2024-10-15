package org.smartregister.fct.sm.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.ResourceType
import org.smartregister.fct.aurora.presentation.ui.components.ExtendedFloatingActionButton
import org.smartregister.fct.aurora.presentation.ui.components.LinearIndicator
import org.smartregister.fct.aurora.presentation.ui.components.ScrollableTabRow
import org.smartregister.fct.aurora.presentation.ui.components.Tab
import org.smartregister.fct.aurora.presentation.ui.components.TextButton
import org.smartregister.fct.common.data.controller.DialogController
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.common.presentation.ui.dialog.rememberConfirmationDialog
import org.smartregister.fct.common.presentation.ui.dialog.rememberDialog
import org.smartregister.fct.common.presentation.ui.dialog.rememberResourceUploadDialog
import org.smartregister.fct.device_database.ui.components.QueryTabComponent
import org.smartregister.fct.editor.presentation.components.CodeEditorComponent
import org.smartregister.fct.editor.presentation.ui.view.CodeEditor
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.readableResourceName
import org.smartregister.fct.json.JsonStyle
import org.smartregister.fct.json.JsonTree
import org.smartregister.fct.json.JsonTreeView
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.sm.data.enums.ResultType
import org.smartregister.fct.sm.data.enums.inverse
import org.smartregister.fct.sm.presentation.component.StructureMapResultTabComponent

@Composable
fun rememberTransformationResultDialog(
    componentContext: ComponentContext,
    title: String = "Transformation Result",
): DialogController<Bundle> {

    val dialogController = rememberDialog<Bundle>(
        width = 1200.dp,
        height = 800.dp,
        title = title,
    ) { _, bundle ->

        SMTransformationResult(
            componentContext = componentContext,
            bundle = bundle ?: Bundle()
        )
    }

    return dialogController
}


@Composable
private fun SMTransformationResult(
    componentContext: ComponentContext,
    bundle: Bundle
) {

    var activeTabIndex by remember { mutableStateOf(0) }

    val component = if (bundle.hasEntry()) StructureMapResultTabComponent(
        componentContext = componentContext,
        resource = bundle.entry[activeTabIndex].resource
    ) else null

    val codeEditorComponent = component?.takeIf {
        it.resource.resourceType == ResourceType.StructureMap
    }?.codeEditorComponent

    val loading = remember { mutableStateOf(false) }
    val info = remember { mutableStateOf<String?>(null) }
    val error = remember { mutableStateOf<String?>(null) }

    Aurora(
        componentContext = componentContext
    ) {

        with(it) {
            showSnackbar(info.value)
            showErrorSnackbar(error.value)
        }

        Box {
            Column(modifier = Modifier.fillMaxSize()) {

                ConstraintLayout(
                    modifier = Modifier.fillMaxWidth(),
                ) {

                    val (left, right) = createRefs()

                    ScrollableTabRow(
                        modifier = Modifier.constrainAs(left) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            end.linkTo(right.start)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.preferredWrapContent
                        },
                        selectedTabIndex = activeTabIndex,
                    ) {
                        bundle.entry.forEachIndexed { index, entry ->
                            Tab(
                                selected = index == activeTabIndex,
                                title = entry.resource.readableResourceName,
                                onClick = {
                                    activeTabIndex = index
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .height(40.dp)
                            .constrainAs(right) {
                                top.linkTo(parent.top)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            }
                    ) {

                        if (codeEditorComponent != null && activeTabIndex == 0) {
                            with(componentContext) {
                                UploadOnServerButton(codeEditorComponent)
                                UploadOnDeviceButton(codeEditorComponent, loading, info, error)
                            }
                            Spacer(Modifier.width(12.dp))
                        }
                    }
                }

                if (component != null) {
                    with(component) {
                        Content()
                    }
                }
            }

            if (loading.value) {
                LinearIndicator()
            }
        }
    }
}

context (StructureMapResultTabComponent)
@Composable
private fun Content() {
    var resultType by remember { mutableStateOf(ResultType.Json) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                label = resultType.inverse.label,
                icon = resultType.inverse.icon,
                onClick = {
                    resultType = resultType.inverse
                }
            )
        }
    ) {
        Box(Modifier.padding(it)) {

            when (resultType) {
                ResultType.Json -> {
                    CodeEditor(
                        component = codeEditorComponent,
                        enableFileImport = false,
                        enableDBImport = false
                    )
                }

                ResultType.Tree -> {
                    val jsonTree = JsonTree(
                        key = codeEditorComponent,
                        json = codeEditorComponent.getText()
                    )
                    JsonTreeView(
                        modifier = Modifier.fillMaxSize(),
                        tree = jsonTree,
                        style = JsonStyle(MaterialTheme.colorScheme)
                    )
                    LaunchedEffect(codeEditorComponent) {
                        jsonTree.expandRoot()
                    }
                }
            }
        }
    }
}

@Composable
private fun ComponentContext.UploadOnServerButton(component: CodeEditorComponent) {

    val resourceUploadDialog = rememberResourceUploadDialog(
        componentContext = this
    )

    TextButton(
        label = "Upload on Server",
        shape = RectangleShape,
        onClick = {
            resourceUploadDialog.show(component.getText())
        }
    )
}

@Composable
private fun ComponentContext.UploadOnDeviceButton(
    component: CodeEditorComponent,
    loading: MutableState<Boolean>,
    info: MutableState<String?>,
    error: MutableState<String?>,
) {

    val confirmationDialogController = rememberConfirmationDialog<Unit> { _, _ ->

        componentScope.launch {
            loading.value = true
            val queryTabComponent = QueryTabComponent(this@UploadOnDeviceButton)
            val result = queryTabComponent.updateRecordByResourceId(
                serializedResource = component.getText()
            )
            loading.value = false
            if (result.isSuccess) {
                info.value = "StructureMap successfully uploaded in device."
            } else {
                error.value = result.exceptionOrNull()?.message
                FCTLogger.e(result.exceptionOrNull() ?: UnknownError("Query Error"))
            }
            delay(200)
            info.value = null
            error.value = null
        }
    }

    TextButton(
        label = "Upload on Device",
        shape = RectangleShape,
        onClick = {
            confirmationDialogController.show(
                title = "Upload",
                message = "Are you sure you want to upload this in device?"
            )
        }
    )
}