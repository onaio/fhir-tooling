package org.smartregister.fct.workflow.util

import org.smartregister.fct.workflow.domain.model.Workflow

internal fun Workflow.createWorkflowFilePath(fileName: String): String {
    return "${WorkflowConfig.getWorkflowPath(id)}${
        fileName.replace(
            " ",
            "_"
        )
    }.txt"
}