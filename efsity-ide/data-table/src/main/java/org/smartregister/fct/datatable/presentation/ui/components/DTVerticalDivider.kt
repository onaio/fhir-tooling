package org.smartregister.fct.datatable.presentation.ui.components

import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.util.getKoinInstance

@Composable
internal fun DTVerticalDivider(
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    val theme = getKoinInstance<AppSettingManager>()
    VerticalDivider(
        modifier = modifier,
        thickness = if (theme.appSetting.isDarkTheme) 0.3.dp else 1.dp,
        color = if (theme.appSetting.isDarkTheme) MaterialTheme.colorScheme.outline else DividerDefaults.color.copy(
            alpha = alpha
        )
    )
}