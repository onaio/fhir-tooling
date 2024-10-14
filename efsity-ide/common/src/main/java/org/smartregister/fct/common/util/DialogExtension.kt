package org.smartregister.fct.common.util

import org.smartregister.fct.logger.FCTLogger

fun <T> List<Any?>.getOrThrow(index: Int): T = this[index] as T

fun <T> List<Any?>.getOrDefault(index: Int, default: T): T =
    getOrDefault<T, T>(index, default) { result, _ -> result }

fun <T, R> List<Any?>.getOrDefault(index: Int, default: R, map: (T, R) -> R): R {
    return try {
        map(getOrThrow(index), default)
    } catch (ex: Exception) {
        FCTLogger.e(ex)
        default
    }
}