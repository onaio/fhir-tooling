package org.smartregister.fct.workflow.data.respository

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.workflow.domain.datasource.WorkflowDataSource
import org.smartregister.fct.workflow.domain.model.Workflow
import org.smartregister.fct.workflow.domain.repository.WorkflowRepository

internal class WorkflowSqlDelightRepository(private val workflowDataSource: WorkflowDataSource) :
    WorkflowRepository {

    override fun getAll(): Flow<List<Workflow>> = workflowDataSource.getAll()
    override suspend fun insert(workflow: Workflow) = workflowDataSource.insert(workflow)
    override suspend fun update(workflow: Workflow) = workflowDataSource.update(workflow)
    override suspend fun delete(id: String) = workflowDataSource.delete(id)
}