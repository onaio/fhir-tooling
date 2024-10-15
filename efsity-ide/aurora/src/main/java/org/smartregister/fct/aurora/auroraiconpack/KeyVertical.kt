package org.smartregister.fct.aurora.auroraiconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.AuroraIconPack

public val AuroraIconPack.KeyVertical: ImageVector
    get() {
        if (_keyVertical != null) {
            return _keyVertical!!
        }
        _keyVertical = Builder(name = "KeyVertical", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(435.39f, 280.0f)
                quadToRelative(0.0f, -26.65f, 18.98f, -45.63f)
                quadToRelative(18.98f, -18.98f, 45.63f, -18.98f)
                quadToRelative(26.65f, 0.0f, 45.63f, 18.98f)
                quadToRelative(18.98f, 18.98f, 18.98f, 45.63f)
                quadToRelative(0.0f, 26.65f, -18.98f, 45.63f)
                quadToRelative(-18.98f, 18.98f, -45.63f, 18.98f)
                quadToRelative(-26.65f, 0.0f, -45.63f, -18.98f)
                quadToRelative(-18.98f, -18.98f, -18.98f, -45.63f)
                close()
                moveTo(280.0f, 280.0f)
                quadToRelative(0.0f, -91.67f, 64.14f, -155.83f)
                quadTo(408.28f, 60.0f, 499.91f, 60.0f)
                quadToRelative(91.63f, 0.0f, 155.86f, 64.17f)
                quadTo(720.0f, 188.33f, 720.0f, 280.0f)
                quadToRelative(0.0f, 64.31f, -33.19f, 116.31f)
                quadToRelative(-33.2f, 52.0f, -86.81f, 79.38f)
                verticalLineToRelative(345.0f)
                quadToRelative(0.0f, 7.07f, -2.62f, 13.69f)
                quadToRelative(-2.61f, 6.62f, -8.23f, 12.23f)
                lineToRelative(-63.84f, 63.85f)
                quadToRelative(-5.62f, 5.62f, -11.9f, 7.92f)
                quadToRelative(-6.28f, 2.31f, -13.46f, 2.31f)
                quadToRelative(-7.18f, 0.0f, -13.41f, -2.31f)
                quadToRelative(-6.23f, -2.3f, -11.85f, -7.92f)
                lineTo(369.23f, 804.62f)
                quadToRelative(-4.65f, -4.5f, -7.44f, -10.79f)
                quadToRelative(-2.79f, -6.29f, -3.4f, -12.52f)
                quadToRelative(-0.62f, -6.23f, 1.19f, -12.46f)
                reflectiveQuadToRelative(6.04f, -11.85f)
                lineToRelative(37.07f, -49.31f)
                lineToRelative(-41.46f, -55.84f)
                quadToRelative(-3.61f, -4.62f, -5.23f, -9.85f)
                quadToRelative(-1.61f, -5.23f, -1.61f, -10.85f)
                quadToRelative(0.0f, -5.61f, 1.8f, -11.15f)
                quadToRelative(1.81f, -5.54f, 5.04f, -10.15f)
                lineTo(400.0f, 553.46f)
                verticalLineToRelative(-77.77f)
                quadToRelative(-53.23f, -27.38f, -86.61f, -79.38f)
                quadTo(280.0f, 344.31f, 280.0f, 280.0f)
                close()
                moveTo(340.0f, 280.0f)
                quadToRelative(0.0f, 57.54f, 34.77f, 99.65f)
                quadToRelative(34.77f, 42.12f, 85.23f, 54.97f)
                verticalLineToRelative(137.69f)
                lineToRelative(-39.85f, 57.61f)
                quadToRelative(0.0f, -0.38f, -0.3f, -0.19f)
                quadToRelative(-0.31f, 0.19f, 0.3f, 0.19f)
                lineToRelative(57.16f, 78.16f)
                lineTo(425.38f, 776.0f)
                horizontalLineToRelative(0.2f)
                horizontalLineToRelative(-0.2f)
                lineTo(500.0f, 850.62f)
                verticalLineToRelative(-0.31f)
                verticalLineToRelative(0.31f)
                lineToRelative(40.0f, -40.0f)
                horizontalLineToRelative(0.31f)
                horizontalLineToRelative(-0.31f)
                verticalLineToRelative(-376.0f)
                quadToRelative(50.46f, -12.85f, 85.23f, -54.97f)
                quadTo(660.0f, 337.54f, 660.0f, 280.0f)
                quadToRelative(0.0f, -66.0f, -47.0f, -113.0f)
                reflectiveQuadToRelative(-113.0f, -47.0f)
                quadToRelative(-66.0f, 0.0f, -113.0f, 47.0f)
                reflectiveQuadToRelative(-47.0f, 113.0f)
                close()
            }
        }
        .build()
        return _keyVertical!!
    }

private var _keyVertical: ImageVector? = null
