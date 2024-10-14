package org.smartregister.fct.pm.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.engine.domain.model.PackageInfo
import org.smartregister.fct.pm.domain.repository.PackageRepository

internal class GetSavedPackages(private val repository: PackageRepository) {

    operator fun invoke(): Flow<List<PackageInfo>> =
        repository.getSavedPackages()
}