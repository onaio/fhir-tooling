package org.smartregister.fct.sm.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.smartregister.fct.sm.domain.model.SMModel
import org.smartregister.fct.sm.domain.repository.SMRepository

internal class GetAllSM(private val smRepository: SMRepository) {
    operator fun invoke(): Flow<List<SMModel>> = smRepository.getAll()
}