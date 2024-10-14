package org.smartregister.fct.engine.domain.usecase

import org.smartregister.fct.engine.domain.model.AppSetting
import org.smartregister.fct.engine.util.encodeJson
import sqldelight.AppSettingsDaoQueries

class UpdateAppSetting(private val appSettingDao: AppSettingsDaoQueries) {

    operator fun invoke(appSetting: AppSetting) {
        appSettingDao.update(
            id = "1",
            settings = appSetting.encodeJson()
        )
    }
}