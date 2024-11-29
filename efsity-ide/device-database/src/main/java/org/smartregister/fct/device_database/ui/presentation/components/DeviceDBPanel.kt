package org.smartregister.fct.device_database.ui.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.common.data.manager.AuroraManager
import org.smartregister.fct.common.presentation.ui.components.AuroraTabs
import org.smartregister.fct.device_database.data.persistence.DeviceDBConfigPersistence
import org.smartregister.fct.device_database.ui.components.DeviceDBPanelComponent
import org.smartregister.fct.device_database.ui.components.QueryTabBaseComponent
import org.smartregister.fct.device_database.ui.components.QueryTabComponent
import org.smartregister.fct.device_database.ui.components.TableTabComponent

context (AuroraManager)
@Composable
internal fun AppDatabasePanel(componentContext: ComponentContext) {

    val deviceDBPanelComponent = remember { DeviceDBPanelComponent(componentContext) }

    val listOfTables by deviceDBPanelComponent.listOfTables.collectAsState()
    val anyError by deviceDBPanelComponent.error.collectAsState()

    showErrorSnackbar(anyError)

    Row {
        SidePanel(
            listOfTables = listOfTables,
            onTableDoubleClick = {
                var tabIndex = 0
                val tabs = DeviceDBConfigPersistence.controller.getItems()
                val tabAlreadyExists = tabs.filterIndexed { index, tab ->
                    val isFound = tab is TableTabComponent && tab.tableInfo.name == it.name
                    if (isFound) tabIndex = index
                    isFound
                }

                if (tabAlreadyExists.isNotEmpty()) {
                    DeviceDBConfigPersistence.controller.select(tabIndex)
                } else {
                    DeviceDBConfigPersistence.addNewTab(
                        TableTabComponent(
                            componentContext = deviceDBPanelComponent,
                            tableInfo = it,
                            database = DeviceDBConfigPersistence.sidePanelDBInfo.name
                        )
                    )
                }
            },
            isTablesLoading = deviceDBPanelComponent.loadingTables.collectAsState().value,
            refreshTables = deviceDBPanelComponent::reFetchTables,
            openNewTab = {
                DeviceDBConfigPersistence.controller.add(
                    QueryTabComponent(deviceDBPanelComponent)
                )
            },
            onDBSelected = deviceDBPanelComponent::updateDatabase,
        )
        VerticalDivider()
        MainContent(deviceDBPanelComponent)
    }
}

@Composable
internal fun MainContent(componentContext: ComponentContext) {

    AuroraTabs(
        tabsController = DeviceDBConfigPersistence.controller,
        noContent = {
            DeviceDBConfigPersistence.addNewTab(
                QueryTabComponent(componentContext)
            )
        }
    ) {
        DeviceDBTabPanel(componentContext, this)
    }
}

@Composable
internal fun DeviceDBTabPanel(
    componentContext: ComponentContext,
    queryTabBaseComponent: QueryTabBaseComponent,
) {
    when (queryTabBaseComponent) {
        is QueryTabComponent -> QueryTabPanel(
            componentContext = componentContext,
            tabComponent = queryTabBaseComponent
        )
        is TableTabComponent -> TableTabPanel(
            componentContext = componentContext,
            tableTabComponent = queryTabBaseComponent
        )
    }
}







