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

public val AuroraIconPack.VisibilityOff: ImageVector
    get() {
        if (_visibilityOff != null) {
            return _visibilityOff!!
        }
        _visibilityOff = Builder(name = "VisibilityOff", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(630.92f, 518.92f)
                lineTo(586.0f, 474.0f)
                quadToRelative(9.0f, -49.69f, -28.35f, -89.35f)
                quadTo(520.31f, 345.0f, 466.0f, 354.0f)
                lineToRelative(-44.92f, -44.92f)
                quadToRelative(13.54f, -6.08f, 27.77f, -9.12f)
                quadToRelative(14.23f, -3.04f, 31.15f, -3.04f)
                quadToRelative(68.08f, 0.0f, 115.58f, 47.5f)
                reflectiveQuadTo(643.08f, 460.0f)
                quadToRelative(0.0f, 16.92f, -3.04f, 31.54f)
                quadToRelative(-3.04f, 14.61f, -9.12f, 27.38f)
                close()
                moveTo(758.15f, 643.38f)
                lineTo(714.0f, 602.0f)
                quadToRelative(38.0f, -29.0f, 67.5f, -63.5f)
                reflectiveQuadTo(832.0f, 460.0f)
                quadToRelative(-50.0f, -101.0f, -143.5f, -160.5f)
                reflectiveQuadTo(480.0f, 240.0f)
                quadToRelative(-29.0f, 0.0f, -57.0f, 4.0f)
                reflectiveQuadToRelative(-55.0f, 12.0f)
                lineToRelative(-46.61f, -46.61f)
                quadToRelative(37.92f, -15.08f, 77.46f, -22.23f)
                quadTo(438.39f, 180.0f, 480.0f, 180.0f)
                quadToRelative(140.61f, 0.0f, 253.61f, 77.54f)
                reflectiveQuadTo(898.46f, 460.0f)
                quadToRelative(-22.23f, 53.61f, -57.42f, 100.08f)
                quadToRelative(-35.2f, 46.46f, -82.89f, 83.3f)
                close()
                moveTo(790.46f, 874.77f)
                lineTo(628.62f, 714.15f)
                quadToRelative(-30.77f, 11.39f, -68.2f, 18.62f)
                quadTo(523.0f, 740.0f, 480.0f, 740.0f)
                quadToRelative(-141.0f, 0.0f, -253.61f, -77.54f)
                quadTo(113.77f, 584.92f, 61.54f, 460.0f)
                quadToRelative(22.15f, -53.0f, 57.23f, -98.88f)
                quadToRelative(35.08f, -45.89f, 77.23f, -79.58f)
                lineToRelative(-110.77f, -112.0f)
                lineToRelative(42.16f, -42.15f)
                lineToRelative(705.22f, 705.22f)
                lineToRelative(-42.15f, 42.16f)
                close()
                moveTo(238.16f, 323.69f)
                quadToRelative(-31.7f, 25.23f, -61.66f, 60.66f)
                quadTo(146.54f, 419.77f, 128.0f, 460.0f)
                quadToRelative(50.0f, 101.0f, 143.5f, 160.5f)
                reflectiveQuadTo(480.0f, 680.0f)
                quadToRelative(27.31f, 0.0f, 54.39f, -4.62f)
                quadToRelative(27.07f, -4.61f, 45.92f, -9.53f)
                lineTo(529.69f, 614.0f)
                quadToRelative(-10.23f, 4.15f, -23.69f, 6.61f)
                quadToRelative(-13.46f, 2.47f, -26.0f, 2.47f)
                quadToRelative(-68.08f, 0.0f, -115.58f, -47.5f)
                reflectiveQuadTo(316.92f, 460.0f)
                quadToRelative(0.0f, -12.15f, 2.47f, -25.42f)
                quadToRelative(2.46f, -13.27f, 6.61f, -24.27f)
                lineToRelative(-87.84f, -86.62f)
                close()
                moveTo(541.0f, 429.0f)
                close()
                moveTo(409.23f, 494.77f)
                close()
            }
        }
        .build()
        return _visibilityOff!!
    }

private var _visibilityOff: ImageVector? = null
