package org.smartregister.fct.settings.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.serverconfig.presentation.ui.panel.ServerConfigPanel
import org.smartregister.fct.settings.domain.model.Setting
import org.smartregister.fct.settings.presentation.components.SettingsComponent

@Composable
internal fun SettingsContainer(
    component: SettingsComponent
) {

    val activeSetting by component.activeSetting.subscribeAsState()

    Row {
        SidePanel(
            activeSetting = activeSetting,
            onSettingClick = {
                component.changeSetting(it)
            }
        )

        Aurora(
            componentContext = component
        ) {
            when (activeSetting) {
                is Setting.ServerConfigs -> {
                    ServerConfigPanel(component)
                }

                is Setting.CodeEditor -> {
                    CodeEditorSettingPanel()
                }
            }
        }
    }

}