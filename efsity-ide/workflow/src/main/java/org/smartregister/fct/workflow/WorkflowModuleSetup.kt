package org.smartregister.fct.workflow

import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.smartregister.fct.database.Database
import org.smartregister.fct.engine.setup.ModuleSetup
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.workflow.data.datasource.WorkflowSqlDelightDataSource
import org.smartregister.fct.workflow.data.generator.LiteWorkflowGenerator
import org.smartregister.fct.workflow.data.respository.WorkflowSqlDelightRepository
import org.smartregister.fct.workflow.domain.datasource.WorkflowDataSource
import org.smartregister.fct.workflow.domain.repository.WorkflowRepository
import org.smartregister.fct.workflow.domain.usecase.CreateNewWorkflow
import org.smartregister.fct.workflow.domain.usecase.DeleteWorkflow
import org.smartregister.fct.workflow.domain.usecase.GetAllWorkflow
import org.smartregister.fct.workflow.domain.usecase.UpdateWorkflow

class WorkflowModuleSetup : ModuleSetup {

    private val workflowModule = module(createdAtStart = false) {
        single<WorkflowDataSource> { WorkflowSqlDelightDataSource(Database.getDatabase().workflowDaoQueries) }
        single<WorkflowRepository> { WorkflowSqlDelightRepository(get()) }
        single<GetAllWorkflow> { GetAllWorkflow(get()) }
        single<CreateNewWorkflow> { CreateNewWorkflow(get()) }
        single<UpdateWorkflow> { UpdateWorkflow(get()) }
        single<DeleteWorkflow> { DeleteWorkflow(get()) }
        single<LiteWorkflowGenerator> { LiteWorkflowGenerator(get(), get()) }
    }

    override suspend fun setup() {
        FCTLogger.d("Loading... Workflow Module")
        GlobalContext.get().loadModules(listOf(workflowModule))
        FCTLogger.d("Workflow Module Loaded")
    }
}