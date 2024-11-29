package org.smartregister.fct.common.data.controller

class ConfirmationDialogController<T>(
    private val onShow: ConfirmationDialogController<T>.(
        title: String?,
        message: String,
        data: T?
    ) -> Unit,
    private val onHide: ConfirmationDialogController<T>.() -> Unit
) {

    fun show(title: String? = null, message: String, data: T? = null) {
        onShow(title, message, data)
    }

    fun hide() {
        onHide()
    }
}