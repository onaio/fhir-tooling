package org.smartregister.fct.engine.domain.usecase

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.smartregister.fct.engine.domain.model.AppSetting
import org.smartregister.fct.engine.util.decodeJson
import sqldelight.AppSettingsDaoQueries

class GetAppSetting(private val appSettingDao: AppSettingsDaoQueries) {

    operator fun invoke(): Flow<AppSetting> {
        return appSettingDao
            .select()
            .asFlow()
            .mapToOne(Dispatchers.IO)
            .map { it.settings.decodeJson() }
    }
}