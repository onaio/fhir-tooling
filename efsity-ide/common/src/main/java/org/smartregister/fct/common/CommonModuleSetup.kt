package org.smartregister.fct.common

import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.smartregister.fct.engine.setup.ModuleSetup
import org.smartregister.fct.logger.FCTLogger

class CommonModuleSetup : ModuleSetup {

    private val commonModule = module(createdAtStart = true) {

    }

    override suspend fun setup() {
        FCTLogger.d("Loading... Common Module")
        GlobalContext.get().loadModules(listOf(commonModule))
        FCTLogger.d("Common Module Loaded")
    }
}