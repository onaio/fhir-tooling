package org.smartregister.fct.workflow.domain.model

import org.smartregister.fct.workflow.data.enums.WorkflowType

internal data class Workflow(
    val id: String,
    val name: String,
    val type: WorkflowType,
    val config: Config
)
