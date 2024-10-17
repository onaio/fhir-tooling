package org.smartregister.fct.rules.presentation.ui.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import org.koin.compose.koinInject
import org.smartregister.fct.aurora.presentation.ui.components.Button
import org.smartregister.fct.aurora.presentation.ui.components.NumberDropDown
import org.smartregister.fct.aurora.presentation.ui.components.OutlinedTextField
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.common.data.controller.DialogController
import org.smartregister.fct.common.presentation.ui.dialog.rememberConfirmationDialog
import org.smartregister.fct.common.presentation.ui.dialog.rememberDialog
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.domain.model.AppSetting
import org.smartregister.fct.engine.util.uuid
import org.smartregister.fct.rules.data.transformation.RuleActionTransformation
import org.smartregister.fct.rules.domain.model.Rule
import org.smartregister.fct.rules.domain.model.Widget

@Composable
internal fun rememberNewRuleDialog(
    title: String = "New Rule",
    onDismiss: ((DialogController<Widget<Rule>>) -> Unit)? = null,
    onDeleteRule: ((Widget<Rule>) -> Unit)? = null,
    onDone: (Widget<Rule>, Boolean) -> Unit,
): DialogController<Widget<Rule>> {

    val dialogController = rememberDialog(
        width = 800.dp,
        height = 500.dp,
        title = title,
        onDismiss = onDismiss,
        cancelOnTouchOutside = false
    ) { controller, existingRuleWidget ->

        NewRuleDialog(
            controller = controller,
            existingRuleWidget = existingRuleWidget,
            onDeleteRule = onDeleteRule,
            onDone = onDone,
        )
    }

    return dialogController
}

@Composable
private fun NewRuleDialog(
    controller: DialogController<Widget<Rule>>,
    existingRuleWidget: Widget<Rule>?,
    onDeleteRule: ((Widget<Rule>) -> Unit)?,
    onDone: (Widget<Rule>, Boolean) -> Unit,
) {

    val appSetting: AppSetting = koinInject<AppSettingManager>().appSetting
    val isDarkTheme = appSetting.isDarkTheme
    val focusRequester = remember { FocusRequester() }
    val existingRule = existingRuleWidget?.body
    var name by remember { mutableStateOf(existingRule?.name ?: "") }
    var priority by remember { mutableStateOf(existingRule?.priority ?: 1) }
    var condition by remember { mutableStateOf(existingRule?.condition ?: "true") }
    var description by remember { mutableStateOf(existingRule?.description ?: "") }
    val actions = remember {
        mutableStateListOf<String>().apply {
            existingRule?.actions?.let {
                addAll(it)
            } ?: add("")
        }
    }

    var nameError by remember { mutableStateOf(false) }

    val deleteRuleDialog = rememberConfirmationDialog<Widget<Rule>> { _, widget ->
        controller.hide()
        onDeleteRule?.invoke(widget!!)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.padding(12.dp).verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            value = name,
            onValueChange = {
                val input = it.trim()
                nameError = input.isEmpty()
                name = input
            },
            placeholder = "Name",
            isError = nameError
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberDropDown(
                    modifier = Modifier.fillMaxWidth(),
                    segments = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                    defaultValue = priority,
                    errorHighlight = false,
                    placeholder = "Priority",
                    min = 1,
                    max = 10,
                    onValueChanged = {
                        priority = it
                    },
                    onSelected = {
                        priority = it
                    }
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = colorScheme.primary,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = condition == "true",
                        onCheckedChange = {
                            condition = "$it"
                        }
                    )
                    Text(
                        text = "Condition "
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = {
                description = it
            },
            placeholder = "Description",
        )

        actions.forEachIndexed { index, action ->
            Spacer(Modifier.height(12.dp))
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth().height(80.dp)
            ) {
                val (actionFieldRef, deleteActionRef) = createRefs()

                OutlinedTextField(
                    modifier = Modifier.constrainAs(actionFieldRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(deleteActionRef.start, 12.dp)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    },
                    value = action,
                    singleLine = false,
                    onValueChange = {
                        actions[index] = it
                    },
                    visualTransformation = RuleActionTransformation(
                        isDarkTheme = isDarkTheme,
                        colorScheme = colorScheme,
                    ),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace
                    ),
                    placeholder = "Action ${index + 1}",
                )

                SmallIconButton(
                    mainModifier = Modifier.constrainAs(deleteActionRef) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                    icon = Icons.Outlined.Delete,
                    enable = actions.size > 1,
                    onClick = {
                        actions.removeAt(index)
                    }
                )

            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SmallIconButton(
                icon = Icons.Outlined.Add,
                onClick = {
                    actions.add("")
                }
            )
            Row {

                if (existingRuleWidget != null) {
                    Button(
                        modifier = Modifier,
                        label = "Delete",
                        onClick = {
                            deleteRuleDialog.show(
                                title = "Delete Rule",
                                message = "Are you sure you want to delete this rule?",
                                data = existingRuleWidget
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.error,
                            contentColor = colorScheme.onError
                        )
                    )
                    Spacer(Modifier.width(12.dp))
                }

                Button(
                    modifier = Modifier,
                    enable = !nameError && name.trim().isNotEmpty(),
                    label = if (existingRuleWidget != null) "Update" else "Create",
                    onClick = {
                        controller.hide()
                        val widget = existingRuleWidget?.let {
                            it.body = Rule(
                                id = it.body.id,
                                name = name,
                                priority = priority,
                                condition = condition,
                                description = description,
                                actions = actions,
                                result = it.body.result
                            )
                            it
                        } ?: Widget(
                            Rule(
                                id = uuid(),
                                name = name,
                                priority = priority,
                                condition = condition,
                                description = description,
                                actions = actions
                            )
                        )
                        onDone(
                            widget,
                            existingRuleWidget != null
                        )
                    }
                )
            }
        }
    }
}