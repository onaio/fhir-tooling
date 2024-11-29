package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.smartregister.fct.engine.domain.model.IntSize
import org.smartregister.fct.rules.domain.model.BoardProperty
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent
import org.smartregister.fct.rules.presentation.ui.dialog.rememberNewDataSourceDialog

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun DataSourceWidget(
    component: RulesScreenComponent,
    boardProperty: BoardProperty
) {
    val colorScheme = MaterialTheme.colorScheme
    val chipHeight = remember { 40 }
    val chipSpace = remember { 12 }
    val dsSize = component.dataSourceWidgets.collectAsState().value.size

    val editDataSourceDialog = rememberNewDataSourceDialog(
        title = "Update Data Source",
        onDeleteDataSource = {
            component.removeDataSource(it)
        }
    ) { widget, isEdit ->
        if (isEdit) {
            component.focus(widget)
        }
    }

    component.dataSourceWidgets.collectAsState().value.forEachIndexed { index, widget ->

        var offset by remember { mutableStateOf(IntOffset.Zero) }

        Chip(
            modifier = Modifier
                .zIndex(1f)
                .offset(x = offset.x.dp, y = offset.y.dp)
                .height(chipHeight.dp)
                .onGloballyPositioned {

                    val x = (0 - it.size.width / 2) + boardProperty.center.x
                    val y =
                        ((chipHeight * index + chipSpace * index) - (chipHeight / 2) * dsSize + (chipSpace / 2 * dsSize.minus(
                            1
                        ))) + boardProperty.center.y

                    widget.x = x.toFloat()
                    widget.y = y.toFloat()
                    widget.size = IntSize(
                        width = it.size.width,
                        height = it.size.height
                    )

                    offset = IntOffset(
                        x = x,
                        y = y
                    )
                },
            border = BorderStroke(
                width = 0.5.dp,
                color = colorScheme.onSurface.copy(0.6f)
            ),
            colors = ChipDefaults.chipColors(
                backgroundColor = colorScheme.surface,
            ),
            onClick = {
                editDataSourceDialog.show(widget)
            }
        ) {
            Text(
                text = widget.body.id.ifEmpty { widget.body.resourceType },
                color = colorScheme.onSurface
            )
        }
    }
}