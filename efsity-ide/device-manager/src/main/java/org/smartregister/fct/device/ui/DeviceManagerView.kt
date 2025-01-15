package org.smartregister.fct.device.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.common.data.enums.RightWindowState
import org.smartregister.fct.common.data.manager.SubWindowManager
import org.smartregister.fct.common.presentation.ui.components.RightWindowHeader

@Composable
fun DeviceManagerWindow(subWindowManager: SubWindowManager) {

    val devices by DeviceManager.getAllDevices().collectAsState(initial = null)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        RightWindowHeader(
            title = "Device Manager",
            onViewModeSelected = {
                subWindowManager.changeRightWindowViewMode(
                    state = RightWindowState.DeviceManager,
                    viewMode = it
                )
            }
        )
        HorizontalDivider()
        Header()
        HorizontalDivider()

        devices?.takeIf { it.isNotEmpty() }?.filterNotNull()?.run {
            LazyColumn(modifier = Modifier.fillMaxSize()) {

                items(this@run) { device ->

                    val deviceInfo = device.getDeviceInfo()

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.height(4.dp))
                        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                            val (nameRef, apiRef, typeRef) = createRefs()

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 8.dp).constrainAs(nameRef) {
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top)
                                    end.linkTo(apiRef.start)
                                    width = Dimension.preferredWrapContent
                                },
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Column {
                                    MediumText("${deviceInfo.model} API ${deviceInfo.apiLevel}")
                                    Text(
                                        text = "Android ${deviceInfo.version}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.width(65.dp).constrainAs(apiRef) {
                                    top.linkTo(nameRef.top)
                                    end.linkTo(typeRef.start)
                                    bottom.linkTo(nameRef.bottom)
                                },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                MediumText(deviceInfo.apiLevel)
                            }

                            Row(
                                modifier = Modifier.width(94.dp).constrainAs(typeRef) {
                                    top.linkTo(nameRef.top)
                                    end.linkTo(parent.end)
                                    bottom.linkTo(nameRef.bottom)
                                },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                MediumText(deviceInfo.type.name)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Column(modifier = Modifier.height(30.dp).fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer)) {

        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (nameHeading, apiHeading, typeHeading) = createRefs()

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp).constrainAs(nameHeading) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(apiHeading.start)
                    width = Dimension.preferredWrapContent
                },
                horizontalArrangement = Arrangement.Start
            ) {
                SmallHeading("Name")
            }

            Row(
                modifier = Modifier.constrainAs(apiHeading) {
                    top.linkTo(parent.top)
                    end.linkTo(typeHeading.start)
                }
            ) {
                VerticalDivider()
                Spacer(Modifier.width(20.dp))
                SmallHeading("API")
                Spacer(Modifier.width(20.dp))
            }

            Row(
                modifier = Modifier.constrainAs(typeHeading) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            ) {
                VerticalDivider()
                Spacer(Modifier.width(30.dp))
                SmallHeading("Type")
                Spacer(Modifier.width(30.dp))
            }
        }
    }
}

@Composable
private fun MediumText(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun SmallHeading(
    text: String
) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 4.dp),
        style = MaterialTheme.typography.titleSmall
    )
}