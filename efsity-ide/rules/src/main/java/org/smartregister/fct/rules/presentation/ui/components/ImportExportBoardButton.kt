package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Download
import org.smartregister.fct.aurora.auroraiconpack.Publish
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent
import org.smartregister.fct.text_viewer.ui.dialog.rememberTextViewerDialog

@Composable
internal fun ImportExportButton(
    component: RulesScreenComponent
) {

    val importRuleJsonDialog = rememberTextViewerDialog(
        componentContext = component,
        callback = component::importRules
    )

    val exportRuleDialog = rememberTextViewerDialog(
        componentContext = component,
        formatOnStart = true,
    )

    CircleButton(icon = AuroraIconPack.Publish,
        tooltip = "Import Rules",
        tooltipPosition = TooltipPosition.Top(),
        enable = component.workspace.collectAsState().value != null,
        onClick = {
            importRuleJsonDialog.show()
        }
    )
    Spacer(Modifier.width(12.dp))
    CircleButton(icon = AuroraIconPack.Download,
        tooltip = "Export Rules",
        tooltipPosition = TooltipPosition.Top(),
        enable = component.workspace.collectAsState().value != null,
        onClick = {
            exportRuleDialog.show(component.getRulesJsonString())
        }
    )
}