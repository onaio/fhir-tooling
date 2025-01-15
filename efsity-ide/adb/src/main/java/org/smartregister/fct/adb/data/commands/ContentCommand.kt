package org.smartregister.fct.adb.data.commands

import org.json.JSONObject
import org.smartregister.fct.adb.domain.model.CommandResult
import org.smartregister.fct.adb.domain.program.ADBCommand
import org.smartregister.fct.engine.util.compress
import org.smartregister.fct.engine.util.decompress
import org.smartregister.fct.engine.util.replaceLast
import org.smartregister.fct.logger.FCTLogger

internal abstract class ContentCommand(
    private val packageId: String,
    private val arg: String
) : ADBCommand<JSONObject> {

    override fun process(response: String, dependentResult: List<CommandResult<*>>): Result<JSONObject> {
        return try {
            val sanitizeResponse = response.replace("Result: Bundle[{data=", "").replaceLast("}]", "")
            val jsonObject = JSONObject(sanitizeResponse.decompress())
            return process(jsonObject)
        } catch (ex: Exception) {
            FCTLogger.e(ex)
            Result.failure(ex)
        }
    }

    open fun process(jsonObject: JSONObject) : Result<JSONObject> {
        return if (jsonObject.getBoolean("success")) {
            Result.success(jsonObject)
        } else {
            Result.failure(RuntimeException(jsonObject.getString("error")))
        }
    }

    override fun build(): List<String> {
        return listOf("content call --uri 'content://${packageId}.fct' --method '${getMethodName()}' --arg '${getCompressedArgument()}'")
    }

    private fun getCompressedArgument(): String {
        FCTLogger.d("Content Request: $arg")
        if (arg.trim().isEmpty()) return arg
        return arg.compress()
    }

    abstract fun getMethodName(): String
}