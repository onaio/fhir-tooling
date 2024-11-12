package org.smartregister.fct.presentation.ui.components

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Settings
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.settings.presentation.ui.dialogs.rememberSettingsDialog

@Composable
internal fun SettingButton(
    componentContext: ComponentContext
) {

    val settingsDialog = rememberSettingsDialog(
        componentContext = componentContext
    )

    SmallIconButton(
        tooltip = "Settings",
        tooltipPosition = TooltipPosition.Bottom(),
        onClick = {
            settingsDialog.show()
        },
        tint = colorScheme.onSurface,
        icon = AuroraIconPack.Settings,
    )
}