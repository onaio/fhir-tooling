package org.smartregister.fct.adb.domain.model

import org.smartregister.fct.adb.domain.program.ADBCommand

internal data class CommandResult<T>(
    val command: ADBCommand<T>,
    val result: Result<*>
)
