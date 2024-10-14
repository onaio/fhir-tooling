package org.smartregister.fct.common.data.locals

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.common.data.manager.SubWindowManager

val LocalRootComponent = staticCompositionLocalOf<ComponentContext?> { null }
val LocalSubWindowManager = staticCompositionLocalOf { SubWindowManager() }
val LocalSnackbarHost = staticCompositionLocalOf { SnackbarHostState() }