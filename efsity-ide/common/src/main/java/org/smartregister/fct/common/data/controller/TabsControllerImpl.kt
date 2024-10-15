package org.smartregister.fct.common.data.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.smartregister.fct.common.domain.controller.TabsController
import org.smartregister.fct.common.domain.model.TabData
import org.smartregister.fct.common.domain.model.TabType

class TabsControllerImpl<T>(
    items: List<T>,
    override val title: (Int, T) -> String,
    defaultTabIndex: Int = 0,
    override val tabType: TabType = TabType.Filled,
    override val showCloseIcon: Boolean = true,
) : TabsController<T> {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _tabData: MutableStateFlow<TabData<T>>
    internal val tabData: StateFlow<TabData<T>>

    init {
        checkIndex(defaultTabIndex)

        _tabData = MutableStateFlow(
            TabData(
                activeIndex = defaultTabIndex,
                items = items
            )
        )
        tabData = _tabData

    }

    override fun add(data: T) = add(data, _tabData.value.activeIndex + 1)

    override fun addFirst(data: T) = add(data, 0)

    override fun addLast(data: T) = add(data, _tabData.value.items.size)

    override fun add(data: T, index: Int) {
        checkIndex(index)

        scope.launch {
            if (_tabData.value.items.isNotEmpty()) {
                val newItems = _tabData.value.items.toMutableList()
                newItems.add(index, data)
                _tabData.emit(
                    TabData(
                        activeIndex = index,
                        items = newItems
                    )
                )
            } else {
                _tabData.emit(
                    TabData(
                        activeIndex = 0,
                        items = listOf(data)
                    )
                )
            }
        }
    }

    override fun close(index: Int) {
        checkIndex(index)

        scope.launch {
            val newItems = _tabData.value.items.toMutableList()
            newItems.removeAt(index)

            val updatedTabData = if (newItems.isEmpty()) {
                TabData(
                    activeIndex = 0,
                    items = newItems,
                )
            } else if (_tabData.value.activeIndex >= newItems.size) {
                TabData(
                    activeIndex = _tabData.value.activeIndex - 1,
                    items = newItems
                )
            } else {
                _tabData.value.copy(
                    items = newItems
                )
            }

            _tabData.emit(updatedTabData)
        }

    }

    override fun select(index: Int) {
        checkIndex(index)
        scope.launch {
            _tabData.emit(
                _tabData.value.copy(
                    activeIndex = index
                )
            )
        }
    }

    override fun moveNext() {
        scope.launch {
            if (_tabData.value.activeIndex < _tabData.value.items.size.minus(1)) {
                _tabData.emit(
                    _tabData.value.copy(
                        activeIndex = _tabData.value.activeIndex + 1
                    )
                )
            }
        }
    }

    override fun movePrevious() {
        scope.launch {
            if (_tabData.value.activeIndex > 0) {
                _tabData.emit(
                    _tabData.value.copy(
                        activeIndex = _tabData.value.activeIndex - 1
                    )
                )
            }
        }
    }

    override fun getItems(): List<T> = _tabData.value.items

    private fun checkIndex(index: Int) {
        if (index != 0) {
            require((index - 1) in 0.._tabData.value.items.size) {
                throw IndexOutOfBoundsException("{index=$index} should in tabs size range")
            }
        }
    }
}