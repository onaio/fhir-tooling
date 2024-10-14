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

public val AuroraIconPack.Folder: ImageVector
    get() {
        if (_folder != null) {
            return _folder!!
        }
        _folder = Builder(name = "Folder", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(172.31f, 780.0f)
                quadTo(142.0f, 780.0f, 121.0f, 759.0f)
                quadToRelative(-21.0f, -21.0f, -21.0f, -51.31f)
                verticalLineToRelative(-455.38f)
                quadTo(100.0f, 222.0f, 121.0f, 201.0f)
                quadToRelative(21.0f, -21.0f, 51.31f, -21.0f)
                lineTo(362.0f, 180.0f)
                quadToRelative(14.46f, 0.0f, 27.81f, 5.62f)
                quadToRelative(13.34f, 5.61f, 23.19f, 15.46f)
                lineTo(471.92f, 260.0f)
                horizontalLineToRelative(315.77f)
                quadTo(818.0f, 260.0f, 839.0f, 281.0f)
                quadToRelative(21.0f, 21.0f, 21.0f, 51.31f)
                verticalLineToRelative(375.38f)
                quadTo(860.0f, 738.0f, 839.0f, 759.0f)
                quadToRelative(-21.0f, 21.0f, -51.31f, 21.0f)
                lineTo(172.31f, 780.0f)
                close()
                moveTo(172.31f, 720.0f)
                horizontalLineToRelative(615.38f)
                quadToRelative(5.39f, 0.0f, 8.85f, -3.46f)
                reflectiveQuadToRelative(3.46f, -8.85f)
                verticalLineToRelative(-375.38f)
                quadToRelative(0.0f, -5.39f, -3.46f, -8.85f)
                reflectiveQuadToRelative(-8.85f, -3.46f)
                lineTo(447.38f, 320.0f)
                lineToRelative(-76.53f, -76.54f)
                quadToRelative(-1.93f, -1.92f, -4.04f, -2.69f)
                quadToRelative(-2.12f, -0.77f, -4.43f, -0.77f)
                lineTo(172.31f, 240.0f)
                quadToRelative(-5.39f, 0.0f, -8.85f, 3.46f)
                reflectiveQuadToRelative(-3.46f, 8.85f)
                verticalLineToRelative(455.38f)
                quadToRelative(0.0f, 5.39f, 3.46f, 8.85f)
                reflectiveQuadToRelative(8.85f, 3.46f)
                close()
                moveTo(160.0f, 720.0f)
                verticalLineToRelative(-480.0f)
                verticalLineToRelative(480.0f)
                close()
            }
        }
        .build()
        return _folder!!
    }

private var _folder: ImageVector? = null
