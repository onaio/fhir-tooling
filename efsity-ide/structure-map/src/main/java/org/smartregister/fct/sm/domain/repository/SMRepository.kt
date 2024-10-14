package org.smartregister.fct.sm.domain.repository

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.sm.domain.model.SMModel

internal interface SMRepository {

    fun getAll(): Flow<List<SMModel>>
    suspend fun insert(smModel: SMModel)
    suspend fun update(smModel: SMModel)
    suspend fun delete(id: String)
}