package org.smartregister.fct.adb.utils

internal object CommandConstants {
    const val DEVICES = "devices"
    const val DEVICE_ID = "ro.serialno"
    const val DEVICE_API_LEVEL = "ro.build.version.sdk"
    const val DEVICE_OS_VERSION = "ro.build.version.release"
    const val DEVICE_MANUFACTURER = "ro.product.manufacturer"
    const val DEVICE_MODEL = "ro.product.model"
    const val DEVICE_NAME = "ro.product.name"
    const val DEVICE_NAME_DEFAULT = "ro.product.product.tran.device.name.default"
    const val DEVICE_TYPE = "ro.product.system.device"
    const val DEVICE_RESOLUTION = "mStableDisplaySize"
}