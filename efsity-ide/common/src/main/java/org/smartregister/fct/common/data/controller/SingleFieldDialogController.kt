package org.smartregister.fct.common.data.controller

class SingleFieldDialogController(private val onShow: () -> Unit) {
    private var extra: Any? = null

    fun show(extra: Any? = null) {
        this.extra = extra
        onShow()
    }

    fun<T> getExtra() : T? = extra as T?
}