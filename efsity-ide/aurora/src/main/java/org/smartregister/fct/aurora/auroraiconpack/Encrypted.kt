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

public val AuroraIconPack.Encrypted: ImageVector
    get() {
        if (_encrypted != null) {
            return _encrypted!!
        }
        _encrypted = Builder(name = "Encrypted", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(429.62f, 590.0f)
                horizontalLineToRelative(100.76f)
                lineToRelative(-22.61f, -126.31f)
                quadToRelative(18.84f, -8.08f, 30.34f, -25.34f)
                quadToRelative(11.5f, -17.27f, 11.5f, -38.35f)
                quadToRelative(0.0f, -28.77f, -20.42f, -49.19f)
                quadToRelative(-20.42f, -20.42f, -49.19f, -20.42f)
                quadToRelative(-28.77f, 0.0f, -49.19f, 20.42f)
                quadToRelative(-20.42f, 20.42f, -20.42f, 49.19f)
                quadToRelative(0.0f, 21.08f, 11.5f, 38.35f)
                quadToRelative(11.5f, 17.26f, 30.34f, 25.34f)
                lineTo(429.62f, 590.0f)
                close()
                moveTo(480.0f, 859.23f)
                quadToRelative(-129.77f, -35.39f, -214.88f, -152.77f)
                quadTo(180.0f, 589.08f, 180.0f, 444.0f)
                verticalLineToRelative(-230.15f)
                lineToRelative(300.0f, -112.31f)
                lineToRelative(300.0f, 112.31f)
                lineTo(780.0f, 444.0f)
                quadToRelative(0.0f, 145.08f, -85.12f, 262.46f)
                quadTo(609.77f, 823.84f, 480.0f, 859.23f)
                close()
                moveTo(480.0f, 796.0f)
                quadToRelative(104.0f, -33.0f, 172.0f, -132.0f)
                reflectiveQuadToRelative(68.0f, -220.0f)
                verticalLineToRelative(-189.0f)
                lineToRelative(-240.0f, -89.62f)
                lineTo(240.0f, 255.0f)
                verticalLineToRelative(189.0f)
                quadToRelative(0.0f, 121.0f, 68.0f, 220.0f)
                reflectiveQuadToRelative(172.0f, 132.0f)
                close()
                moveTo(480.0f, 480.38f)
                close()
            }
        }
        .build()
        return _encrypted!!
    }

private var _encrypted: ImageVector? = null
