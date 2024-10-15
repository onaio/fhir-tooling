package org.smartregister.fct.logcat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.common.domain.model.ViewMode
import org.smartregister.fct.common.presentation.ui.components.ViewModePopupMenu
import org.smartregister.fct.logger.model.LogLevel

@Composable
internal fun TopBar(
    onViewModeSelected: (ViewMode) -> Unit,
    logLevelFilter: MutableState<LogLevel?>
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LogLevelFilterMenu(logLevelFilter)
        ViewModePopupMenu(
            onSelected = onViewModeSelected,
        )
    }
}

