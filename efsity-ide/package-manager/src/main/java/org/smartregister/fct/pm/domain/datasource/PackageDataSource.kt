package org.smartregister.fct.pm.domain.datasource

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.engine.domain.model.PackageInfo

internal interface PackageDataSource {

    fun getAllPackages(device: Device, filter: List<String>): Flow<List<PackageInfo>>
    fun getSavedPackages() : Flow<List<PackageInfo>>
    fun insert(id: String, packageId: String, packageName: String)
    fun update(id: String, packageName: String)
    fun delete(id: String)
}