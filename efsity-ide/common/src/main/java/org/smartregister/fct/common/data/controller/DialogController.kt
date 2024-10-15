package org.smartregister.fct.common.data.controller

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow

class DialogController<T>(
    private val onShow: DialogController<T>.(
        data: T?
    ) -> Unit,
    private val onHide: DialogController<T>.() -> Unit)
{
    internal val isShowDialog = MutableStateFlow(false)
    internal val data = MutableStateFlow<T?>(null)

    fun show(data: T? = null) {
        onShow(data)
    }

    fun hide() {
        onHide()
    }

    @Composable
    internal fun build() {

    }
}