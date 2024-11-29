package org.smartregister.fct.workflow.domain.usecase

import org.smartregister.fct.workflow.domain.model.Workflow
import org.smartregister.fct.workflow.domain.repository.WorkflowRepository

internal class UpdateWorkflow(private val workflowRepository: WorkflowRepository) {
    suspend operator fun invoke(workflow: Workflow) = workflowRepository.update(workflow)
}