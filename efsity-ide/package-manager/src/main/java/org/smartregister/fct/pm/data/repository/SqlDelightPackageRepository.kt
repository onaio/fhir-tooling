package org.smartregister.fct.pm.data.repository

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.engine.domain.model.PackageInfo
import org.smartregister.fct.pm.domain.datasource.PackageDataSource
import org.smartregister.fct.pm.domain.repository.PackageRepository

internal class SqlDelightPackageRepository(private val dataSource: PackageDataSource) : PackageRepository {
    override fun getAllPackages(device: Device, filter: List<String>): Flow<List<PackageInfo>> =
        dataSource.getAllPackages(device, filter)

    override fun getSavedPackages(): Flow<List<PackageInfo>> = dataSource.getSavedPackages()
    override fun insert(id: String, packageId: String, packageName: String) =
        dataSource.insert(id, packageId, packageName)

    override fun update(id: String, packageName: String) = dataSource.update(id, packageName)

    override fun delete(id: String) = dataSource.delete(id)

}