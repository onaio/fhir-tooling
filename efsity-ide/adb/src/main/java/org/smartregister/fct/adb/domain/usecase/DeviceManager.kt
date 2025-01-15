package org.smartregister.fct.adb.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.adb.data.commands.GetAllDevicesCommand
import org.smartregister.fct.adb.data.commands.GetDeviceInfoCommand
import org.smartregister.fct.adb.data.controller.ADBController
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.domain.model.PackageInfo
import org.smartregister.fct.logger.FCTLogger

object DeviceManager : KoinComponent {

    private val appSettingManager: AppSettingManager by inject()
    private val controller: ADBController by inject()
    private val devices = MutableSharedFlow<List<Device?>>()
    private val selectedPackage = MutableStateFlow<PackageInfo?>(null)
    private val activeDevice = MutableStateFlow<Device?>(null)

    init {
        start()
    }

    private fun start() {

        CoroutineScope(Dispatchers.Default).launch {
            appSettingManager.appSetting.getPackageInfoAsFlow().collectLatest {
                selectedPackage.emit(it)
            }
        }
        CoroutineScope(Dispatchers.Default).launch {

            delay(1000)
            while (true) {
                val deviceList = controller.executeCommand(GetAllDevicesCommand(), shell = false)
                try {
                    if (deviceList.isSuccess) {
                        deviceList.getOrThrow().forEach {
                            val deviceInfo = controller.executeCommand(
                                GetDeviceInfoCommand(),
                                deviceId = it.deviceId
                            )
                            it.deviceInfo = deviceInfo.getOrThrow()
                        }
                    }

                    deviceList.getOrDefault(listOf())
                        .run {
                            if (activeDevice.value == null) {
                                activeDevice.emit(firstOrNull())
                            } else if (activeDevice.value!!.deviceId !in map { it.deviceId }) {
                                activeDevice.emit(lastOrNull())
                            }
                        }

                    devices.emit(deviceList.getOrDefault(listOf()))
                } catch (ex: Throwable) {
                    FCTLogger.e(ex)
                }
                delay(4000)
            }
        }
    }

    suspend fun setActiveDevice(device: Device?) {
        activeDevice.emit(device)
    }

    fun getActiveDevice(): Device? {
        return activeDevice.value
    }

    fun listenActiveDevice(): StateFlow<Device?> {
        return activeDevice
    }

    fun getAllDevices(): Flow<List<Device?>> = devices

    suspend fun setActivePackage(packageInfo: PackageInfo?) {
        FCTLogger.i("Package Changed(id = ${packageInfo?.id}, packageId = ${packageInfo?.packageId}, packageName = ${packageInfo?.name})")
        selectedPackage.emit(packageInfo)
        with(appSettingManager) {
            appSetting.updatePackageInfo(packageInfo)
            update()
        }
    }

    fun getActivePackage(): StateFlow<PackageInfo?> = selectedPackage

}