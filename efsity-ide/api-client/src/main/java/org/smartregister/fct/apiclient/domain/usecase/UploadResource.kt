package org.smartregister.fct.apiclient.domain.usecase

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.apiclient.data.repository.ApiClientRepository
import org.smartregister.fct.apiclient.domain.model.UploadResourceRequest
import org.smartregister.fct.apiclient.domain.model.UploadResponse

class UploadResource : KoinComponent {

    private val repository: ApiClientRepository by inject()

    suspend operator fun invoke(
        request: UploadResourceRequest
    ): UploadResponse = repository.upload(request)
}