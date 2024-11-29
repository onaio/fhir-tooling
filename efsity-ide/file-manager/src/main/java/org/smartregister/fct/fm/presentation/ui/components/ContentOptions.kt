package org.smartregister.fct.fm.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.smartregister.fct.aurora.presentation.ui.components.LabelledCheckBox
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.fm.domain.model.FileManagerMode
import org.smartregister.fct.fm.presentation.components.FileManagerComponent

@Composable
internal fun ContentOptions(
    component: FileManagerComponent,
    content: @Composable (RowScope.() -> Unit)? = null
) {
    Box(
        Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
        ) {

            BackButton(
                component = component
            )

            if (component.mode == FileManagerMode.Edit()) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        content = content ?: {}
                    )

                    val showHiddenFile by component.getShowHiddenFile().collectAsState()

                    LabelledCheckBox(
                        checked = showHiddenFile,
                        label = "Hidden Files",
                        onCheckedChange = {
                            component.componentScope.launch {
                                component.setShowHiddenFile(it)
                            }
                        }
                    )
                }

            }
        }

        HorizontalDivider(Modifier.align(Alignment.BottomCenter))
    }
}