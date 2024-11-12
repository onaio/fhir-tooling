package org.smartregister.fct.common.presentation.component

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value

interface SlotComponent<T: Any, C> {

    val slot: Value<ChildSlot<*, T>>
    fun changeSlot(item: C)
}