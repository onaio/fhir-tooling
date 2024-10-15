package org.smartregister.fct.workflow.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.workflow.domain.model.Workflow
import org.smartregister.fct.workflow.domain.repository.WorkflowRepository

internal class GetAllWorkflow(private val workflowRepository: WorkflowRepository) {
    operator fun invoke(): Flow<List<Workflow>> = workflowRepository.getAll()
}