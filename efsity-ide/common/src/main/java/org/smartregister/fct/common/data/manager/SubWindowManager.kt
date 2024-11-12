package org.smartregister.fct.common.data.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.smartregister.fct.common.data.enums.BottomWindowState
import org.smartregister.fct.common.data.enums.RightWindowState
import org.smartregister.fct.common.domain.model.ViewMode


class SubWindowManager {

    private val rightWindowState = MutableStateFlow<RightWindowState?>(null)
    private val bottomWindowState = MutableStateFlow<BottomWindowState?>(null)

    private val rightWindowsMode = mutableMapOf(
        Pair(RightWindowState.DeviceManager, ViewMode.Dock),
        Pair(RightWindowState.PackageManager, ViewMode.Dock),
    )

    private val bottomWindowsMode = mutableMapOf(
        Pair(BottomWindowState.Logcat, ViewMode.Dock),
    )

    private var _enableRightWindow = MutableStateFlow(false)
    val enableRightWindow: StateFlow<Boolean> = _enableRightWindow

    private var _rightWindowViewMode = MutableStateFlow(ViewMode.Dock)
    val rightWindowViewMode: StateFlow<ViewMode> = _rightWindowViewMode

    private var _enableBottomWindow = MutableStateFlow(false)
    val enableBottomWindow: StateFlow<Boolean> = _enableBottomWindow

    private var _bottomWindowViewMode = MutableStateFlow(ViewMode.Dock)
    val bottomWindowViewMode: StateFlow<ViewMode> = _bottomWindowViewMode

    fun changeRightWindowViewMode(state: RightWindowState, viewMode: ViewMode) {
        CoroutineScope(Dispatchers.IO).launch {
            rightWindowsMode[state] = viewMode
            _rightWindowViewMode.emit(viewMode)
        }
    }

    fun changeBottomWindowViewMode(state: BottomWindowState, viewMode: ViewMode) {
        CoroutineScope(Dispatchers.IO).launch {
            bottomWindowsMode[state] = viewMode
            _bottomWindowViewMode.emit(viewMode)
        }
    }

    fun getRightWindowState(): StateFlow<RightWindowState?> = rightWindowState

    fun setRightWindowState(state: RightWindowState?) {
        CoroutineScope(Dispatchers.IO).launch {
            val checkState = if (state == rightWindowState.value) null else state
            rightWindowState.emit(checkState)
            _enableRightWindow.emit(checkState != null)
        }
    }

    fun getBottomWindowState(): StateFlow<BottomWindowState?> = bottomWindowState

    fun setBottomWindowState(state: BottomWindowState?) {
        CoroutineScope(Dispatchers.IO).launch {
            val checkState = if (state == bottomWindowState.value) null else state
            bottomWindowState.emit(checkState)
            _enableBottomWindow.emit(checkState != null)
        }
    }
}