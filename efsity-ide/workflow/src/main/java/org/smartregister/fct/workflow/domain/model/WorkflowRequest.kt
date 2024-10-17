package org.smartregister.fct.workflow.domain.model

import kotlinx.serialization.Serializable
import org.smartregister.fct.workflow.data.enums.WorkflowType

@Serializable
internal data class WorkflowRequest(
    val type: WorkflowType,
    val planDefinition: String,
    val subject: String,
    val otherResource: List<String>
)
