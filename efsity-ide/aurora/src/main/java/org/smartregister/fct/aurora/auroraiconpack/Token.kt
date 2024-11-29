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

public val AuroraIconPack.Token: ImageVector
    get() {
        if (_token != null) {
            return _token!!
        }
        _token = Builder(name = "Token", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(480.0f, 857.61f)
                quadToRelative(-9.23f, 0.0f, -18.08f, -2.11f)
                quadToRelative(-8.84f, -2.12f, -17.07f, -6.73f)
                lineToRelative(-277.7f, -153.85f)
                quadToRelative(-17.07f, -9.84f, -27.11f, -26.42f)
                quadTo(130.0f, 651.92f, 130.0f, 631.85f)
                verticalLineToRelative(-303.7f)
                quadToRelative(0.0f, -20.07f, 10.04f, -36.65f)
                reflectiveQuadToRelative(27.11f, -26.42f)
                lineToRelative(277.7f, -153.85f)
                quadToRelative(8.23f, -4.61f, 17.07f, -6.73f)
                quadToRelative(8.85f, -2.11f, 18.08f, -2.11f)
                quadToRelative(9.23f, 0.0f, 18.08f, 2.11f)
                quadToRelative(8.84f, 2.12f, 17.07f, 6.73f)
                lineToRelative(277.7f, 153.85f)
                quadToRelative(17.07f, 9.84f, 27.11f, 26.42f)
                quadTo(830.0f, 308.08f, 830.0f, 328.15f)
                verticalLineToRelative(303.7f)
                quadToRelative(0.0f, 20.07f, -10.04f, 36.65f)
                reflectiveQuadToRelative(-27.11f, 26.42f)
                lineToRelative(-277.7f, 153.85f)
                quadToRelative(-8.23f, 4.61f, -17.07f, 6.73f)
                quadToRelative(-8.85f, 2.11f, -18.08f, 2.11f)
                close()
                moveTo(365.92f, 382.31f)
                quadToRelative(22.39f, -25.31f, 51.7f, -38.81f)
                quadTo(446.92f, 330.0f, 480.0f, 330.0f)
                quadToRelative(33.46f, 0.0f, 62.58f, 13.5f)
                quadToRelative(29.11f, 13.5f, 51.5f, 38.81f)
                lineTo(736.39f, 303.0f)
                lineTo(486.15f, 163.92f)
                quadToRelative(-3.07f, -1.54f, -6.15f, -1.54f)
                quadToRelative(-3.08f, 0.0f, -6.15f, 1.54f)
                lineTo(223.61f, 303.0f)
                lineToRelative(142.31f, 79.31f)
                close()
                moveTo(450.0f, 782.92f)
                verticalLineToRelative(-155.61f)
                quadToRelative(-52.0f, -11.7f, -86.0f, -52.27f)
                quadToRelative(-34.0f, -40.58f, -34.0f, -95.04f)
                quadToRelative(0.0f, -12.15f, 1.58f, -22.92f)
                quadToRelative(1.57f, -10.77f, 5.34f, -21.7f)
                lineTo(190.0f, 353.08f)
                verticalLineToRelative(278.38f)
                quadToRelative(0.0f, 3.46f, 1.54f, 6.16f)
                quadToRelative(1.54f, 2.69f, 4.61f, 4.61f)
                lineTo(450.0f, 782.92f)
                close()
                moveTo(480.0f, 570.0f)
                quadToRelative(37.23f, 0.0f, 63.62f, -26.38f)
                quadTo(570.0f, 517.23f, 570.0f, 480.0f)
                reflectiveQuadToRelative(-26.38f, -63.62f)
                quadTo(517.23f, 390.0f, 480.0f, 390.0f)
                reflectiveQuadToRelative(-63.62f, 26.38f)
                quadTo(390.0f, 442.77f, 390.0f, 480.0f)
                reflectiveQuadToRelative(26.38f, 63.62f)
                quadTo(442.77f, 570.0f, 480.0f, 570.0f)
                close()
                moveTo(510.0f, 782.92f)
                lineTo(763.85f, 642.23f)
                quadToRelative(3.07f, -1.92f, 4.61f, -4.61f)
                quadToRelative(1.54f, -2.7f, 1.54f, -6.16f)
                verticalLineToRelative(-278.38f)
                lineToRelative(-146.92f, 82.3f)
                quadToRelative(3.77f, 11.93f, 5.34f, 22.2f)
                quadTo(630.0f, 467.85f, 630.0f, 480.0f)
                quadToRelative(0.0f, 54.46f, -34.0f, 95.04f)
                quadToRelative(-34.0f, 40.57f, -86.0f, 52.27f)
                verticalLineToRelative(155.61f)
                close()
            }
        }
        .build()
        return _token!!
    }

private var _token: ImageVector? = null
