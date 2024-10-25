package org.smartregister.fct.adb.data.commands

import org.smartregister.fct.adb.domain.model.CommandResult
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.adb.domain.program.ADBCommand
import org.smartregister.fct.adb.utils.CommandConstants
import org.smartregister.fct.adb.utils.resultAsCommandException

internal class GetAllDevicesCommand : ADBCommand<List<Device>> {

    override fun process(response: String, dependentResult: List<CommandResult<*>>): Result<List<Device>> {
        return response
            .takeIf { it.contains("\t") }
            ?.split("\n")
            ?.filter { it.contains("\t") }
            ?.filterNot { it.contains("offline") }
            ?.map {
                Device(
                    deviceId = it.split("\t")[0]
                )
            }
            ?.toList()
            ?.let { Result.success(it) }
            ?: response.resultAsCommandException()
    }

    override fun build(): List<String> {
        return listOf(CommandConstants.DEVICES)
    }
}