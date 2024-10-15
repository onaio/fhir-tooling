package org.smartregister.fct.shell.program

import org.smartregister.fct.shell.evalBash

class KScriptShellProgram : ShellProgram {

    @Synchronized
    override fun run(command: String): Result<String> {
        return try {
            val evalBash = evalBash(command)
            val result = evalBash.getOrThrow()

            if (result.trim().isEmpty() && evalBash.stderr.toList().isNotEmpty()) {
                val errorMessage = evalBash
                    .stderr
                    .filter { !it.contains("\t") }
                    .joinToString("\n")
                    .trim()
                Result.failure(RuntimeException(errorMessage))
            } else {
                Result.success(result)
            }

        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}