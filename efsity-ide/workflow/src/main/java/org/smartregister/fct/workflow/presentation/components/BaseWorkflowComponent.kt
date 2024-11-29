package org.smartregister.fct.workflow.presentation.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.StructureMap
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.editor.presentation.components.CodeEditorComponent
import org.smartregister.fct.engine.data.enums.FileType
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.decodeJson
import org.smartregister.fct.engine.util.decodeResourceFromString
import org.smartregister.fct.engine.util.encodeJson
import org.smartregister.fct.engine.util.encodeResourceToString
import org.smartregister.fct.engine.util.prettyJson
import org.smartregister.fct.fm.util.FileUtil
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.sm.data.transformation.SMTransformService
import org.smartregister.fct.workflow.data.enums.WorkflowType
import org.smartregister.fct.workflow.data.generator.LiteWorkflowGenerator
import org.smartregister.fct.workflow.domain.model.Workflow
import org.smartregister.fct.workflow.domain.model.WorkflowRequest
import org.smartregister.fct.workflow.domain.model.WorkflowResponse
import org.smartregister.fct.workflow.util.WorkflowConfig
import org.smartregister.fct.workflow.util.createWorkflowFilePath

internal abstract class BaseWorkflowComponent(
    val workflow: Workflow,
    val screenComponent: WorkflowScreenComponent
) : KoinComponent, ComponentContext by screenComponent {

    private val transformService: SMTransformService by inject()
    private val liteWorkflowGenerator: LiteWorkflowGenerator by inject()

    internal val codeEditorComponent =
        CodeEditorComponent(screenComponent, fileType = FileType.Json)

    private val _openPath = MutableStateFlow<String?>(null)
    internal val openPath: StateFlow<String?> = _openPath

    init {
        openPath(workflow.config.getLastOpenPath())
    }

    open fun onDestroy() {
        saveOpenedWorkflowFile()
    }

    internal fun openPath(path: String) {
        componentScope.launch {

            // save current file
            saveOpenedWorkflowFile()

            // update open path
            _openPath.emit(path)

            // read file content
            val content = FileUtil.readFile(path.toPath())

            // set file type
            codeEditorComponent.setFileType(
                if (isStructureMapContent(content)) {
                    FileType.StructureMap
                } else {
                    FileType.Json
                }
            )

            // update content on editor
            codeEditorComponent.setText(content)

            // update last open path in config
            workflow.config.updateLastOpenPath(path)
        }
    }

    internal fun createNewWorkflowFile(fileName: String) {
        componentScope.launch {

            with(workflow.config) {
                val existingFileNames = (otherResourcesPath + listOf(
                    planDefinitionPath, subjectPath
                )).map {
                    WorkflowConfig.getFileName(it).lowercase()
                }

                if (fileName.lowercase() in existingFileNames) {
                    screenComponent.showError("$fileName already exists")
                    return@launch
                }

                // build path from name
                val filePath = workflow.createWorkflowFilePath(fileName)

                // create new file in storage
                FileUtil.writeFile(filePath.toPath())

                // add file-path in other resources list
                addOtherResourcePath(filePath)

                // save/update active workflow
                screenComponent.saveWorkflow(workflow)

                // open workflow file
                openPath(filePath)
            }


        }
    }

    internal fun deleteWorkflowFile(path: String) {
        componentScope.launch {

            // open plan-def file
            openPath(workflow.config.planDefinitionPath)

            // delete file path from other resources list
            workflow.config.removeOtherResourcePath(path)

            // save/update workflow
            screenComponent.saveWorkflow(workflow)

            // show info
            screenComponent.showInfo("${WorkflowConfig.getFileName(path)} has been successfully deleted")

            delay(1000)

            // delete file from storage
            FileUtil.deleteFile(path.toPath())
        }
    }

    internal fun updateOpenedFileContent(text: String) {
        componentScope.launch {
            try {
                val resource = text.decodeResourceFromString<Resource>()

                if (_openPath.value == workflow.config.planDefinitionPath && resource.resourceType != ResourceType.PlanDefinition) {
                    screenComponent.showError("Resource should be PlanDefinition")
                    return@launch
                }

                codeEditorComponent.setFileType(FileType.Json)
                codeEditorComponent.setText(text.prettyJson())
            } catch (ex: Exception) {
                codeEditorComponent.setText(text)
                if (isStructureMapContent(text)) {
                    codeEditorComponent.setFileType(FileType.StructureMap)
                } else {
                    FCTLogger.e(ex)
                    screenComponent.showError(ex.message)
                }
            }
        }
    }

    internal fun execute() {
        componentScope.launch(Dispatchers.Default) {
            try {

                // save open workflow file
                saveOpenedWorkflowFile()

                // show loader
                screenComponent.showLoader(true)

                val planDefinition = FileUtil
                    .readFile(workflow.config.planDefinitionPath.toPath())
                    .decodeResourceFromString<PlanDefinition>()

                val subject = FileUtil
                    .readFile(workflow.config.subjectPath.toPath())
                    .decodeResourceFromString<Resource>()

                val resources = mutableListOf<String>()
                workflow.config.otherResourcesPath.forEach { path ->

                    val content = FileUtil.readFile(path.toPath())

                    // transform content to StructureMap resource
                    if (isStructureMapContent(content)) {
                        val result = transformService.transform(content)

                        if (result.isSuccess) {
                            resources.add(
                                result.getOrThrow().entry.map { it.resource }
                                    .filterIsInstance<StructureMap>().first()
                                    .encodeResourceToString()
                            )
                        } else {
                            FCTLogger.e(result.exceptionOrNull())
                            screenComponent.showError(
                                "${WorkflowConfig.getFileName(path)}\n${result.exceptionOrNull()?.message}"
                            )
                            return@launch
                        }

                    } else {

                        // check content is valid fhir resource
                        content.decodeResourceFromString<Resource>()
                        resources.add(content)
                    }
                }

                // generate workflow
                val response = when(workflow.type) {

                    WorkflowType.Lite -> {

                        // intentional delayed local process is very fast
                        delay(500)

                        liteWorkflowGenerator.generate(
                            planDefinition = planDefinition,
                            subject = subject,
                            otherResource = resources
                        )
                    }

                    WorkflowType.Apply -> {

                        val device = DeviceManager.getActiveDevice()

                        if (device == null) {
                            screenComponent.showError("No device selected")
                            return@launch
                        }

                        val result = device.executeWorkflow(
                            WorkflowRequest(
                                type = workflow.type,
                                planDefinition = planDefinition.encodeResourceToString(),
                                subject = subject.encodeResourceToString(),
                                otherResource = resources
                            ).encodeJson()
                        )

                        if (result.isFailure) {
                            throw result.exceptionOrNull()!!
                        }

                        result.getOrThrow().toString().decodeJson<WorkflowResponse>()
                    }
                }

                // hide loader
                screenComponent.showLoader(false)

                //val response = result.getOrThrow().toString().decodeJson<WorkflowResponse>()
                if (response.error == null) {
                    screenComponent.setWorkflowResult(
                        response.result
                            .map { it.decodeResourceFromString<Resource>() }
                            .let {
                                Bundle().apply {

                                    resources
                                        .map { it.decodeResourceFromString<Resource>() }
                                        .filterIsInstance<StructureMap>()
                                        .forEach { smResource ->
                                            addEntry().apply {
                                                setResource(smResource)
                                            }
                                        }

                                    it.forEach { resource ->
                                        addEntry().apply {
                                            setResource(resource)
                                        }
                                    }
                                }
                            }

                    )
                } else {
                    FCTLogger.e(response.error)
                    screenComponent.showError(response.error)
                }

            } catch (ex: Exception) {
                screenComponent.showLoader(false)
                FCTLogger.e(ex)
                screenComponent.showError(ex.message)
            }
        }
    }

    private fun saveOpenedWorkflowFile() {
        _openPath.value?.let { path ->
            FileUtil.writeFile(path.toPath(), codeEditorComponent.getText())
        }
    }

    private fun isStructureMapContent(content: String): Boolean {
        return content.trim().isNotEmpty() && content.length > 3 && content.substring(0, 3)
            .contains("map", ignoreCase = true)
    }
}