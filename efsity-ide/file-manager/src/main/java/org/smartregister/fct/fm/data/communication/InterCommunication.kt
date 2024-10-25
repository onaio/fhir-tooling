package org.smartregister.fct.fm.data.communication

import kotlinx.coroutines.flow.MutableStateFlow
import okio.Path

internal class InterCommunication {
    val pathReceived = MutableStateFlow<Path?>(null)
}