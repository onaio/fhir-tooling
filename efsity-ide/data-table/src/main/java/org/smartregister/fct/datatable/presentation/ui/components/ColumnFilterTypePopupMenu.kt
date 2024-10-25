package org.smartregister.fct.datatable.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.datatable.data.controller.DataTableController
import org.smartregister.fct.datatable.domain.feature.DTFilterableType
import org.smartregister.fct.datatable.domain.model.DataFilterTypeColumn

context (ConstraintLayoutScope)
@Composable
internal fun ColumnFilterTypePopupMenu(
    modifier: Modifier = Modifier,
    controller: DataTableController,
    typeRef: ConstrainedLayoutReference,
    dataFilterTypeColumn: DataFilterTypeColumn,
    onSelected: (DataFilterTypeColumn) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.constrainAs(typeRef) {
            top.linkTo(parent.top)
            end.linkTo(parent.end, 10.dp)
            bottom.linkTo(parent.bottom)
        }
    ) {
        Tooltip(
            tooltip = dataFilterTypeColumn.filterType.label,
            tooltipPosition = TooltipPosition.Bottom(),
        ) {
            SmallIconButton(
                mainModifier = Modifier.size(22.dp),
                iconModifier = Modifier.size(16.dp),
                icon = dataFilterTypeColumn.filterType.icon,
                onClick = {
                    expanded = !expanded
                }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (controller is DTFilterableType) {
                controller.getFilterTypes(dataFilterTypeColumn).forEach {
                    DropdownMenuItem(
                        text = {
                            Row {
                                Icon(
                                    icon = it.icon
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(it.label)
                            }
                        },
                        onClick = {
                            expanded = false
                            onSelected(
                                dataFilterTypeColumn.copy(
                                    filterType = it
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}