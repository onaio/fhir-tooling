package org.smartregister.fct.pm.domain.usecase

import org.smartregister.fct.pm.domain.repository.PackageRepository

internal class UpdatePackage(private val repository: PackageRepository) {

    operator fun invoke(id: String, packageName: String) = repository.update(
        id = id,
        packageName = packageName
    )
}