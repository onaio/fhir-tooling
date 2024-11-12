package org.smartregister.fct.settings.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.engine.data.manager.AppSettingManager

@Composable
internal fun CodeEditorSettingPanel() {
    val appSettingManager = koinInject<AppSettingManager>()
    val tabIndent = appSettingManager.appSetting.codeEditorConfig.indent

    var selectedTabIndent2 by remember { mutableStateOf(tabIndent == 2) }
    var selectedTabIndent4 by remember { mutableStateOf(tabIndent == 4) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            FilterChip(
                onClick = {
                    selectedTabIndent2 = true
                    selectedTabIndent4 = false
                    updateTabIndentSetting(2, appSettingManager)
                },
                label = {
                    Text("2 Tab Indent")
                },
                selected = selectedTabIndent2,
                leadingIcon = if (selectedTabIndent2) {
                    {
                        Icon(
                            icon = Icons.Filled.Done,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
            )
            Spacer(Modifier.width(12.dp))
            FilterChip(
                onClick = {
                    selectedTabIndent4 = true
                    selectedTabIndent2 = false
                    updateTabIndentSetting(4, appSettingManager)
                },
                label = {
                    Text("4 Tab Indent")
                },
                selected = selectedTabIndent4,
                leadingIcon = if (selectedTabIndent4) {
                    {
                        Icon(
                            icon = Icons.Filled.Done,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
            )
        }
        HorizontalDivider()
    }

}

private fun updateTabIndentSetting(tabIndent: Int, appSettingManager: AppSettingManager) {
    val appSetting = appSettingManager.appSetting
    appSetting.codeEditorConfig.indent = tabIndent
    appSettingManager.update()
}