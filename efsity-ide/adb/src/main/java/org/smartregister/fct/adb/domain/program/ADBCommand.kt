package org.smartregister.fct.adb.domain.program

import org.smartregister.fct.adb.domain.model.CommandResult

internal interface ADBCommand<T> {

    fun process(response: String, dependentResult: List<CommandResult<*>>): Result<T>
    fun build(): List<String>
    fun getDependentCommands(): List<ADBCommand<*>> = ArrayList()
}