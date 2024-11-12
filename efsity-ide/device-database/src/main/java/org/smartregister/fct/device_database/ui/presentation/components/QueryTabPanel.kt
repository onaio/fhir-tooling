package org.smartregister.fct.device_database.ui.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.common.domain.model.ResizeOption
import org.smartregister.fct.common.presentation.ui.components.VerticalSplitPane
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.device_database.ui.components.QueryTabComponent

@Composable
internal fun QueryTabPanel(
    tabComponent: QueryTabComponent,
    componentContext: ComponentContext,
    defaultQuery: String? = null,
    onDataSelect: ((String) -> Unit)? = null
) {
    val resultComponent by tabComponent.queryResultDataController.collectAsState()

    Aurora(
        componentContext = tabComponent,
    ) {

        it.showErrorSnackbar(tabComponent.error.collectAsState().value)

        Column {
            QueryTabToolbar(tabComponent)
            HorizontalDivider()
            VerticalSplitPane(
                resizeOption = ResizeOption.Flexible(
                    savedKey = tabComponent,
                    sizeRatio = 0.3f,
                    minSizeRatio = 0.1f,
                    maxSizeRatio = 0.9f
                ),
                topContent = {
                    QueryEditor(tabComponent, defaultQuery)
                },
                bottomContent = {
                    if (resultComponent != null) {
                        QueryResult(
                            component = resultComponent!!,
                            componentContext = componentContext,
                            onDataSelect = onDataSelect
                        )
                    }
                },
                enableBottomContent = resultComponent != null
            )
        }
    }
}