package org.smartregister.fct.device_database

import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.smartregister.fct.engine.setup.ModuleSetup
import org.smartregister.fct.logger.FCTLogger

class DeviceDBModuleSetup : ModuleSetup {

    private val deviceDBModule = module(createdAtStart = true) {

    }

    override suspend fun setup() {
        FCTLogger.d("Loading... Device Database Module")
        GlobalContext.get().loadModules(listOf(deviceDBModule))
        FCTLogger.d("Device Database Module Loaded")
    }
}