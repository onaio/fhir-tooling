package org.smartregister.fct.fm.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.fm.presentation.components.FileManagerComponent

@Composable
internal fun BackButton(
    modifier: Modifier = Modifier,
    component: FileManagerComponent
) {
    val scope = rememberCoroutineScope()
    val activePath by component.getActivePath().collectAsState()

    SmallIconButton(
        iconModifier = modifier,
        icon = Icons.AutoMirrored.Outlined.ArrowBack,
        enable = activePath.toString() !in component.getCommonDirs().map { it.path.toString() },
        onClick = {
            scope.launch {
                activePath.parent?.let {
                    component.setActivePath(it)
                }
            }
        }
    )
}