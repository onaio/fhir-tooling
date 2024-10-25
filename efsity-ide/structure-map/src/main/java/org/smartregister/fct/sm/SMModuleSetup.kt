package org.smartregister.fct.sm

import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.smartregister.fct.database.Database
import org.smartregister.fct.engine.setup.ModuleSetup
import org.smartregister.fct.engine.util.getKoinInstance
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.sm.data.datasource.SMSqlDelightDataSource
import org.smartregister.fct.sm.data.repository.SMSqlDelightRepository
import org.smartregister.fct.sm.data.transformation.SMTransformService
import org.smartregister.fct.sm.domain.datasource.SMDataSource
import org.smartregister.fct.sm.domain.repository.SMRepository
import org.smartregister.fct.sm.domain.usecase.CreateNewSM
import org.smartregister.fct.sm.domain.usecase.DeleteSM
import org.smartregister.fct.sm.domain.usecase.GetAllSM
import org.smartregister.fct.sm.domain.usecase.UpdateSM

class SMModuleSetup : ModuleSetup {

    private val smModule = module(createdAtStart = true) {
        single<SMDataSource> { SMSqlDelightDataSource(Database.getDatabase().sMDaoQueries) }
        single<SMRepository> { SMSqlDelightRepository(get()) }
        single<GetAllSM> { GetAllSM(get()) }
        single<CreateNewSM> { CreateNewSM(get()) }
        single<UpdateSM> { UpdateSM(get()) }
        single<DeleteSM> { DeleteSM(get()) }
        single<SMTransformService> { SMTransformService(get(), get()) }
    }

    override suspend fun setup() {
        FCTLogger.d("Loading... Structure Map Module")
        GlobalContext.get().loadModules(listOf(smModule))
        getKoinInstance<SMTransformService>().init()
        FCTLogger.d("Structure Map Module Loaded")
    }
}