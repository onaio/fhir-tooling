package org.smartregister.fct.adb.data.commands

import org.json.JSONObject

internal class ExecuteWorkflowCommand(packageId: String, arg: String) : ContentCommand(packageId, arg) {

    override fun getMethodName(): String {
        return "execute_workflow"
    }

    override fun process(jsonObject: JSONObject): Result<JSONObject> {
        return Result.success(jsonObject)
    }
}