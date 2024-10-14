package org.smartregister.fct.adb.domain.model

import org.smartregister.fct.adb.data.enums.DeviceType
import org.smartregister.fct.engine.domain.model.IntSize

data class DeviceInfo(
    val id: String,
    val name: String,
    val model: String,
    val version: String,
    val apiLevel: String,
    val manufacturer: String,
    val type: DeviceType,
    val resolution: IntSize?,
    val batteryAndOtherInfo: BatteryAndOtherInfo
) {

    fun getAllBasicDetail() : String {
        return "$name ($id) Android $version, API $apiLevel"
    }
}