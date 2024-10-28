package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ScrollableTabRow as Mat3ScrollableTabRow
import androidx.compose.material3.Tab as Mat3Tab
import androidx.compose.material3.TabRow as Mat3TabRow

@Composable
fun<T> Tabs(
    tabs: List<T>,
    title: (T) -> String,
    onClose: ((T) -> Unit)? = null,
    onSelected:  @Composable (ColumnScope.(Int, T) -> Unit)? = null
) {
    var tabIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth()) {
            TabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = tabIndex,
            ) {
                tabs.forEachIndexed { index, item ->
                    Tab(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    title(item),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                onClose?.let {
                                    Box(
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .clickable(
                                                onClick = { onClose.invoke(item) },
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
                        selected = index == tabIndex,
                        onClick = {
                            tabIndex = index
                        }
                    )
                }
            }
        }

        if (tabs.isNotEmpty() && tabIndex < tabs.size) {
            onSelected?.invoke(this, tabIndex, tabs[tabIndex])
        }
    }
}

@Composable
fun<T> ScrollableTabs(
    tabs: List<T>,
    title: (T) -> String,
    onClose: ((T) -> Unit)? = null,
    onSelected:  @Composable (ColumnScope.(Int, T) -> Unit)? = null
) {
    var tabIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth()) {
            ScrollableTabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = tabIndex,
            ) {
                tabs.forEachIndexed { index, item ->
                    Tab(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    title(item),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                onClose?.let {
                                    Box(
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .clickable(
                                                onClick = { onClose.invoke(item) },
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
                        selected = index == tabIndex,
                        onClick = {
                            tabIndex = index
                        }
                    )
                }
            }
        }

        if (tabs.isNotEmpty() && tabIndex < tabs.size) {
            onSelected?.invoke(this, tabIndex, tabs[tabIndex])
        }
    }
}

@Composable
fun TabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = TabRowDefaults.primaryContainerColor,
    contentColor: Color = TabRowDefaults.primaryContentColor,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->
        if (selectedTabIndex < tabPositions.size) {
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = MaterialTheme.colorScheme.primary,
                height = 2.dp
            )
        }
    },
    divider: @Composable () -> Unit = @Composable {
        HorizontalDivider()
    },
    tabs: @Composable () -> Unit
) {
    Mat3TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        indicator = indicator,
        divider = divider,
        tabs = tabs
    )
}

@Composable
fun ScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = TabRowDefaults.primaryContainerColor,
    contentColor: Color = TabRowDefaults.primaryContentColor,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->
        if (selectedTabIndex < tabPositions.size) {
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = MaterialTheme.colorScheme.primary,
                height = 2.dp
            )
        }
    },
    divider: @Composable () -> Unit = @Composable {
        HorizontalDivider()
    },
    tabs: @Composable () -> Unit
) {
    Mat3ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        indicator = indicator,
        divider = divider,
        tabs = tabs
    )
}

@Composable
fun Tab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Mat3Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier.height(40.dp)
            .background(if (selected) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface),
        enabled = enabled,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        interactionSource = interactionSource,
        text = text,
        icon = icon
    )
}

@Composable
fun Tab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    title: String,
    icon: @Composable (() -> Unit)? = null,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Mat3Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier.height(40.dp)
            .background(if (selected) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface),
        enabled = enabled,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        interactionSource = interactionSource,
        text = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        icon = icon
    )
}

@Composable
fun<T> CloseableTab(
    index: Int,
    item: T,
    title: (T) -> String,
    selected: Boolean,
    onClick: (Int) -> Unit,
    onClose: (Int) -> Unit
) {
    Tab(
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    title(item),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box(
                    modifier = Modifier
                        .offset(x = 20.dp)
                        .minimumInteractiveComponentSize()
                        .clickable(
                            onClick = { onClose(index) },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = false,
                                radius = 15.dp
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        icon = Icons.Rounded.Close,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

        },
        selected = selected,
        onClick = { onClick(index) }
    )
}

