package org.smartregister.fct.common.util

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.flow.MutableStateFlow

val LocalWindowState =
    compositionLocalOf<WindowState> { error("window controller not provided") }

val windowTitle = MutableStateFlow("Dashboard")
val allResourcesSyncedStatus = MutableStateFlow<List<Pair<String, Int>>?>(null)
val appVersion = MutableStateFlow<String?>(null)
val buildDate = MutableStateFlow<String?>(null)