package org.smartregister.fct.sm.data.repository

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.sm.domain.datasource.SMDataSource
import org.smartregister.fct.sm.domain.model.SMModel
import org.smartregister.fct.sm.domain.repository.SMRepository

internal class SMSqlDelightRepository(private val smDataSource: SMDataSource) : SMRepository {

    override fun getAll(): Flow<List<SMModel>> = smDataSource.getAll()
    override suspend fun insert(smModel: SMModel) = smDataSource.insert(smModel)
    override suspend fun update(smModel: SMModel) = smDataSource.update(smModel)
    override suspend fun delete(id: String) = smDataSource.delete(id)
}