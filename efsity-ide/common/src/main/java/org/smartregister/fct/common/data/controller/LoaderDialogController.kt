package org.smartregister.fct.common.data.controller

class LoaderDialogController(private val onShow: () -> Unit, private val onHide: () -> Unit) {

    fun show() {
        onShow()
    }

    fun hide() {
        onHide()
    }
}