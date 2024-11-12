package org.smartregister.fct.sm.presentation.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.MoveDown
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.PanelHeading
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.common.presentation.ui.dialog.rememberConfirmationDialog
import org.smartregister.fct.sm.domain.model.SMModel
import org.smartregister.fct.sm.presentation.component.StructureMapScreenComponent
import org.smartregister.fct.sm.util.SMConfig

@Composable
internal fun ShowAllStructureMaps(component: StructureMapScreenComponent) {

    var alpha by remember { mutableStateOf(0f) }
    var offsetX by remember { mutableStateOf((-400).dp) }
    var isShow by remember { mutableStateOf(false) }

    val animatedAlpha by animateFloatAsState(alpha, tween(500))
    val animatedOffsetX by animateDpAsState(
        targetValue = offsetX,
        finishedListener = {
            isShow = component.showAllStructureMapPanel.value
        }
    )


    if (component.showAllStructureMapPanel.collectAsState().value) {
        isShow = true
        alpha = 0.3f
        offsetX = 0.dp
    } else {
        alpha = 0.0f
        offsetX = (-400).dp
    }

    if (isShow) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(animatedAlpha))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        alpha = 0f
                        component.toggleAllStructureMapPanel()
                    }
            )

            Box(
                modifier = Modifier
                    .width(400.dp)
                    .fillMaxHeight()
                    .offset(x = animatedOffsetX)
                    .background(colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                ) {
                    PanelHeading("All StructureMaps")
                    StructureMapList(component)
                }

                VerticalDivider(
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
private fun StructureMapList(component: StructureMapScreenComponent) {

    val deleteWorkflowDialog = rememberConfirmationDialog<SMModel> { _, workflow ->
        component.toggleAllStructureMapPanel()
        component.deleteStructureMap(workflow!!)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        itemsIndexed(component.allStructureMaps) { index, smModel ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        component.toggleAllStructureMapPanel()
                        component.openStructureMap(smModel, smModel.mapPath)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(30.dp).padding(start = 12.dp),
                    icon = AuroraIconPack.MoveDown
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Tooltip(
                        tooltip = smModel.name,
                        tooltipPosition = TooltipPosition.Top(),
                    ) {
                        Text(
                            text = smModel.name
                        )
                    }
                    if (smModel.id != SMConfig.activeStructureMap?.id) {
                        SmallIconButton(
                            icon = Icons.Outlined.Delete,
                            onClick = {
                                deleteWorkflowDialog.show(
                                    title = "Delete StructureMap",
                                    message = "Are you sure you want to delete this structure-map?",
                                    data = smModel
                                )
                            },
                            tooltip = "Delete",
                            tooltipPosition = TooltipPosition.Bottom()
                        )
                    }
                }
            }
        }

        if (component.allStructureMaps.isEmpty()) {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    text = "No structure-map available",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}