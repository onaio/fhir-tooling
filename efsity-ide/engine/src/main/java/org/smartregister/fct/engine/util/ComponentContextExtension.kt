package org.smartregister.fct.engine.util

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob

fun ComponentContext.componentScope(dispatcher: MainCoroutineDispatcher = Dispatchers.Main.immediate) : CoroutineScope {
    return coroutineScope(dispatcher + SupervisorJob())
}

val ComponentContext.componentScope: CoroutineScope
    get() = componentScope()