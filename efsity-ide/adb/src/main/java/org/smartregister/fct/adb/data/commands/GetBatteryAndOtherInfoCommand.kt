package org.smartregister.fct.adb.data.commands

import org.smartregister.fct.adb.domain.model.CommandResult
import org.smartregister.fct.adb.domain.program.ADBCommand
import org.smartregister.fct.adb.utils.CommandConstants
import org.smartregister.fct.adb.utils.resultAsCommandException
import org.smartregister.fct.adb.utils.takeIfNotError
import org.smartregister.fct.engine.util.Platform
import org.smartregister.fct.engine.util.PlatformType

internal class GetBatteryAndOtherInfoCommand : ADBCommand<Map<String, String>> {

    override fun process(
        response: String,
        dependentResult: List<CommandResult<*>>
    ): Result<Map<String, String>> {
        return response
            .takeIfNotError()
            ?.split("\n")
            ?.filter { it.contains(":") || it.contains("=") }
            ?.associate {
                val row = it
                    .takeIf { it.contains(":") }
                    ?.replace(": ", ":")
                    ?.split(":") ?: it.split("=")
                row[0].trim() to row[1].trim()
            }?.let {
                Result.success(it)
            } ?: response.resultAsCommandException()
    }

    override fun build(): List<String> {
        val platform = Platform.getPlatform()

        val searchCommand = if (platform == PlatformType.Windows) {
            "findstr"
        } else {
            "grep"
        }

        return listOf(
            "dumpsys battery",
            "dumpsys display | $searchCommand \"${CommandConstants.DEVICE_RESOLUTION}\""
        )
    }
}