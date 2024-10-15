package org.smartregister.fct.workflow.domain.model

import kotlinx.serialization.Serializable

@Serializable
internal data class WorkflowResponse(
    var error: String?,
    val result: List<String> = listOf()
)
