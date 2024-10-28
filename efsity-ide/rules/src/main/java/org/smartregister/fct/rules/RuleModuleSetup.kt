package org.smartregister.fct.rules

import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.smartregister.fct.database.Database
import org.smartregister.fct.engine.setup.ModuleSetup
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.rules.data.datasource.WorkspaceSqlDelightDataSource
import org.smartregister.fct.rules.data.repository.WorkspaceSqlDelightRepository
import org.smartregister.fct.rules.domain.datasource.WorkspaceDataSource
import org.smartregister.fct.rules.domain.repository.WorkspaceRepository
import org.smartregister.fct.rules.domain.usecase.CreateNewWorkspace
import org.smartregister.fct.rules.domain.usecase.DeleteWorkspace
import org.smartregister.fct.rules.domain.usecase.GetAllWorkspace
import org.smartregister.fct.rules.domain.usecase.UpdateWorkspace

class RuleModuleSetup : ModuleSetup {

    private val ruleModule = module(createdAtStart = true) {
        single<WorkspaceDataSource> { WorkspaceSqlDelightDataSource(Database.getDatabase().ruleDaoQueries) }
        single<WorkspaceRepository> { WorkspaceSqlDelightRepository(get()) }
        single<GetAllWorkspace> { GetAllWorkspace(get()) }
        single<CreateNewWorkspace> { CreateNewWorkspace(get()) }
        single<UpdateWorkspace> { UpdateWorkspace(get()) }
        single<DeleteWorkspace> { DeleteWorkspace(get()) }
    }

    override suspend fun setup() {
        FCTLogger.d("Loading... Rule Designer Module")
        GlobalContext.get().loadModules(listOf(ruleModule))
        FCTLogger.d("Rule Designer Module Loaded")
    }
}