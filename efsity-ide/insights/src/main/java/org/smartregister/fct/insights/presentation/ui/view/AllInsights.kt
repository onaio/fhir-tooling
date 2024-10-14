package org.smartregister.fct.insights.presentation.ui.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.aurora.presentation.ui.components.CardWidget
import org.smartregister.fct.aurora.presentation.ui.components.LinearIndicator
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.insights.presentation.ui.components.InsightDetail
import org.smartregister.fct.insights.presentation.ui.components.InsightFetchFailed
import org.smartregister.fct.insights.presentation.components.InsightsComponent
import org.smartregister.fct.insights.presentation.ui.components.ResourceTypeCountChart

@Composable
fun AllInsights(component: InsightsComponent) {

    Aurora(component) {

        it.showErrorSnackbar(component.error.collectAsState().value) {
            component.setError(null)
        }

        val insights by component.insights.collectAsState()

        if (insights != null) {

            ConstraintLayout(Modifier.fillMaxSize()) {
                val (detailRef, resourceCountChartRef) = createRefs()

                CardWidget(modifier = Modifier.constrainAs(detailRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }) {
                    InsightDetail(insights!!)
                }

                CardWidget(modifier = Modifier.constrainAs(resourceCountChartRef) {
                    start.linkTo(parent.start)
                    top.linkTo(detailRef.bottom, 16.dp)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }) {
                    ResourceTypeCountChart(
                        insights = insights!!,
                        onRefresh = {
                            DeviceManager.getActiveDevice()?.let { device ->
                                component.fetchInsights(device, true)
                            } ?: component.setError("No device found")
                        }
                    )
                }
            }
        } else {
            InsightFetchFailed(component)
        }

        if (component.loading.collectAsState().value) {
            LinearIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
        }
    }
}