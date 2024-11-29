package org.smartregister.fct.rules.domain.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.smartregister.fct.engine.domain.model.IntSize
import org.smartregister.fct.rules.data.enums.Placement

@Serializable
internal data class Widget<T : java.io.Serializable>(
    var body: T,
    var x: Float = 0f,
    var y: Float = 0f,
    var size: IntSize = IntSize.Zero,
    var placement: Placement = Placement.Left,

    @kotlinx.serialization.Transient
    var parents: List<Widget<out java.io.Serializable>> = listOf(),

    @kotlinx.serialization.Transient
    var warnings: List<String> = listOf(),

    ) {

    @kotlinx.serialization.Transient
    private var _flash = MutableStateFlow(false)

    @kotlinx.serialization.Transient
    internal val flash: StateFlow<Boolean> = _flash

    @kotlinx.serialization.Transient
    private var _isSelected = MutableStateFlow(false)

    @kotlinx.serialization.Transient
    internal val isSelected: StateFlow<Boolean> = _isSelected

    suspend fun setIsSelected(selected: Boolean) {
        _isSelected.emit(selected)
    }

    suspend fun setFlash(isFlash: Boolean) {
        _flash.emit(isFlash)
    }

    fun updatePlacement(boardProperty: BoardProperty) {
        this.placement = if (x > boardProperty.center.x) {
            Placement.Right
        } else if ((x + size.width) < boardProperty.center.x) {
            Placement.Left
        } else {
            Placement.Mid
        }
    }
}
