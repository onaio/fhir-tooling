package org.smartregister.fct.common.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.ScrollableTabRow
import org.smartregister.fct.aurora.presentation.ui.components.Tab
import org.smartregister.fct.aurora.presentation.ui.components.TabRow
import org.smartregister.fct.common.data.controller.TabsControllerImpl
import org.smartregister.fct.common.domain.controller.TabsController
import org.smartregister.fct.common.domain.model.TabType
import org.smartregister.fct.common.presentation.ui.dialog.rememberConfirmationDialog

@Composable
fun<T> rememberTabsController(
    items: List<T>,
    title: (Int, T) -> String,
    defaultTabIndex: Int = 0,
    tabType: TabType = TabType.Filled,
    showCloseIcon: Boolean = true,
) : TabsController<T> {
    return remember {
        TabsControllerImpl(
            items = items,
            title = title,
            defaultTabIndex = defaultTabIndex,
            tabType = tabType,
            showCloseIcon = showCloseIcon
        )
    }
}

@Composable
fun<T> AuroraTabs(
    modifier: Modifier = Modifier.fillMaxSize(),
    tabsController: TabsController<T>,
    noContent: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable T.(Int) -> Unit
) {

    val impl = tabsController as TabsControllerImpl
    val tabData by impl.tabData.collectAsState()
    val activeIndex = tabData.activeIndex
    val items = tabData.items

    with(tabsController) {
        TabRowView(
            modifier = modifier,
            tabs = items,
            selectedTabIndex = activeIndex,
            tabType = impl.tabType,
            noContent = noContent
        ) {
            content(items[activeIndex], activeIndex)
        }
    }
}

context (TabsController<T>)
@Composable
private fun<T> TabRowView(
    modifier: Modifier,
    tabs: List<T>,
    selectedTabIndex: Int,
    tabType: TabType,
    noContent: (@Composable BoxScope.() -> Unit)? = null,
    tabsContent: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        when (tabType) {
            is TabType.Filled -> {
                FilledTabView(
                    tabs = tabs,
                    selectedTabIndex = selectedTabIndex
                )
            }
            is TabType.Scrollable -> {
                ScrollableTabView(
                    tabs = tabs,
                    selectedTabIndex = selectedTabIndex
                )
            }
        }

        if (tabs.isNotEmpty()) {
            tabsContent()
        } else {
            Box(Modifier.fillMaxSize()) {
                noContent?.invoke(this)
            }
        }
    }

}

context (TabsController<T>)
@Composable
private fun<T> ScrollableTabView(
    tabs: List<T>,
    selectedTabIndex: Int,
) {

    Box(Modifier.fillMaxWidth()) {
        ScrollableTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = selectedTabIndex,
            tabs = {
                TabView(
                    tabs = tabs,
                    selectedTabIndex = selectedTabIndex
                )
            }
        )
    }
}

context (TabsController<T>)
@Composable
private fun<T> FilledTabView(
    tabs: List<T>,
    selectedTabIndex: Int,
) {

    Box(Modifier.fillMaxWidth()) {
        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = selectedTabIndex,
            tabs = {
                TabView(
                    tabs = tabs,
                    selectedTabIndex = selectedTabIndex
                )
            }
        )
    }
}

context (TabsController<T>)
@Composable
private fun<T> TabView(
    tabs: List<T>,
    selectedTabIndex: Int,
) {

    val confirmCloseTabDialogController = rememberConfirmationDialog<Int> { _ , index ->
        close(index!!)
    }

    tabs.forEachIndexed { index, item ->
        Tab(
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        title(index, item),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (showCloseIcon) {
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clickable(
                                    onClick = {
                                        confirmCloseTabDialogController.show(
                                            title = "Close Tab",
                                            message = "Are you sure you want to close ${title(index, item)} tab?",
                                            data = index
                                        )
                                    },
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(
                                        bounded = false,
                                        radius = 15.dp
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                icon = Icons.Rounded.Close,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }

            },
            selected = index == selectedTabIndex,
            onClick = {
                select(index)
            }
        )
    }
}