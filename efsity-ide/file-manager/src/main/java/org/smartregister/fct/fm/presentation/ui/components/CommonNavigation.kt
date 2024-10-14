package org.smartregister.fct.fm.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import okio.Path
import org.smartregister.fct.fm.domain.model.Directory

@Composable
internal fun CommonNavigation(
    activePath: Path,
    commonDirs: List<Directory>? = null,
    rootDirs: List<Directory>? = null,
    onDirectoryClick: ((Path) -> Unit)? = null
) {

    Box(
        Modifier.width(150.dp).fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainer).alpha(0.8f)
    ) {
        Column(Modifier.padding(top = 4.dp)) {

            commonDirs?.forEach {
                NavigationItem(
                    directory = it,
                    selected = it.path.toString() == activePath.toString(),
                    onClick = { selectedDir ->
                        onDirectoryClick?.invoke(selectedDir.path)
                    }
                )
            }

            rootDirs?.takeIf { it.isNotEmpty() }?.let { dirs ->
                Spacer(Modifier.height(4.dp))
                HorizontalDivider(Modifier)
                Spacer(Modifier.height(4.dp))
                dirs.forEach {
                    NavigationItem(
                        directory = it,
                        selected = it.path.toString() == activePath.toString(),
                        onClick = {  selectedDir ->
                            onDirectoryClick?.invoke(selectedDir.path)
                        }
                    )
                }
            }
        }

        VerticalDivider(Modifier.align(Alignment.CenterEnd))
    }
}