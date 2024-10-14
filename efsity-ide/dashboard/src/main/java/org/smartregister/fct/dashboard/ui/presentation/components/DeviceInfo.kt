package org.smartregister.fct.dashboard.ui.presentation.components

import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import org.smartregister.fct.adb.domain.model.DeviceInfo
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.ElectricBoltFill
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.dashboard.ui.components.DeviceInfoComponent

@Composable
internal fun DeviceInfo(component: DeviceInfoComponent) {

    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val deviceInfo by component.deviceInfo.collectAsState()
        if (deviceInfo != null) {
            Spacer(Modifier.height(12.dp))
            BatteryIndicator(deviceInfo!!)
            Spacer(Modifier.height(16.dp))
            ManufacturerInfo(deviceInfo!!)
            Spacer(Modifier.height(16.dp))
            ResolutionInfo(deviceInfo!!)
        } else {
            NoDeviceFound()
        }
    }
}

@Composable
private fun NoDeviceFound() {
    Text("No Device Found")
}

@Composable
private fun BatteryIndicator(
    deviceInfo: DeviceInfo,
) {
    Box(Modifier.size(280.dp)) {
        BatteryProgressBar(deviceInfo)
        TemperatureIndicator(deviceInfo)
    }
}

context(BoxScope)
@Composable
private fun BatteryProgressBar(
    deviceInfo: DeviceInfo
) {

    val baseColor = colorScheme.surface
    val anyPoweredSource = deviceInfo.batteryAndOtherInfo.anyPoweredSource()
    val animBatteryLevel by animateIntAsState(
        targetValue = deviceInfo.batteryAndOtherInfo.level,
        animationSpec = tween(
            durationMillis = 1000,
            easing = Ease
        )
    )

    val gradientBrush = remember {
        Brush.sweepGradient(
            *arrayOf(
                0f to Color(0xffe00000),
                0.1f to Color(0xFFFF0000),
                0.3f to Color(0xffff5f17),
                0.5f to Color(0xffffae17),
                0.6f to Color(0xffffd600),
                0.7f to Color(0xffc8ff00),
                //0.8f to Color(0xffa2ff00),
                0.8f to Color(0xff48ff00),
                0.95f to Color(0xff48ff00),
                1f to Color(0xffe00000),
                //0.9f to Color(0xff48ff00),
                //1f to Color(0xff39e500),

            )
        )
    }

    Canvas(Modifier.fillMaxSize()) {

        val rect = Rect(Offset.Zero, size)
        rotate(112.5f, rect.center) {
            drawArc(
                startAngle = 0f,
                sweepAngle = 315f,
                color = baseColor,
                useCenter = false,
                style = Stroke(
                    width = 15f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                ),
            )

            drawArc(
                startAngle = 0f,
                sweepAngle = 315f / 100f * animBatteryLevel.toFloat(),
                brush = gradientBrush,
                useCenter = false,
                style = Stroke(
                    width = 15f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                ),
            )
        }
    }

    if (anyPoweredSource) {
        Box(Modifier.align(Alignment.TopCenter).padding(top = 50.dp)) {
            Icon(
                modifier = Modifier.size(40.dp),
                icon = AuroraIconPack.ElectricBoltFill,
                tint = Color(0xff3eda00)
            )
        }
    }

    Text(
        modifier = Modifier.align(Alignment.Center),
        text = "$animBatteryLevel%",
        style = typography.displayLarge
    )

    Text(
        modifier = Modifier.align(Alignment.BottomCenter),
        text = "Battery",
        style = typography.titleMedium
    )
}

context(BoxScope)
@Composable
private fun TemperatureIndicator(
    deviceInfo: DeviceInfo
) {

    val baseColor = colorScheme.surface
    val anyPoweredSource = deviceInfo.batteryAndOtherInfo.anyPoweredSource()
    val animBatteryLevel by animateIntAsState(
        targetValue = deviceInfo.batteryAndOtherInfo.level,
        animationSpec = tween(
            durationMillis = 1000,
            easing = Ease
        )
    )

    val gradientBrush = remember {
        Brush.sweepGradient(
            *arrayOf(
                0f to Color(0xffe00000),
                0.2f to Color(0xffff1515),
                0.21f to Color(0xffffb102),
                0.55f to Color(0xffffb102),
                0.56f to Color(0xff1797ff),
                0.7f to Color(0xff1797ff),
                0.8f to Color(0xff178bff),
                0.95f to Color(0xff1759ff),
                1f to Color(0xffe00000),

            )
        )
    }


    Canvas(Modifier.width(70.dp).height(70.dp)
        .offset(y = -(30.dp))
        .align(Alignment.BottomCenter)
    ) {

            val rect = Rect(Offset.Zero, size)
            rotate(135f, rect.center) {
                drawArc(
                    startAngle = 0f,
                    sweepAngle = 270f,
                    brush = gradientBrush,
                    useCenter = false,
                    style = Stroke(
                        width = 6f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    ),
                )
            }
    }

    Text(
        modifier = Modifier.align(Alignment.BottomCenter).offset(y = -(55.dp)),
        text = "${String.format("%.1f", deviceInfo.batteryAndOtherInfo.getCelsiusTemperature())}℃",
        style = typography.titleSmall
    )
}

@Composable
private fun ManufacturerInfo(deviceInfo: DeviceInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface.copy(0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Device: ",
                    style = typography.titleSmall
                )
                Text(
                    text = deviceInfo.name,
                    style = typography.bodySmall
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Android: ",
                        style = typography.titleSmall
                    )
                    Text(
                        text = deviceInfo.version,
                        style = typography.bodySmall
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Api Level: ",
                        style = typography.titleSmall
                    )
                    Text(
                        text = deviceInfo.apiLevel,
                        style = typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ResolutionInfo(deviceInfo: DeviceInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface.copy(0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Resolution: ",
                    style = typography.titleSmall
                )
                Text(
                    text = "(${deviceInfo.resolution?.width},${deviceInfo.resolution?.height})",
                    style = typography.bodySmall
                )
            }
        }
    }
}