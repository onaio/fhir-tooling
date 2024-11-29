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

public val AuroraIconPack.JoinLeft: ImageVector
    get() {
        if (_joinLeft != null) {
            return _joinLeft!!
        }
        _joinLeft = Builder(name = "JoinLeft", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(320.0f, 720.0f)
                quadToRelative(-100.08f, 0.0f, -170.04f, -69.96f)
                reflectiveQuadTo(80.0f, 480.0f)
                quadToRelative(0.0f, -100.08f, 69.96f, -170.04f)
                reflectiveQuadTo(320.0f, 240.0f)
                quadToRelative(36.23f, 0.0f, 69.42f, 10.38f)
                quadToRelative(33.2f, 10.39f, 61.04f, 30.16f)
                quadToRelative(-41.08f, 39.0f, -63.46f, 90.84f)
                quadToRelative(-22.39f, 51.85f, -22.39f, 108.62f)
                reflectiveQuadTo(387.0f, 588.62f)
                quadToRelative(22.38f, 51.84f, 63.46f, 90.84f)
                quadToRelative(-27.84f, 19.77f, -61.04f, 30.16f)
                quadTo(356.23f, 720.0f, 320.0f, 720.0f)
                close()
                moveTo(640.0f, 720.0f)
                quadToRelative(-36.23f, 0.0f, -69.42f, -10.38f)
                quadToRelative(-33.2f, -10.39f, -61.04f, -30.16f)
                quadToRelative(7.77f, -7.08f, 14.69f, -15.11f)
                quadToRelative(6.92f, -8.04f, 13.77f, -16.04f)
                quadToRelative(21.77f, 15.07f, 47.54f, 23.38f)
                quadTo(611.31f, 680.0f, 640.0f, 680.0f)
                quadToRelative(83.0f, 0.0f, 141.5f, -58.5f)
                reflectiveQuadTo(840.0f, 480.0f)
                quadToRelative(0.0f, -83.0f, -58.5f, -141.5f)
                reflectiveQuadTo(640.0f, 280.0f)
                quadToRelative(-28.69f, 0.0f, -54.46f, 8.31f)
                reflectiveQuadTo(538.0f, 311.69f)
                quadToRelative(-6.85f, -8.0f, -13.77f, -16.04f)
                quadToRelative(-6.92f, -8.03f, -14.69f, -15.11f)
                quadToRelative(27.84f, -19.77f, 61.04f, -30.16f)
                quadTo(603.77f, 240.0f, 640.0f, 240.0f)
                quadToRelative(100.08f, 0.0f, 170.04f, 69.96f)
                reflectiveQuadTo(880.0f, 480.0f)
                quadToRelative(0.0f, 100.08f, -69.96f, 170.04f)
                reflectiveQuadTo(640.0f, 720.0f)
                close()
                moveTo(480.0f, 657.69f)
                quadToRelative(-37.77f, -33.61f, -58.88f, -80.0f)
                quadTo(400.0f, 531.31f, 400.0f, 480.0f)
                quadToRelative(0.0f, -51.31f, 21.12f, -97.69f)
                quadToRelative(21.11f, -46.39f, 58.88f, -80.0f)
                quadToRelative(37.77f, 33.61f, 58.88f, 80.0f)
                quadTo(560.0f, 428.69f, 560.0f, 480.0f)
                quadToRelative(0.0f, 51.31f, -21.12f, 97.69f)
                quadToRelative(-21.11f, 46.39f, -58.88f, 80.0f)
                close()
            }
        }
        .build()
        return _joinLeft!!
    }

private var _joinLeft: ImageVector? = null
