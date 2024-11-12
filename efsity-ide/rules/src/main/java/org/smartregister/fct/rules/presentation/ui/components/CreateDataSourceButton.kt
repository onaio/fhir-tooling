package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Dataset
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent
import org.smartregister.fct.rules.presentation.ui.dialog.rememberNewDataSourceDialog

@Composable
internal fun CreateDataSourceButton(
    component: RulesScreenComponent
) {
    val newSourceDialog = rememberNewDataSourceDialog { widget, isEdit ->
        if (!isEdit) {
            component.addDataSource(widget.body)
        }
    }

    CircleButton(
        icon = AuroraIconPack.Dataset,
        tooltip = "Create source",
        tooltipPosition = TooltipPosition.Bottom(),
        enable = component.workspace.collectAsState().value != null,
        onClick = {
            newSourceDialog.show()
        }
    )
}