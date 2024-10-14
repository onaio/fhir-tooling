package org.smartregister.fct.adb.data.commands

internal class AppDatabaseQueryCommand(packageId: String, arg: String) : ContentCommand(packageId, arg) {

    override fun getMethodName(): String {
        return "db_operation"
    }

}