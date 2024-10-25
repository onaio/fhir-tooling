package org.smartregister.fct.apiclient.domain.model

import org.hl7.fhir.r4.model.OperationOutcome

sealed class Response {

    data class Success(
        val httpStatusCode: Int,
        val httpStatus: String,
        val response: String
    ) : Response()
    data class Failed(
        val httpStatusCode: Int,
        val httpStatus: String,
        val outcome: OperationOutcome
    ) : Response()
}

