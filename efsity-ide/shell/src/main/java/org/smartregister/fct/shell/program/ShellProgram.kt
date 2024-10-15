package org.smartregister.fct.shell.program

interface ShellProgram {
    fun run(command: String): Result<String>
}