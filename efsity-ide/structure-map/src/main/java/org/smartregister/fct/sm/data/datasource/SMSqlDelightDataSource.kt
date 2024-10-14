package org.smartregister.fct.sm.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.sm.domain.datasource.SMDataSource
import org.smartregister.fct.sm.domain.model.SMModel
import sqldelight.SMDaoQueries

internal class SMSqlDelightDataSource(private val smDao: SMDaoQueries) : SMDataSource {

    override fun getAll(): Flow<List<SMModel>> {
        return smDao
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map {
                it.mapNotNull { sm ->
                    try {
                        SMModel(
                            id = sm.id,
                            name = sm.name,
                            mapPath = sm.map_path,
                            sourcePath = sm.source_path,
                        )
                    } catch (t: Throwable) {
                        FCTLogger.e(t)
                        null
                    }
                }
            }
    }

    override suspend fun insert(smModel: SMModel) {
        smDao.insert(
            id = smModel.id,
            name = smModel.name,
            map_path = smModel.mapPath,
            source_path = smModel.sourcePath
        )
    }

    override suspend fun update(smModel: SMModel) {
        smDao.update(
            id = smModel.id,
            name = smModel.name,
            map_path = smModel.mapPath,
            source_path = smModel.sourcePath
        )
    }

    override suspend fun delete(id: String) {
        smDao.delete(id)
    }
}