package org.smartregister.fct.adb.data.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.smartregister.fct.adb.domain.model.CommandResult
import org.smartregister.fct.adb.domain.program.ADBCommand
import org.smartregister.fct.shell.program.ShellProgram
import org.smartregister.fct.logger.FCTLogger

@Suppress("UNCHECKED_CAST")
internal class ADBController(private val shellProgram: ShellProgram) {

    suspend fun <T> executeCommand(
        command: ADBCommand<T>,
        deviceId: String? = null,
        shell: Boolean = true,
    ): Result<T> {

        val dependentResult = mutableListOf<CommandResult<*>>()
        command.getDependentCommands().forEach {
            executeBatch(it, deviceId, shell, dependentResult)
        }

        return execute(command, deviceId, shell, dependentResult).result as Result<T>
    }

    private suspend fun executeBatch(
        command: ADBCommand<*>,
        deviceId: String? = null,
        shell: Boolean = true,
        dependentResult: MutableList<CommandResult<*>>
    ) {
        if (command.getDependentCommands().isNotEmpty()) {
            command.getDependentCommands().forEach {
                executeBatch(it, deviceId, shell, dependentResult)
            }
        } else {
            dependentResult.add(execute(command, deviceId, shell, dependentResult))
        }
    }

    private suspend fun execute(
        command: ADBCommand<*>,
        deviceId: String? = null,
        shell: Boolean = true,
        dependentResult: List<CommandResult<*>>
    ): CommandResult<*> {

        val commandList = mutableListOf<String>().apply {
            command.build().forEachIndexed { index, cmd ->

                if (index > 0) {
                    add("&")
                }

                add("adb")
                if (deviceId != null) {
                    add("-s")
                    add(deviceId)
                }
                if (shell) add("shell")
                add(cmd)
            }
        }

        val asyncResult = CoroutineScope(Dispatchers.Default).async {
            shellProgram.run(commandList.joinToString(" ")
                .also { FCTLogger.d(it, tag = command.javaClass.simpleName) }
            )
        }
        val result = asyncResult.await()

        return if (result.isSuccess) {
            try {
                CommandResult(
                    command = command,
                    result = command.process(result.getOrThrow(), dependentResult)
                )
            } catch (t: Throwable) {
                FCTLogger.e(t)
                CommandResult(
                    command = command,
                    result = result
                )
            }
        } else {
            CommandResult(
                command = command,
                result = result
            )
        }
    }
}