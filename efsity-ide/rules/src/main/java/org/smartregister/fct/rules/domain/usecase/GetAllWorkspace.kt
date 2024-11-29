package org.smartregister.fct.rules.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.rules.domain.model.Workspace
import org.smartregister.fct.rules.domain.repository.WorkspaceRepository

internal class GetAllWorkspace(private val workspaceRepository: WorkspaceRepository) {
    operator fun invoke(): Flow<List<Workspace>> = workspaceRepository.getAll()
}