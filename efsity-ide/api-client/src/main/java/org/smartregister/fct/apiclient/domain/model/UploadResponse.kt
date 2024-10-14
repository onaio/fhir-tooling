package org.smartregister.fct.apiclient.domain.model

import org.hl7.fhir.r4.model.OperationOutcome

sealed class UploadResponse {

    data object Success : UploadResponse()

    data object UnAuthorized : UploadResponse()

    data class Failed(
        val outcome: OperationOutcome
    ) : UploadResponse()
}

