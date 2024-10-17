package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import org.smartregister.fct.aurora.presentation.ui.components.MiddleEllipsisText
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.engine.domain.model.IntSize
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.rules.domain.model.BoardProperty
import org.smartregister.fct.rules.domain.model.Rule
import org.smartregister.fct.rules.domain.model.Widget
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent
import org.smartregister.fct.rules.presentation.ui.dialog.rememberNewRuleDialog
import org.smartregister.fct.rules.util.WorkspaceConfig
import org.smartregister.fct.rules.util.appendBold

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun RuleWidget(
    component: RulesScreenComponent,
    boardProperty: BoardProperty
) {

    val editRuleDialog = rememberNewRuleDialog(
        title = "Update Rule",
        onDeleteRule = {
            component.removeRule(it)
        }
    ) { ruleWidget, isEdit ->
        if (isEdit) {
            component.focus(ruleWidget)
        }
    }

    var topMostZIndex by remember { mutableStateOf(1f) }
    component.ruleWidgets.collectAsState().value.forEach { widget ->

        var offset by remember(widget.body.id) { mutableStateOf(Offset(widget.x, widget.y)) }
        var zIndex by remember(widget.body.id) { mutableStateOf(topMostZIndex) }
        var highlightConnections by remember(widget.body.id) { mutableStateOf(false) }
        val isSelected by widget.isSelected.collectAsState()
        val flash by widget.flash.collectAsState()
        val theme = getTheme(widget)

        val titleBackgroundColor by animateColorAsState(
            targetValue = if (flash) colorScheme.tertiary else theme.titleBackground,
            animationSpec = tween(300),
            finishedListener = {
                component.componentScope.launch {
                    widget.setFlash(false)
                }
            }
        )

        DrawConnections(
            component = component,
            widget = widget,
            highlight = highlightConnections,
            isSelected = isSelected
        )

        Card(
            modifier = Modifier
                .width(component.widgetWidth.dp)
                .zIndex(zIndex)
                .offset(x = offset.x.dp, y = offset.y.dp)
                .onPointerEvent(PointerEventType.Press) {
                    topMostZIndex += 1f
                    zIndex = topMostZIndex
                    component.selectRuleWidget(widget)
                }
                .onPointerEvent(PointerEventType.Enter) {
                    highlightConnections = true
                }
                .onPointerEvent(PointerEventType.Exit) {
                    highlightConnections = false
                }
                .pointerInput(widget.body.id) {

                    detectDragGestures { _, dragAmount ->
                        widget.x += dragAmount.x
                        widget.y += dragAmount.y
                        offset = Offset(
                            x = widget.x,
                            y = widget.y
                        )
                        widget.updatePlacement(boardProperty)
                    }
                }
                .onGloballyPositioned {
                    widget.size = IntSize(
                        width = it.size.width,
                        height = it.size.height
                    )
                    widget.updatePlacement(boardProperty)
                }
                .graphicsLayer {
                    shadowElevation = if (isSelected || highlightConnections) 10f else 0f
                },
            colors = CardDefaults.cardColors(
                containerColor = theme.background
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (isSelected || highlightConnections) colorScheme.onSurface else theme.border
            )
        ) {
            Column(Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(titleBackgroundColor)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Tooltip(
                        modifier = Modifier.requiredWidthIn(max = 240.dp).align(Alignment.Center),
                        tooltip = widget.body.name,
                        tooltipPosition = TooltipPosition.Top(),
                    ) {
                        MiddleEllipsisText(
                            modifier = Modifier.align(Alignment.Center),
                            text = widget.body.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = theme.titleColor,
                            textAlign = TextAlign.Center
                        )
                    }

                    SmallIconButton(
                        mainModifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(22.dp),
                        iconModifier = Modifier
                            .size(14.dp),
                        icon = Icons.Outlined.Edit,
                        tint = theme.titleColor,
                        onClick = {
                            editRuleDialog.show(widget)
                        }
                    )
                }
                HorizontalDivider()
                Column(Modifier.padding(8.dp)) {
                    Label(
                        prefix = "Priority",
                        text = widget.body.priority.toString(),
                        theme = theme,
                    )
                    Spacer(Modifier.height(8.dp))
                    Label(
                        prefix = "Condition",
                        text = widget.body.condition,
                        theme = theme,
                    )
                    if (widget.body.result.trim().isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        val result = if (theme.themeType == ThemeType.Error) {
                            buildAnnotatedString {
                                appendBold("Result: ")
                                append(widget.body.result.text.replace(WorkspaceConfig.jexlError, ""))
                            }
                        } else widget.body.result

                        Text(
                            text = result,
                            style = MaterialTheme.typography.bodySmall,
                            color = theme.contentColor,
                        )
                    }
                    widget.warnings.forEach { warning ->
                        Spacer(Modifier.height(8.dp))
                        Label(
                            prefix = "Warning",
                            text = warning,
                            theme = theme
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Label(prefix: String, text: String, theme: RuleWidgetTheme) {
    Row {
        Text(
            text = "$prefix: ",
            style = MaterialTheme.typography.bodySmall,
            color = theme.contentColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = theme.contentColor,
        )
    }

}

@Composable
private fun getTheme(widget: Widget<Rule>): RuleWidgetTheme {
    val result = widget.body.result
    return when {
        result.contains(WorkspaceConfig.jexlError) || result.contains(WorkspaceConfig.ambiguousError) -> RuleWidgetTheme(
            themeType = ThemeType.Error,
            background = Color(0xFFffdad6),
            titleBackground = Color(0xFFba1a1a),
            titleColor = Color.White,
            border = Color(0xFFba1a1a),
            contentColor = Color.Black
        )

        widget.warnings.isNotEmpty() -> RuleWidgetTheme(
            themeType = ThemeType.Warn,
            background = Color(0xfffff5cf),
            titleBackground = Color(0xFFFFCC00),
            titleColor = Color.Black,
            border = Color(0xffd2a701),
            contentColor = Color.Black
        )

        else -> RuleWidgetTheme(
            themeType = ThemeType.Default,
            background = colorScheme.background,
            titleBackground = colorScheme.surface,
            titleColor = colorScheme.onSurface,
            border = colorScheme.onSurface.copy(0.4f),
            contentColor = colorScheme.onBackground
        )
    }
}

private data class RuleWidgetTheme(
    val themeType: ThemeType,
    val background: Color,
    val titleBackground: Color,
    val titleColor: Color,
    val border: Color,
    val contentColor: Color
)

private enum class ThemeType {
    Default, Warn, Error
}