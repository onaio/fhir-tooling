package org.smartregister.fct.base64.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.base64.ui.components.Editor
import org.smartregister.fct.base64.ui.components.SourceToolbar
import org.smartregister.fct.base64.ui.components.TargetToolbar
import org.smartregister.fct.common.data.controller.DialogController
import org.smartregister.fct.common.domain.model.ResizeOption
import org.smartregister.fct.common.presentation.ui.components.HorizontalSplitPane
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.common.presentation.ui.dialog.rememberDialog
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.util.getKoinInstance

@Composable
fun rememberBase64EncodeDecodeDialog(
    componentContext: ComponentContext,
    title: String = "Base64 Encode / Decode",
    formatOnStart: Boolean = false,
    cancelable: Boolean = true,
    onDismiss: (DialogController<String>.() -> Unit)? = null,
): DialogController<String> {

    val controller = rememberDialog(
        title = title,
        width = 1400.dp,
        height = 800.dp,
        cancelable = cancelable,
        cancelOnTouchOutside = true,
        onDismiss = onDismiss
    ) { c, text ->

        Base64EncodeDecode(
            componentContext = componentContext,
            formatOnStart = formatOnStart,
            text = text ?: ""
        )
    }

    return controller
}

@Composable
private fun Base64EncodeDecode(
    text: String,
    formatOnStart: Boolean = false,
    componentContext: ComponentContext
) {

    val appSetting = getKoinInstance<AppSettingManager>().appSetting
    val binaryChecked = remember { mutableStateOf(false) }
    val targetTextState = remember { mutableStateOf(text) }

    HorizontalSplitPane(
        resizeOption = ResizeOption.Flexible(),
        leftContent = {
            val tabIndentState = remember { mutableStateOf(appSetting.codeEditorConfig.indent) }
            val textState = remember { mutableStateOf(text) }

            Aurora(
                componentContext = componentContext,
            ) {
                with(it) {
                    Column {
                        SourceToolbar(textState, targetTextState, tabIndentState, binaryChecked)
                        HorizontalDivider()
                        Editor(textState, tabIndentState, formatOnStart, true)
                    }
                }
            }
        },
        rightContent = {
            val tabIndentState = remember { mutableStateOf(appSetting.codeEditorConfig.indent) }
            Aurora(
                componentContext = componentContext,
            ) {
                with(it) {
                    Column {
                        TargetToolbar(targetTextState, tabIndentState, binaryChecked)
                        HorizontalDivider()
                        Editor(targetTextState, tabIndentState, formatOnStart, false)
                    }
                }
            }
        }
    )


}