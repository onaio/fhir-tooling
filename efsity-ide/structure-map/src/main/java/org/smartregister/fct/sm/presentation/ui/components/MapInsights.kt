package org.smartregister.fct.sm.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.PanelHeading
import org.smartregister.fct.sm.presentation.component.StructureMapScreenComponent

@Composable
internal fun MapInsights(component: StructureMapScreenComponent) {
    Box(
        modifier = Modifier
            .width(250.dp)
            .fillMaxHeight()
            .background(colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val totalGroups by component.groups.collectAsState()
            val outputResources by component.outputResources.collectAsState()

            PanelHeading("Groups")
            GroupListAndOutResources(false, totalGroups)

            OutputResourcesPanelHeading()
            GroupListAndOutResources( true, outputResources)

        }

        VerticalDivider()
    }

}

@Composable
private fun OutputResourcesPanelHeading() {
    Box(
        Modifier.fillMaxWidth().height(40.dp)
            .background(colorScheme.surface)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Output Resources",
            style = MaterialTheme.typography.titleSmall
        )
        HorizontalDivider(modifier = Modifier.align(Alignment.TopCenter))
        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}