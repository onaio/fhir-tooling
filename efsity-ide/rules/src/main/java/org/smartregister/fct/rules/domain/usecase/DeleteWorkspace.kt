package org.smartregister.fct.rules.domain.usecase

import org.smartregister.fct.rules.domain.repository.WorkspaceRepository

internal class DeleteWorkspace(private val workspaceRepository: WorkspaceRepository) {
    suspend operator fun invoke(id: String) = workspaceRepository.delete(id)
}