package org.smartregister.fct.settings.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import org.smartregister.fct.settings.domain.model.Setting

@Composable
internal fun SidePanel(
    activeSetting: Setting,
    onSettingClick: (Setting) -> Unit
) {

    Box(
        Modifier.width(200.dp).fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainer).alpha(0.8f)
    ) {
        Column(Modifier.padding(top = 4.dp)) {

            listOf(
                Setting.ServerConfigs,
                Setting.CodeEditor
            ).forEach {
                PanelItem(
                    setting = it,
                    selected = it == activeSetting,
                    onClick = { setting ->
                        onSettingClick.invoke(setting)
                    }
                )
            }
        }

        VerticalDivider(Modifier.align(Alignment.CenterEnd))
    }
}