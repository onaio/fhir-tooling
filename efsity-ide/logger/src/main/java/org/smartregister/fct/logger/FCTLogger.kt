package org.smartregister.fct.logger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.apache.commons.collections4.queue.CircularFifoQueue
import org.smartregister.fct.logger.model.Log
import org.smartregister.fct.logger.model.LogFilter
import org.smartregister.fct.logger.model.LogLevel
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

object FCTLogger {

    private const val MAXIMUM_LOG_LIMIT = 1000
    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    private var logFilters = mutableMapOf<Any, LogFilter>()
    private val isPause = MutableStateFlow(false)
    private val logChain = CircularFifoQueue<Log>(MAXIMUM_LOG_LIMIT)
    private val lastLog = MutableSharedFlow<Log?>()

    fun listen(): SharedFlow<Log?> = lastLog

    fun clearLogs() {
        CoroutineScope(Dispatchers.IO).launch {
            logChain.clear()
            logFilters.entries.forEach {
                it.value.onClear()
            }
        }
    }

    fun togglePause() {
        CoroutineScope(Dispatchers.IO).launch {
            isPause.emit(!isPause.value)
        }
    }

    fun getAllLogs(): List<Log> = logChain.toList()

    fun getPause(): StateFlow<Boolean> = isPause

    fun addFilter(key: Any, logFilter: LogFilter) {
        logFilters[key] = logFilter
    }

    fun w(message: String, tag: String? = null) {
        prepareLog(
            priority = LogLevel.WARNING,
            message = message,
            tag = tag,
        )
    }

    fun v(message: String, tag: String? = null) {
        prepareLog(
            priority = LogLevel.VERBOSE,
            message = message,
            tag = tag,
        )
    }

    fun d(message: String, tag: String? = null) {
        prepareLog(
            priority = LogLevel.DEBUG,
            message = message,
            tag = tag,
        )
    }

    fun i(message: String, tag: String? = null) {
        prepareLog(
            priority = LogLevel.INFO,
            message = message,
            tag = tag,
        )
    }

    fun e(t: Throwable?, tag: String? = null) {
        prepareLog(
            priority = LogLevel.ERROR,
            t = t,
            tag = tag,
        )
    }

    fun e(message: String?, tag: String? = null) {
        prepareLog(
            priority = LogLevel.ERROR,
            message = message,
            tag = tag,
        )
    }

    fun wtf(message: String, tag: String? = null) {
        prepareLog(
            priority = LogLevel.ASSERT,
            message = message,
            tag = tag,
        )
    }

    private fun prepareLog(
        priority: LogLevel,
        message: String? = null,
        t: Throwable? = null,
        tag: String?,
    ) {
        var msg = message

        msg = if (msg.isNullOrEmpty()) {
            if (t == null) return
            getStackTraceString(t)
        } else {
            msg
        }

        push(
            Log(
                dateTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                tag = parseTag(tag),
                message = msg.replace("\t", "    "),
                priority = priority
            )
        )
    }

    private fun push(log: Log) {

        val isLoggable = logFilters.entries.all { it.value.isLoggable(log) }
        if (!isLoggable || isPause.value) return

        CoroutineScope(Dispatchers.IO).launch {
            lastLog.emit(log)
            logChain.add(log)
        }
    }

    private fun parseTag(tag: String?): String {
        return tag ?: Throwable()
            .stackTrace
            .first { it.className !in listOf(FCTLogger::class.java.name) }
            .let(FCTLogger::createStackElementTag)
    }

    private fun createStackElementTag(element: StackTraceElement): String {
        var tag = element.className.substringAfterLast('.')
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return tag
    }

    private fun getStackTraceString(t: Throwable): String {
        val sw = StringWriter(256)
        val pw = PrintWriter(sw, false)
        t.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }
}