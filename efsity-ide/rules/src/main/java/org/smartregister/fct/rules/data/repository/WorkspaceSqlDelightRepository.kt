package org.smartregister.fct.rules.data.repository

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.rules.domain.datasource.WorkspaceDataSource
import org.smartregister.fct.rules.domain.model.Workspace
import org.smartregister.fct.rules.domain.repository.WorkspaceRepository

internal class WorkspaceSqlDelightRepository(private val workspaceDataSource: WorkspaceDataSource) :
    WorkspaceRepository {

    override fun getAll(): Flow<List<Workspace>> = workspaceDataSource.getAll()
    override suspend fun insert(workspace: Workspace) = workspaceDataSource.insert(workspace)
    override suspend fun update(workspace: Workspace) = workspaceDataSource.update(workspace)
    override suspend fun delete(id: String) = workspaceDataSource.delete(id)
}