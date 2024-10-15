package org.smartregister.fct.pm.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.engine.domain.model.PackageInfo
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.pm.domain.datasource.PackageDataSource
import sqldelight.PackageInfoDaoQueries

internal class SqlDelightPackageDataSource(private val packageInfoDao: PackageInfoDaoQueries) : PackageDataSource {

    override fun getAllPackages(device: Device, filter: List<String>): Flow<List<PackageInfo>> {
        return flow {
            device
                .getAllPackages(filter)
                .takeIf { it.isSuccess }
                ?.getOrNull()
                ?.run { emit(this@run) }
        }.flowOn(Dispatchers.IO)
    }

    override fun getSavedPackages(): Flow<List<PackageInfo>> {
        return packageInfoDao.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                it.map { pi ->
                    PackageInfo(
                        id = pi.id,
                        packageId = pi.package_id,
                        name = pi.package_name
                    )
                }
            }
    }

    override fun insert(id: String, packageId: String, packageName: String) {
        packageInfoDao.insert(id, packageId, packageName)
        FCTLogger.d("New Package Saved(id = $id, packageId = $packageId, packageName = $packageName)")
    }

    override fun update(id: String, packageName: String) {
        packageInfoDao.update(packageName, id)
        FCTLogger.d("Package Updated(id = $id, packageName = $packageName)")
    }

    override fun delete(id: String) {
        packageInfoDao.delete(id)
        FCTLogger.d("Package Deleted(id = $id)")
    }

}