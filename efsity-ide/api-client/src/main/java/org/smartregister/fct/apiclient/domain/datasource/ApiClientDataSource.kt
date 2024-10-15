package org.smartregister.fct.apiclient.domain.datasource

import org.smartregister.fct.apiclient.domain.model.AuthRequest
import org.smartregister.fct.apiclient.domain.model.AuthResponse
import org.smartregister.fct.apiclient.domain.model.Request
import org.smartregister.fct.apiclient.domain.model.Response
import org.smartregister.fct.apiclient.domain.model.UploadResourceRequest
import org.smartregister.fct.apiclient.domain.model.UploadResponse

internal interface ApiClientDataSource {

    suspend fun auth(
        request: AuthRequest
    ) : AuthResponse

    suspend fun upload(
       request: UploadResourceRequest
    ) : UploadResponse

    suspend fun request(
        request: Request
    ) : Response
}