package org.smartregister.fct.adb

import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.smartregister.fct.adb.data.commands.GetAllDevicesCommand
import org.smartregister.fct.adb.data.commands.GetBatteryAndOtherInfoCommand
import org.smartregister.fct.adb.data.commands.GetDeviceInfoCommand
import org.smartregister.fct.adb.data.controller.ADBController
import org.smartregister.fct.shell.program.KScriptShellProgram
import org.smartregister.fct.shell.program.ShellProgram
import org.smartregister.fct.engine.setup.ModuleSetup
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.logger.model.Log
import org.smartregister.fct.logger.model.LogFilter

class ADBModuleSetup : ModuleSetup {

    private val adbModule = module(createdAtStart = true) {
        single<ShellProgram> { KScriptShellProgram() }
        single<ADBController> { ADBController(get()) }
    }

    override suspend fun setup() {
        FCTLogger.d("Loading... ADB Module")
        GlobalContext.get().loadModules(listOf(adbModule))
        FCTLogger.addFilter(this, ADBCommandFilter())
        FCTLogger.d("ADB Module Loaded")
    }

    private class ADBCommandFilter : LogFilter {

        override fun isLoggable(log: Log): Boolean {
            return log.tag.let {
                it != GetAllDevicesCommand::class.java.simpleName &&
                it != GetDeviceInfoCommand::class.java.simpleName &&
                it != GetBatteryAndOtherInfoCommand::class.java.simpleName
            }
        }

    }
}