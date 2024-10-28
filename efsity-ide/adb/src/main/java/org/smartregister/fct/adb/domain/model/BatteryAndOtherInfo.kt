package org.smartregister.fct.adb.domain.model

data class BatteryAndOtherInfo(
    val level: Int,
    val temperature: Int?,
    val acPowered: Boolean,
    val usbPowered: Boolean,
    val wirelessPowered: Boolean,
    val dockPowered: Boolean
) {

    fun anyPoweredSource(): Boolean {
        return acPowered || usbPowered || wirelessPowered || dockPowered
    }

    fun getCelsiusTemperature(): Float? {
        return temperature?.let {
            it.toFloat() / 10f
        }
    }
}
