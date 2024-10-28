package org.smartregister.fct.workflow.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.smartregister.fct.engine.util.decodeJson
import org.smartregister.fct.engine.util.encodeJson
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.workflow.data.enums.WorkflowType
import org.smartregister.fct.workflow.domain.datasource.WorkflowDataSource
import org.smartregister.fct.workflow.domain.model.Workflow
import sqldelight.WorkflowDaoQueries

internal class WorkflowSqlDelightDataSource(private val workflowDao: WorkflowDaoQueries) :
    WorkflowDataSource {

    override fun getAll(): Flow<List<Workflow>> {
        return workflowDao
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map {
                it.mapNotNull { record ->
                    try {
                        Workflow(
                            id = record.id,
                            name = record.name,
                            type = WorkflowType.valueOf(record.type),
                            config = record.config.decodeJson()
                        )
                    } catch (t: Throwable) {
                        FCTLogger.e(t)
                        null
                    }
                }
            }
    }

    override suspend fun insert(workflow: Workflow) {
        workflowDao.insert(
            id = workflow.id,
            name = workflow.name,
            type = workflow.type.name,
            config = workflow.config.encodeJson()
        )
    }

    override suspend fun update(workflow: Workflow) {
        workflowDao.update(
            id = workflow.id,
            name = workflow.name,
            type = workflow.type.name,
            config = workflow.config.encodeJson()
        )
    }

    override suspend fun delete(id: String) {
        workflowDao.delete(id)
    }
}