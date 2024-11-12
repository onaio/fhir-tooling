package org.smartregister.fct.pm.domain.usecase

import org.smartregister.fct.pm.domain.repository.PackageRepository

internal class SaveNewPackage(private val repository: PackageRepository) {

    operator fun invoke(id: String, packageId: String, packageName: String) = repository.insert(
        id = id,
        packageId = packageId,
        packageName = packageName
    )
}