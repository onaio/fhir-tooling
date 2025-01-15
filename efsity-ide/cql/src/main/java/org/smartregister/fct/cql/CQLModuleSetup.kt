package org.smartregister.fct.cql

import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.smartregister.fct.engine.setup.ModuleSetup
import org.smartregister.fct.logger.FCTLogger

class CQLModuleSetup : ModuleSetup {

    private val cqlModule = module(createdAtStart = true) {

    }

    override suspend fun setup() {
        FCTLogger.d("Loading... CQL Module")
        GlobalContext.get().loadModules(listOf(cqlModule))
        FCTLogger.d("CQL Module Loaded")
    }
}