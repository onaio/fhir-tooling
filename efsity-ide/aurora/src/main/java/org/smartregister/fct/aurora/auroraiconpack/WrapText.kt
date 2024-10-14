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

public val AuroraIconPack.WrapText: ImageVector
    get() {
        if (_wrapText != null) {
            return _wrapText!!
        }
        _wrapText = Builder(name = "WrapText", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(210.0f, 495.0f)
                quadToRelative(-12.75f, 0.0f, -21.37f, -8.63f)
                quadToRelative(-8.63f, -8.63f, -8.63f, -21.38f)
                quadToRelative(0.0f, -12.76f, 8.63f, -21.37f)
                quadTo(197.25f, 435.0f, 210.0f, 435.0f)
                horizontalLineToRelative(473.46f)
                quadToRelative(57.35f, 0.0f, 96.94f, 40.04f)
                quadToRelative(39.6f, 40.04f, 39.6f, 97.27f)
                reflectiveQuadToRelative(-39.6f, 97.46f)
                quadTo(740.81f, 710.0f, 683.46f, 710.0f)
                lineTo(569.38f, 710.0f)
                lineToRelative(37.0f, 38.15f)
                quadToRelative(8.93f, 9.31f, 8.55f, 21.26f)
                quadToRelative(-0.37f, 11.94f, -8.55f, 20.9f)
                quadToRelative(-9.3f, 9.3f, -21.57f, 9.61f)
                quadToRelative(-12.27f, 0.31f, -21.19f, -9.0f)
                lineToRelative(-84.85f, -85.61f)
                quadToRelative(-5.23f, -5.62f, -7.54f, -11.9f)
                quadToRelative(-2.31f, -6.28f, -2.31f, -13.46f)
                quadToRelative(0.0f, -7.18f, 2.5f, -13.41f)
                reflectiveQuadToRelative(7.73f, -11.85f)
                lineToRelative(84.47f, -85.23f)
                quadToRelative(8.92f, -9.31f, 21.19f, -9.31f)
                quadToRelative(12.27f, 0.0f, 21.66f, 9.31f)
                quadToRelative(8.61f, 9.31f, 8.72f, 21.39f)
                quadToRelative(0.12f, 12.07f, -8.81f, 21.38f)
                lineToRelative(-37.0f, 37.77f)
                horizontalLineToRelative(114.08f)
                quadToRelative(32.08f, 0.0f, 54.31f, -22.73f)
                quadTo(760.0f, 604.54f, 760.0f, 572.39f)
                quadToRelative(0.0f, -32.16f, -22.23f, -54.77f)
                quadTo(715.54f, 495.0f, 683.46f, 495.0f)
                lineTo(210.0f, 495.0f)
                close()
                moveTo(210.0f, 710.0f)
                quadToRelative(-12.75f, 0.0f, -21.37f, -8.63f)
                quadToRelative(-8.63f, -8.63f, -8.63f, -21.38f)
                quadToRelative(0.0f, -12.76f, 8.63f, -21.37f)
                quadTo(197.25f, 650.0f, 210.0f, 650.0f)
                horizontalLineToRelative(116.15f)
                quadToRelative(12.75f, 0.0f, 21.38f, 8.63f)
                quadToRelative(8.62f, 8.63f, 8.62f, 21.38f)
                quadToRelative(0.0f, 12.76f, -8.62f, 21.37f)
                quadToRelative(-8.63f, 8.62f, -21.38f, 8.62f)
                lineTo(210.0f, 710.0f)
                close()
                moveTo(210.0f, 280.0f)
                quadToRelative(-12.75f, 0.0f, -21.37f, -8.63f)
                quadToRelative(-8.63f, -8.63f, -8.63f, -21.38f)
                quadToRelative(0.0f, -12.76f, 8.63f, -21.37f)
                quadTo(197.25f, 220.0f, 210.0f, 220.0f)
                horizontalLineToRelative(541.15f)
                quadToRelative(12.75f, 0.0f, 21.38f, 8.63f)
                quadToRelative(8.62f, 8.63f, 8.62f, 21.38f)
                quadToRelative(0.0f, 12.76f, -8.62f, 21.37f)
                quadToRelative(-8.63f, 8.62f, -21.38f, 8.62f)
                lineTo(210.0f, 280.0f)
                close()
            }
        }
        .build()
        return _wrapText!!
    }

private var _wrapText: ImageVector? = null
