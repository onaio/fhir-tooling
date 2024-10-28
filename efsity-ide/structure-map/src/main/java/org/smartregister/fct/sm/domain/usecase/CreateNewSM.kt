package org.smartregister.fct.sm.domain.usecase

import org.smartregister.fct.sm.domain.model.SMModel
import org.smartregister.fct.sm.domain.repository.SMRepository

internal class CreateNewSM(private val smRepository: SMRepository) {
    suspend operator fun invoke(smModel: SMModel) = smRepository.insert(smModel)
}