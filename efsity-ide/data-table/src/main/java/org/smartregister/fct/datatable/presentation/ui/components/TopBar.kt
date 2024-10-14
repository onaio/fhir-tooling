package org.smartregister.fct.datatable.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LastPage
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FirstPage
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Sync
import org.smartregister.fct.aurora.presentation.ui.components.LinearIndicator
import org.smartregister.fct.aurora.presentation.ui.components.NumberDropDown
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.datatable.data.controller.DataTableController
import org.smartregister.fct.datatable.domain.feature.DTCountable
import org.smartregister.fct.datatable.domain.feature.DTPagination
import org.smartregister.fct.datatable.domain.feature.DTRefreshable

@Composable
internal fun TopBar(
    controller: DataTableController
) {

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            if (controller is DTRefreshable) {
                Row {
                    Spacer(Modifier.width(12.dp))
                    SmallIconButton(
                        iconModifier = Modifier.size(18.dp),
                        icon = AuroraIconPack.Sync,
                        onClick = {
                            scope.launch {
                                controller.refreshData()
                            }
                        },
                    )
                }
            } else {
                Box(Modifier)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (controller is DTCountable) {
                    Text(
                        text = "Total ${controller.totalRecords()} Record(s)"
                    )
                    Spacer(Modifier.width(12.dp))
                }

                if (controller is DTPagination) {
                    SmallIconButton(
                        iconModifier = Modifier.size(22.dp),
                        icon = Icons.Outlined.FirstPage,
                        enable = controller.canGoFirstPage(),
                        onClick = {
                            scope.launch {
                                controller.gotoFirstPage()
                            }
                        }
                    )
                    Spacer(Modifier.width(12.dp))
                    SmallIconButton(
                        iconModifier = Modifier.size(22.dp),
                        icon = Icons.Outlined.ChevronLeft,
                        enable = controller.canGoPreviousPage(),
                        onClick = {
                            scope.launch {
                                controller.gotoPreviousPage()
                            }
                        }
                    )
                    Spacer(Modifier.width(12.dp))
                    NumberDropDown(
                        modifier = Modifier.width(100.dp),
                        segments = listOf(10, 50, 100, 500, 1000),
                        defaultValue = controller.getLimit(),
                        errorHighlight = false,
                        min = 1,
                        max = 1000,
                        onValueChanged = { num ->
                            scope.launch {
                                controller.setLimit(num)
                            }
                        },
                        onSelected = {
                            scope.launch {
                                controller.changeLimit(it)
                            }
                        }
                    )
                    Spacer(Modifier.width(12.dp))
                    SmallIconButton(
                        iconModifier = Modifier.size(22.dp),
                        icon = Icons.Outlined.ChevronRight,
                        enable = controller.canGoNextPage(),
                        onClick = {
                            scope.launch {
                                controller.gotoNextPage()
                            }
                        }
                    )
                    Spacer(Modifier.width(12.dp))
                    SmallIconButton(
                        iconModifier = Modifier.size(22.dp),
                        icon = Icons.AutoMirrored.Outlined.LastPage,
                        enable = controller.canGoLastPage(),
                        onClick = {
                            scope.launch {
                                controller.gotoLastPage()
                            }
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }
        }

        if (controller.loading.collectAsState().value) {
            LinearIndicator(Modifier.fillMaxWidth().offset(y = -(1.dp)))
        }
    }
}