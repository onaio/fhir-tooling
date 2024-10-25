package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Schema
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent
import org.smartregister.fct.rules.presentation.ui.dialog.rememberNewRuleDialog

@Composable
internal fun CreateRuleButton(
    component: RulesScreenComponent
) {

    val newRuleDialog = rememberNewRuleDialog { ruleWidget, isEdit ->
        if (!isEdit) {
            component.addRule(ruleWidget.body)
        }
    }

    CircleButton(
        icon = AuroraIconPack.Schema,
        tooltip = "Create rule",
        tooltipPosition = TooltipPosition.Bottom(),
        enable = component.workspace.collectAsState().value != null,
        onClick = {
            newRuleDialog.show()
        }
    )
}