package org.smartregister.fct.workflow.presentation.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import org.hl7.fhir.r4.model.Bundle
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.common.presentation.component.ScreenComponent
import org.smartregister.fct.common.util.windowTitle
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.uuid
import org.smartregister.fct.fm.util.FileUtil
import org.smartregister.fct.workflow.data.enums.WorkflowType
import org.smartregister.fct.workflow.domain.model.Config
import org.smartregister.fct.workflow.domain.model.Workflow
import org.smartregister.fct.workflow.domain.usecase.CreateNewWorkflow
import org.smartregister.fct.workflow.domain.usecase.DeleteWorkflow
import org.smartregister.fct.workflow.domain.usecase.GetAllWorkflow
import org.smartregister.fct.workflow.domain.usecase.UpdateWorkflow
import org.smartregister.fct.workflow.util.WorkflowConfig

class WorkflowScreenComponent(private val componentContext: ComponentContext) :
    ScreenComponent, KoinComponent, ComponentContext by componentContext
{

    private val createWorkflow: CreateNewWorkflow by inject()
    private val updateWorkflow: UpdateWorkflow by inject()
    private val getAllWorkflow: GetAllWorkflow by inject()
    private val deleteWorkflow: DeleteWorkflow by inject()

    internal var allWorkflows: List<Workflow> = listOf()
        private set

    private val _error = MutableStateFlow<String?>(null)
    internal val error: StateFlow<String?> = _error

    private val _info = MutableStateFlow<String?>(null)
    internal val info: StateFlow<String?> = _info

    private val _showLoader = MutableStateFlow(false)
    internal val showLoader: StateFlow<Boolean> = _showLoader

    private val _showAllWorkflowPanel = MutableStateFlow(false)
    internal val showAllWorkflowPanel: StateFlow<Boolean> = _showAllWorkflowPanel

    private val _workflowResult = MutableStateFlow<Bundle?>(null)
    internal val workflowResult: StateFlow<Bundle?> = _workflowResult

    private val _activeWorkflowComponent = MutableStateFlow<BaseWorkflowComponent?>(null)
    internal val activeWorkflowComponent: StateFlow<BaseWorkflowComponent?> = _activeWorkflowComponent

    init {
        WorkflowConfig.activeWorkflow.let {
            if (it != null) {
                openWorkflow(it)
            } else {
                setWindowTitle(null)
            }
        }
        componentScope.launch {
            getAllWorkflow().collectLatest {
                allWorkflows = it
            }
        }
    }

    override fun onDestroy() {
        _activeWorkflowComponent.value?.onDestroy()
    }

    internal fun showError(error: String?) {
        componentScope.launch {
            _error.emit(error)
        }
    }

    internal fun showInfo(info: String?) {
        componentScope.launch {
            _info.emit(info)
        }
    }

    internal fun showLoader(isShow: Boolean) {
        componentScope.launch {
            _showLoader.emit(isShow)
        }
    }

    internal fun toggleAllWorkflowPanel() {
        componentScope.launch {
            _showAllWorkflowPanel.emit(!_showAllWorkflowPanel.value)
        }
    }

    internal fun createNewWorkflow(name: String, type: WorkflowType) {
        componentScope.launch {

            val workflowId = uuid()
            val planDefinitionPath = WorkflowConfig.getPlanDefinitionPath(workflowId)
            val subjectPath = WorkflowConfig.getSubjectPath(workflowId)

            // create default empty plan-definition
            FileUtil.writeFile(planDefinitionPath)

            // create default patient subject
            FileUtil.writeFile(subjectPath, WorkflowConfig.SAMPLE_SUBJECT)

            // create new workflow
            val workflow = Workflow(
                id = workflowId,
                name = name,
                type = type,
                config = Config(
                    planDefinitionPath = planDefinitionPath.toFile().path,
                    subjectPath = subjectPath.toFile().path,
                    otherResourcesPath = mutableListOf()
                )
            )

            // insert new workflow in database
            createWorkflow(workflow)

            // open newly created workflow and save active workflow
            openWorkflow(workflow, true)
        }
    }

    internal fun saveWorkflow(workflow: Workflow) {
        componentScope.launch {
            updateWorkflow(workflow)
        }
    }

    internal fun openWorkflow(workflow: Workflow, saveActiveWorkflow: Boolean = false) {
        componentScope.launch {

            if (saveActiveWorkflow) {
                _activeWorkflowComponent.value?.workflow?.let(::saveWorkflow)
            }

            val activeWorkflowComp = when (workflow.type) {
                WorkflowType.Lite -> LiteWorkflowComponent(workflow, this@WorkflowScreenComponent)
                WorkflowType.Apply -> ApplyWorkflowComponent(workflow, this@WorkflowScreenComponent)
            }

            _activeWorkflowComponent.value?.onDestroy()
            _activeWorkflowComponent.emit(activeWorkflowComp)
            setWindowTitle(workflow)
            WorkflowConfig.activeWorkflow = workflow
        }
    }

    internal fun deleteWorkflow(workflow: Workflow) {

        componentScope.launch {

            // delete active workflow folder
            FileUtil.deleteFolder(WorkflowConfig.getWorkflowPath(workflow.id).toPath())

            // delete workflow from database
            deleteWorkflow(workflow.id)
        }
    }

    internal fun setWorkflowResult(bundle: Bundle?) {
        componentScope.launch {
            _workflowResult.emit(bundle)
        }
    }

    private fun setWindowTitle(workflow: Workflow?) {
        componentScope.launch {
            val name = if (workflow != null) " ${workflow.type.name} - ${workflow.name}" else ""
            windowTitle.emit("Workflow$name")
        }
    }
}