package org.smartregister.fct.rules.domain.repository

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.rules.domain.model.Workspace

internal interface WorkspaceRepository {

    fun getAll(): Flow<List<Workspace>>
    suspend fun insert(workspace: Workspace)
    suspend fun update(workspace: Workspace)
    suspend fun delete(id: String)
}