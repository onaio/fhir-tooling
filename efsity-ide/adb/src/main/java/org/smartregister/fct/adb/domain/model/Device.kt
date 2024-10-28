package org.smartregister.fct.adb.domain.model

import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.adb.data.commands.AppDatabaseQueryCommand
import org.smartregister.fct.adb.data.commands.ExecuteRulesCommand
import org.smartregister.fct.adb.data.commands.ExecuteWorkflowCommand
import org.smartregister.fct.adb.data.commands.GetAllPackagesCommand
import org.smartregister.fct.adb.data.commands.GetInsightsCommand
import org.smartregister.fct.adb.data.controller.ADBController
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.engine.domain.model.PackageInfo

class Device internal constructor(internal val deviceId: String) : KoinComponent {

    private val controller: ADBController by inject()
    internal lateinit var deviceInfo: DeviceInfo

    fun getDeviceInfo(): DeviceInfo = deviceInfo

    suspend fun getAllPackages(filter: List<String>): Result<List<PackageInfo>> {
        return controller.executeCommand(GetAllPackagesCommand(filter), deviceId = deviceId)
    }

    suspend fun runAppDBQuery(arg: String): Result<JSONObject> {
        return runContentCommand { packageId ->
            controller.executeCommand(
                AppDatabaseQueryCommand(packageId, arg),
                deviceId = deviceId
            )
        }
    }

    suspend fun executeRules(arg: String): Result<JSONObject> {
        return runContentCommand { packageId ->
            controller.executeCommand(
                ExecuteRulesCommand(packageId, arg),
                deviceId = deviceId
            )
        }
    }

    suspend fun executeWorkflow(arg: String): Result<JSONObject> {
        return runContentCommand { packageId ->
            controller.executeCommand(
                ExecuteWorkflowCommand(packageId, arg),
                deviceId = deviceId
            )
        }
    }

    suspend fun getInsights(arg: String): Result<JSONObject> {
        return runContentCommand { packageId ->
            controller.executeCommand(
                GetInsightsCommand(packageId, arg),
                deviceId = deviceId
            )
        }
    }

    private suspend fun<T> runContentCommand(run: suspend (String) -> Result<T>) : Result<T> {
        val packageId = getPackageId() ?: return Result.failure(NullPointerException("Select Package Id"))
        return run(packageId)
    }

    private fun getPackageId() = DeviceManager.getActivePackage().value?.packageId
}