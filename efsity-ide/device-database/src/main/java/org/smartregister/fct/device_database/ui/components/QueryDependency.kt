package org.smartregister.fct.device_database.ui.components

import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.engine.domain.model.PackageInfo

interface QueryDependency {
    suspend fun getRequiredParam(showErrors: Boolean = true, info: suspend (Device, PackageInfo) -> Unit)
}