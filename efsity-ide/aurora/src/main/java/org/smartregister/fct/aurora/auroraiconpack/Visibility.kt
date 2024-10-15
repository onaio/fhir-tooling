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

public val AuroraIconPack.Visibility: ImageVector
    get() {
        if (_visibility != null) {
            return _visibility!!
        }
        _visibility = Builder(name = "Visibility", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(480.09f, 623.08f)
                quadToRelative(67.99f, 0.0f, 115.49f, -47.59f)
                reflectiveQuadToRelative(47.5f, -115.58f)
                quadToRelative(0.0f, -67.99f, -47.59f, -115.49f)
                reflectiveQuadToRelative(-115.58f, -47.5f)
                quadToRelative(-67.99f, 0.0f, -115.49f, 47.59f)
                reflectiveQuadToRelative(-47.5f, 115.58f)
                quadToRelative(0.0f, 67.99f, 47.59f, 115.49f)
                reflectiveQuadToRelative(115.58f, 47.5f)
                close()
                moveTo(480.0f, 568.0f)
                quadToRelative(-45.0f, 0.0f, -76.5f, -31.5f)
                reflectiveQuadTo(372.0f, 460.0f)
                quadToRelative(0.0f, -45.0f, 31.5f, -76.5f)
                reflectiveQuadTo(480.0f, 352.0f)
                quadToRelative(45.0f, 0.0f, 76.5f, 31.5f)
                reflectiveQuadTo(588.0f, 460.0f)
                quadToRelative(0.0f, 45.0f, -31.5f, 76.5f)
                reflectiveQuadTo(480.0f, 568.0f)
                close()
                moveTo(480.05f, 740.0f)
                quadToRelative(-137.97f, 0.0f, -251.43f, -76.12f)
                quadTo(115.16f, 587.77f, 61.54f, 460.0f)
                quadToRelative(53.62f, -127.77f, 167.02f, -203.88f)
                quadTo(341.97f, 180.0f, 479.95f, 180.0f)
                quadToRelative(137.97f, 0.0f, 251.43f, 76.12f)
                quadTo(844.84f, 332.23f, 898.46f, 460.0f)
                quadToRelative(-53.62f, 127.77f, -167.02f, 203.88f)
                quadTo(618.03f, 740.0f, 480.05f, 740.0f)
                close()
                moveTo(480.0f, 460.0f)
                close()
                moveTo(480.0f, 680.0f)
                quadToRelative(113.0f, 0.0f, 207.5f, -59.5f)
                reflectiveQuadTo(832.0f, 460.0f)
                quadToRelative(-50.0f, -101.0f, -144.5f, -160.5f)
                reflectiveQuadTo(480.0f, 240.0f)
                quadToRelative(-113.0f, 0.0f, -207.5f, 59.5f)
                reflectiveQuadTo(128.0f, 460.0f)
                quadToRelative(50.0f, 101.0f, 144.5f, 160.5f)
                reflectiveQuadTo(480.0f, 680.0f)
                close()
            }
        }
        .build()
        return _visibility!!
    }

private var _visibility: ImageVector? = null
