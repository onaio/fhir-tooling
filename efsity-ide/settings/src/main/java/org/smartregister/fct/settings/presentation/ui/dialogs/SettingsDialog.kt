package org.smartregister.fct.settings.presentation.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.common.data.controller.DialogController
import org.smartregister.fct.common.presentation.ui.dialog.rememberDialog
import org.smartregister.fct.settings.domain.model.Setting
import org.smartregister.fct.settings.presentation.components.SettingsComponent
import org.smartregister.fct.settings.presentation.ui.components.SettingsContainer

@Composable
fun rememberSettingsDialog(
    componentContext: ComponentContext,
    setting: Setting = Setting.ServerConfigs,
    title: String = "Settings",
    cancelable: Boolean = true,
    onDismiss: (DialogController<Unit>.() -> Unit)? = null,
): DialogController<Unit> {

    val controller = rememberDialog(
        title = title,
        width = 1000.dp,
        height = 650.dp,
        cancelable = cancelable,
        cancelOnTouchOutside = false,
        onDismiss = onDismiss
    ) { _, _ ->

        val component = remember {
            SettingsComponent(
                componentContext = componentContext,
                activeSetting = setting
            )
        }

        SettingsContainer(
            component = component
        )
    }

    return controller
}