package org.smartregister.fct.datatable.presentation.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.util.getKoinInstance

@Composable
internal fun DTHorizontalDivider(
    dtWidth: Dp,
    alpha: Float = 1f
) {
    val theme = getKoinInstance<AppSettingManager>()

    HorizontalDivider(
        modifier = Modifier.width(dtWidth),
        color = if (theme.appSetting.isDarkTheme) DividerDefaults.color else DividerDefaults.color.copy(
            alpha = alpha
        )
    )
}