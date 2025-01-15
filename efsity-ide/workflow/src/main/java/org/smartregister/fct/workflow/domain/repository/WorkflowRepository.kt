package org.smartregister.fct.workflow.domain.repository

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.workflow.domain.model.Workflow

internal interface WorkflowRepository {

    fun getAll(): Flow<List<Workflow>>
    suspend fun insert(workflow: Workflow)
    suspend fun update(workflow: Workflow)
    suspend fun delete(id: String)
}