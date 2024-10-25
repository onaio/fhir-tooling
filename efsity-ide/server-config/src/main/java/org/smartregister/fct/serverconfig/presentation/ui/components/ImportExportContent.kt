package org.smartregister.fct.serverconfig.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import org.smartregister.fct.engine.domain.model.ServerConfig
import org.smartregister.fct.serverconfig.presentation.components.ConfigDialogComponent
import org.smartregister.fct.serverconfig.presentation.components.ServerConfigPanelComponent

context (ConfigDialogComponent, ServerConfigPanelComponent)
@Composable
internal fun ImportExportContent(
    configs: List<ServerConfig>,
    bottomContent: @Composable BoxScope.() -> Unit,
) {
    ConstraintLayout {
        val (list, button) = createRefs()

        Box(Modifier.padding(horizontal = 12.dp).constrainAs(list) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            bottom.linkTo(button.top)
            height = Dimension.preferredWrapContent
        }) {
            ImportExportConfigList(configs)
        }

        Box(
            Modifier.background(MaterialTheme.colorScheme.surfaceContainer).padding(12.dp)
                .constrainAs(button) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            content = bottomContent
        )
    }
}