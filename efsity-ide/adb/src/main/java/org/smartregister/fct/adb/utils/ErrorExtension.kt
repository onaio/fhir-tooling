package org.smartregister.fct.adb.utils

import org.smartregister.fct.adb.data.exception.CommandException

internal fun<T> String.resultAsCommandException() = Result.failure<T>(CommandException(this))

internal fun String.takeIfNotError() = takeUnless { it.contains("error:") }