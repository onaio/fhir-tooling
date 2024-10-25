package org.smartregister.fct.common.domain.controller

import org.smartregister.fct.common.domain.model.TabType

interface TabsController<T> {

    val title: (Int, T) -> String
    val tabType: TabType
    val showCloseIcon: Boolean

    fun add(data: T)
    fun addFirst(data: T)
    fun addLast(data: T)
    fun add(data: T, index: Int)
    fun close(index: Int)
    fun select(index: Int)
    fun moveNext()
    fun movePrevious()
    fun getItems(): List<T>
}