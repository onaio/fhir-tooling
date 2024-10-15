package org.smartregister.fct.common.domain.model

internal data class TabData<T>(
    val activeIndex: Int,
    val items: List<T>
)