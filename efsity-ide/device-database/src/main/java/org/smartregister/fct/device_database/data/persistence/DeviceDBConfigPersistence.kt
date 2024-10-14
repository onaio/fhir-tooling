package org.smartregister.fct.device_database.data.persistence

import org.smartregister.fct.common.data.controller.TabsControllerImpl
import org.smartregister.fct.common.domain.model.TabType
import org.smartregister.fct.device_database.domain.model.DBInfo
import org.smartregister.fct.device_database.domain.model.TableInfo
import org.smartregister.fct.device_database.ui.components.QueryTabBaseComponent
import org.smartregister.fct.device_database.ui.components.QueryTabComponent
import org.smartregister.fct.device_database.ui.components.TableTabComponent

object DeviceDBConfigPersistence {

    const val RESOURCE_DB = "resources.db"
    const val KNOWLEDGE_DB = "knowledge.db"

    internal val controller = TabsControllerImpl(
        items = listOf<QueryTabBaseComponent>(),
        title = { index, tab ->
            when (tab) {
                is QueryTabComponent -> "Query [${(index + 1)}]"
                is TableTabComponent -> tab.tableInfo.name
                else -> "$index"
            }
        },
        tabType = TabType.Scrollable
    )

    val listOfDB = listOf(
        DBInfo(
            name = RESOURCE_DB,
            label = "Resource"
        ),
        DBInfo(
            name = KNOWLEDGE_DB,
            label = "Knowledge"
        )
    )

    internal var sidePanelDBInfo: DBInfo = listOfDB[0]

    internal val tablesMap = mutableMapOf<String, List<TableInfo>>(
        Pair(listOfDB[0].name, listOf()),
        Pair(listOfDB[1].name, listOf()),
    )

    internal fun addNewTab(queryTabBaseComponent: QueryTabBaseComponent) {
        controller.add(queryTabBaseComponent)
    }
}