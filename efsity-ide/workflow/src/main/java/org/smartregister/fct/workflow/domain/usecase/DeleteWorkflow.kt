package org.smartregister.fct.workflow.domain.usecase

import org.smartregister.fct.workflow.domain.repository.WorkflowRepository

internal class DeleteWorkflow(private val workflowRepository: WorkflowRepository) {
    suspend operator fun invoke(id: String) = workflowRepository.delete(id)
}