package org.smartregister.fct.adb.data.commands

import org.json.JSONObject

internal class GetInsightsCommand(packageId: String, arg: String) : ContentCommand(packageId, arg) {

    override fun getMethodName(): String {
        return "get_insights"
    }

    override fun process(jsonObject: JSONObject): Result<JSONObject> {
        return Result.success(jsonObject)
    }
}