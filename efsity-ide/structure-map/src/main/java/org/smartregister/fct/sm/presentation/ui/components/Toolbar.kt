package org.smartregister.fct.sm.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.FolderOpen
import org.smartregister.fct.aurora.auroraiconpack.NoteAdd
import org.smartregister.fct.aurora.auroraiconpack.Send
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.common.data.controller.SingleFieldDialogController
import org.smartregister.fct.sm.presentation.component.StructureMapScreenComponent

@Composable
internal fun Toolbar(
    component: StructureMapScreenComponent,
    newStructureMapDialog: SingleFieldDialogController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(39.dp)
            .background(colorScheme.surface.copy(0.8f))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        CreateNewStructureMapButton(newStructureMapDialog)
        Spacer(Modifier.width(8.dp))
        OpenStructureMapButton(component)
        Spacer(Modifier.width(8.dp))
        ExecuteStructureMapButton(component)
    }
    HorizontalDivider()
}

@Composable
private fun CreateNewStructureMapButton(newStructureMapDialog: SingleFieldDialogController) {
    Tooltip(
        tooltip = "New StructureMap",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            icon = AuroraIconPack.NoteAdd,
            onClick = {
                newStructureMapDialog.show()
            }
        )
    }
}

@Composable
private fun OpenStructureMapButton(component: StructureMapScreenComponent) {
    Tooltip(
        tooltip = "Open StructureMap",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            icon = AuroraIconPack.FolderOpen,
            onClick = {
                component.toggleAllStructureMapPanel()
            }
        )
    }
}

@Composable
private fun ExecuteStructureMapButton(component: StructureMapScreenComponent) {
    Tooltip(
        tooltip = "Run",
        tooltipPosition = TooltipPosition.Bottom(),
    ) {
        SmallIconButton(
            icon = AuroraIconPack.Send,
            onClick = {
                component.execute()
            },
        )
    }
}