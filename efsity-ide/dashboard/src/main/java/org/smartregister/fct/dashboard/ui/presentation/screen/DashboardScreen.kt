package org.smartregister.fct.dashboard.ui.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import org.smartregister.fct.aurora.presentation.ui.components.CardWidget
import org.smartregister.fct.dashboard.ui.components.DashboardScreenComponent
import org.smartregister.fct.dashboard.ui.presentation.components.DeviceInfo
import org.smartregister.fct.dashboard.ui.presentation.components.FhirResourcesList
import org.smartregister.fct.insights.presentation.ui.view.AllInsights

@Composable
fun DashboardScreen(component: DashboardScreenComponent) {
    ConstraintLayout(Modifier.fillMaxSize()) {

        val (deviceInfoRef, resourceListRef, insightsRef) = createRefs()

        CardWidget(
            modifier = Modifier.width(330.dp).constrainAs(deviceInfoRef) {
                top.linkTo(parent.top, 16.dp)
                start.linkTo(parent.start, 16.dp)
                //bottom.linkTo(parent.bottom, 16.dp)
                width = Dimension.preferredWrapContent
                //height = Dimension.fillToConstraints
            }
        ) {
            DeviceInfo(component.deviceInfoComponent)
        }

        CardWidget(
            modifier = Modifier.width(330.dp).constrainAs(resourceListRef) {
                top.linkTo(deviceInfoRef.bottom, 16.dp)
                start.linkTo(parent.start, 16.dp)
                bottom.linkTo(parent.bottom, 16.dp)
                width = Dimension.preferredWrapContent
                height = Dimension.fillToConstraints
            }
        ) {
            FhirResourcesList()
        }

        Box(
            modifier = Modifier.constrainAs(insightsRef) {
                top.linkTo(parent.top, 16.dp)
                start.linkTo(deviceInfoRef.end, 16.dp)
                end.linkTo(parent.end, 16.dp)
                bottom.linkTo(parent.bottom, 16.dp)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        ) {
            AllInsights(component.insightsComponent)
        }
    }
}