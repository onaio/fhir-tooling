package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.OutlinedTextField
import org.smartregister.fct.rules.util.RulesEngineMethods

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun RulesEngineMethodList() {

    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    Box {
        Chip(
            modifier = Modifier.width(300.dp),
            onClick = {
                searchText = ""
                expanded = true
            },
            colors = ChipDefaults.chipColors(
                backgroundColor = colorScheme.surface,
            ),
            border = BorderStroke(
                width = 0.5.dp,
                color = colorScheme.onSurface.copy(0.6f)
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rules Engine Methods",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    icon = Icons.Outlined.ArrowDropDown
                )
            }
        }

        DropdownMenu(
            modifier = Modifier
                .width(900.dp)
                .requiredHeightIn(max = 700.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {

            DropdownMenuItem(
                text = {
                    Column {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = searchText,
                            onValueChange = {
                                searchText = it
                            },
                            placeholder = "Search Method"
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                },
                onClick = {}
            )

            RulesEngineMethods.methodList.filter {
                it.name.contains(searchText, ignoreCase = true)
            }.forEachIndexed { index, methodInfo ->

                var visible by remember { mutableStateOf(false) }
                DropdownMenuItem(
                    modifier = Modifier.background(
                        colorScheme.surfaceContainer
                    ),
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onPointerEvent(PointerEventType.Enter) {
                                    visible = true
                                }
                                .onPointerEvent(PointerEventType.Exit) {
                                    visible = false
                                }
                        ) {

                            if (index > 0) HorizontalDivider()

                            Spacer(Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    modifier = Modifier.size(20.dp).padding(top = 2.dp),
                                    icon = Icons.Outlined.Functions
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(methodInfo.name)
                            }

                            AnimatedVisibility(visible) {
                                Row {
                                    Spacer(Modifier.width(26.dp))
                                    Text(
                                        modifier = Modifier.padding(top = 8.dp),
                                        text = methodInfo.description,
                                        style = typography.bodySmall
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))

                        }
                    },
                    onClick = {
                        expanded = false
                    }
                )
            }
        }
    }
}