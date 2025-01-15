package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.VerticalSlider
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent

@Composable
internal fun BoxScope.BoardScaleSlider(component: RulesScreenComponent) {

    Column(
        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon = Icons.Outlined.Add
        )
        Spacer(Modifier.height(8.dp))
        VerticalSlider(
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            value = component.boardScaling.collectAsState().value * 100,
            onValueChange = {
                component.changeBoardScale(it)
            },
            valueRange = 50f..150f,
            colors = SliderDefaults.colors(
                activeTrackColor = Color.Transparent
            ),
            steps = 11
        )
        Spacer(Modifier.height(8.dp))
        Icon(
            icon = Icons.Outlined.Remove
        )
    }

}