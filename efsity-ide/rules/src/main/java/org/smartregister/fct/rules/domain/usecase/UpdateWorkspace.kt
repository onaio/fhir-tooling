package org.smartregister.fct.rules.domain.usecase

import org.smartregister.fct.rules.domain.model.Workspace
import org.smartregister.fct.rules.domain.repository.WorkspaceRepository

internal class UpdateWorkspace(private val workspaceRepository: WorkspaceRepository) {
    suspend operator fun invoke(workspace: Workspace) = workspaceRepository.update(workspace)
}