package org.smartregister.fct.adb.data.commands

import org.smartregister.fct.adb.data.enums.DeviceType
import org.smartregister.fct.adb.domain.model.BatteryAndOtherInfo
import org.smartregister.fct.adb.domain.model.CommandResult
import org.smartregister.fct.adb.domain.model.DeviceInfo
import org.smartregister.fct.adb.domain.program.ADBCommand
import org.smartregister.fct.adb.utils.CommandConstants
import org.smartregister.fct.adb.utils.resultAsCommandException
import org.smartregister.fct.adb.utils.takeIfNotError
import org.smartregister.fct.engine.domain.model.IntSize
import org.smartregister.fct.engine.util.Platform
import org.smartregister.fct.engine.util.PlatformType
import org.smartregister.fct.logger.FCTLogger

internal class GetDeviceInfoCommand : ADBCommand<DeviceInfo> {

    private val commandArgs: String

    init {
        val platform = Platform.getPlatform()
        when (platform) {
            PlatformType.Windows -> {
                commandArgs = listOf(
                    "getprop | findstr ",
                    StringBuilder().apply {
                        append("\"")
                        append(CommandConstants.DEVICE_ID)
                        append(" ")
                        append(CommandConstants.DEVICE_API_LEVEL)
                        append(" ")
                        append(CommandConstants.DEVICE_OS_VERSION)
                        append(" ")
                        append(CommandConstants.DEVICE_MANUFACTURER)
                        append(" ")
                        append(CommandConstants.DEVICE_MODEL)
                        append(" ")
                        append(CommandConstants.DEVICE_NAME)
                        append(" ")
                        append(CommandConstants.DEVICE_NAME_DEFAULT)
                        append(" ")
                        append(CommandConstants.DEVICE_TYPE)
                        append("\"")
                    }.toString(),
                ).joinToString("")
            }

            else -> {
                commandArgs = listOf(
                    "getprop | grep ",
                    StringBuilder().apply {
                        append("\"")
                        append(CommandConstants.DEVICE_ID)
                        append("\\|")
                        append(CommandConstants.DEVICE_API_LEVEL)
                        append("\\|")
                        append(CommandConstants.DEVICE_OS_VERSION)
                        append("\\|")
                        append(CommandConstants.DEVICE_MANUFACTURER)
                        append("\\|")
                        append(CommandConstants.DEVICE_MODEL)
                        append("\\|")
                        append(CommandConstants.DEVICE_NAME)
                        append("\\|")
                        append(CommandConstants.DEVICE_NAME_DEFAULT)
                        append("\\|")
                        append(CommandConstants.DEVICE_TYPE)
                        append("\"")
                    }.toString(),
                ).joinToString("")
            }
        }
    }

    override fun process(
        response: String,
        dependentResult: List<CommandResult<*>>
    ): Result<DeviceInfo> {

        return response
            .takeIfNotError()
            ?.split("\n")
            ?.associate {
                val row = it
                    .replace(": ", ":")
                    .replace("]", "")
                    .replace("[", "")
                    .split(":")

                row[0] to row[1]
            }
            ?.let { map ->

                // log otherInfo errors
                dependentResult.filter { it.result.isFailure }.forEach {
                    FCTLogger.e(it.result.exceptionOrNull())
                }

                val otherInfo = dependentResult
                    .filter { it.result.isSuccess }
                    .flatMap { listOf(it.result.getOrThrow() as Map<String, String>) }
                    .firstOrNull()


                val deviceType = map[CommandConstants.DEVICE_TYPE]
                    ?.let { if (it.contains("generic")) DeviceType.Virtual else DeviceType.Physical }
                    ?: DeviceType.Unknown

                DeviceInfo(
                    id = map[CommandConstants.DEVICE_ID] ?: "N/A",
                    name = getDeviceName(map) ?: "N/A",
                    model = map[CommandConstants.DEVICE_MODEL] ?: "N/A",
                    version = map[CommandConstants.DEVICE_OS_VERSION] ?: "N/A",
                    apiLevel = map[CommandConstants.DEVICE_API_LEVEL] ?: "N/A",
                    manufacturer = map[CommandConstants.DEVICE_MANUFACTURER] ?: "N/A",
                    type = deviceType,
                    resolution = getResolution(otherInfo),
                    batteryAndOtherInfo = BatteryAndOtherInfo(
                        level = otherInfo?.get("level")?.toInt() ?: 100,
                        temperature = otherInfo?.get("temperature")?.toInt(),
                        acPowered = otherInfo?.get("AC powered")?.toBoolean() ?: false,
                        usbPowered = otherInfo?.get("USB powered")?.toBoolean() ?: false,
                        wirelessPowered = otherInfo?.get("Wireless Powered")?.toBoolean() ?: false,
                        dockPowered = otherInfo?.get("Dock powered")?.toBoolean() ?: false
                    )
                )
            }
            ?.let {
                Result.success(it)
            } ?: response.resultAsCommandException()

    }

    private fun getDeviceName(map: Map<String, String>): String? {
        val name = map[CommandConstants.DEVICE_NAME_DEFAULT]

        return if (name != null && name.trim().isNotEmpty()) {
            name
        } else {
            map[CommandConstants.DEVICE_MODEL]
        }
    }

    private fun getResolution(map: Map<String, String>?): IntSize? {

        return map?.get(CommandConstants.DEVICE_RESOLUTION)
            ?.replace(" ", "")
            ?.replace("Point(", "")
            ?.replace(")", "")
            ?.split(",")
            ?.let { IntSize(it[0].toInt(), it[1].toInt()) }
    }

    override fun build(): List<String> {
        return listOf(commandArgs)
    }

    override fun getDependentCommands(): List<ADBCommand<*>> {
        return listOf(GetBatteryAndOtherInfoCommand())
    }
}