package org.smartregister.fct.logger.model

interface LogFilter {
    fun isLoggable(log: Log): Boolean = true

    fun onClear() = Unit
}