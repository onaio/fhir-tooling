package org.smartregister.fct.rules.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.smartregister.fct.engine.util.compress
import org.smartregister.fct.engine.util.decodeJson
import org.smartregister.fct.engine.util.decompress
import org.smartregister.fct.engine.util.encodeJson
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.rules.domain.datasource.WorkspaceDataSource
import org.smartregister.fct.rules.domain.model.Workspace
import sqldelight.RuleDaoQueries

internal class WorkspaceSqlDelightDataSource(private val ruleDao: RuleDaoQueries) : WorkspaceDataSource {

    override fun getAll(): Flow<List<Workspace>> {
        return ruleDao
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map {
                it.mapNotNull { record ->
                    try {
                        Workspace(
                            id = record.id,
                            name = record.name,
                            dataSources = record.sources.decompress().decodeJson(),
                            rules = record.rules.decompress().decodeJson(),
                        )
                    } catch (t: Throwable) {
                        FCTLogger.e(t)
                        null
                    }
                }
            }
    }

    override suspend fun insert(workspace: Workspace) {
        ruleDao.insert(
            id = workspace.id,
            name = workspace.name,
            sources = workspace.dataSources.encodeJson().compress(),
            rules = workspace.rules.encodeJson().compress()
        )
    }

    override suspend fun update(workspace: Workspace) {
        ruleDao.update(
            id = workspace.id,
            name = workspace.name,
            sources = workspace.dataSources.encodeJson().compress(),
            rules = workspace.rules.encodeJson().compress()
        )
    }

    override suspend fun delete(id: String) {
        ruleDao.delete(id)
    }
}