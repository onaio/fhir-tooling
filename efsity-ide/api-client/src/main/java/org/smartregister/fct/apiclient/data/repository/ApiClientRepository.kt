package org.smartregister.fct.apiclient.data.repository

import org.smartregister.fct.apiclient.domain.datasource.ApiClientDataSource
import org.smartregister.fct.apiclient.domain.model.AuthRequest
import org.smartregister.fct.apiclient.domain.model.AuthResponse
import org.smartregister.fct.apiclient.domain.model.Request
import org.smartregister.fct.apiclient.domain.model.Response
import org.smartregister.fct.apiclient.domain.model.UploadResourceRequest
import org.smartregister.fct.apiclient.domain.model.UploadResponse

internal class ApiClientRepository(private val dataSource: ApiClientDataSource)  {

    suspend fun auth(
       request: AuthRequest
    ) : AuthResponse = dataSource.auth(request)

    suspend fun upload(
        request: UploadResourceRequest
    ): UploadResponse = dataSource.upload(request)

    suspend fun request(
        request: Request
    ): Response = dataSource.request(request)
}