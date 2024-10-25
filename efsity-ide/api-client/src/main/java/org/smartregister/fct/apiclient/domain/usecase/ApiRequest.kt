package org.smartregister.fct.apiclient.domain.usecase

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.apiclient.data.repository.ApiClientRepository
import org.smartregister.fct.apiclient.domain.model.Request
import org.smartregister.fct.apiclient.domain.model.Response

class ApiRequest : KoinComponent {

    private val repository: ApiClientRepository by inject()

    suspend operator fun invoke(
        request: Request
    ): Response = repository.request(request)
}