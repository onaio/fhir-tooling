package org.smartregister.fct.logger.model

data class Log(
    val dateTime: String,
    val priority: LogLevel,
    val tag: String,
    val message: String
)