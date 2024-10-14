package org.smartregister.fct.pm.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.engine.domain.model.PackageInfo
import org.smartregister.fct.pm.domain.repository.PackageRepository

internal class GetAllPackages(private val repository: PackageRepository) {

    operator fun invoke(device: Device, filter: List<String> = listOf()): Flow<List<PackageInfo>> =
        repository.getAllPackages(device, filter)
}