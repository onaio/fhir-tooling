package org.smartregister.fct.apiclient.util

import org.hl7.fhir.r4.model.OperationOutcome
import org.smartregister.fct.apiclient.domain.model.AuthResponse

internal fun Exception.asOperationOutcome() = OperationOutcome().apply {
    addIssue().apply {
        severity = OperationOutcome.IssueSeverity.ERROR
        code = OperationOutcome.IssueType.EXCEPTION
        diagnostics = message
    }
}

internal fun AuthResponse.Failed.asOperationOutcome() = OperationOutcome().apply {
    addIssue().apply {
        severity = OperationOutcome.IssueSeverity.ERROR
        code = OperationOutcome.IssueType.SECURITY
        diagnostics = "Error: $error" + (description?.let { "\nDescription: $it" } ?: "")
    }
}