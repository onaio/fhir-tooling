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

public val AuroraIconPack.SearchInsights: ImageVector
    get() {
        if (_searchInsights != null) {
            return _searchInsights!!
        }
        _searchInsights = Builder(name = "SearchInsights", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(400.0f, 640.77f)
                quadToRelative(100.0f, 0.0f, 170.0f, -70.11f)
                quadToRelative(70.0f, -70.12f, 70.0f, -170.28f)
                reflectiveQuadToRelative(-70.0f, -170.27f)
                quadTo(500.0f, 160.0f, 400.0f, 160.0f)
                reflectiveQuadToRelative(-170.0f, 70.11f)
                quadToRelative(-70.0f, 70.11f, -70.0f, 170.27f)
                quadToRelative(0.0f, 100.16f, 70.0f, 170.28f)
                quadToRelative(70.0f, 70.11f, 170.0f, 70.11f)
                close()
                moveTo(399.99f, 511.92f)
                quadToRelative(-12.76f, 0.0f, -21.37f, -8.62f)
                quadToRelative(-8.62f, -8.63f, -8.62f, -21.38f)
                verticalLineToRelative(-199.23f)
                quadToRelative(0.0f, -12.75f, 8.63f, -21.37f)
                quadToRelative(8.63f, -8.63f, 21.38f, -8.63f)
                quadToRelative(12.76f, 0.0f, 21.37f, 8.63f)
                quadToRelative(8.62f, 8.62f, 8.62f, 21.37f)
                verticalLineToRelative(199.23f)
                quadToRelative(0.0f, 12.75f, -8.63f, 21.38f)
                quadToRelative(-8.63f, 8.62f, -21.38f, 8.62f)
                close()
                moveTo(263.06f, 511.92f)
                quadToRelative(-12.75f, 0.0f, -21.37f, -8.62f)
                quadToRelative(-8.61f, -8.63f, -8.61f, -21.38f)
                verticalLineToRelative(-121.15f)
                quadToRelative(0.0f, -12.75f, 8.63f, -21.38f)
                quadToRelative(8.63f, -8.62f, 21.38f, -8.62f)
                quadToRelative(12.76f, 0.0f, 21.37f, 8.62f)
                quadToRelative(8.62f, 8.63f, 8.62f, 21.38f)
                verticalLineToRelative(121.15f)
                quadToRelative(0.0f, 12.75f, -8.63f, 21.38f)
                quadToRelative(-8.63f, 8.62f, -21.39f, 8.62f)
                close()
                moveTo(536.91f, 511.92f)
                quadToRelative(-12.76f, 0.0f, -21.37f, -8.62f)
                quadToRelative(-8.62f, -8.63f, -8.62f, -21.38f)
                lineTo(506.92f, 400.0f)
                quadToRelative(0.0f, -12.75f, 8.63f, -21.37f)
                quadToRelative(8.63f, -8.63f, 21.39f, -8.63f)
                quadToRelative(12.75f, 0.0f, 21.37f, 8.63f)
                quadToRelative(8.61f, 8.62f, 8.61f, 21.37f)
                verticalLineToRelative(81.92f)
                quadToRelative(0.0f, 12.75f, -8.63f, 21.38f)
                quadToRelative(-8.63f, 8.62f, -21.38f, 8.62f)
                close()
                moveTo(400.0f, 700.0f)
                quadToRelative(-125.62f, 0.0f, -212.81f, -87.17f)
                reflectiveQuadTo(100.0f, 400.06f)
                quadToRelative(0.0f, -125.6f, 87.17f, -212.83f)
                quadTo(274.34f, 100.0f, 399.94f, 100.0f)
                quadToRelative(125.6f, 0.0f, 212.83f, 87.19f)
                quadTo(700.0f, 274.38f, 700.0f, 400.0f)
                quadToRelative(0.0f, 54.34f, -17.89f, 102.86f)
                quadToRelative(-17.88f, 48.52f, -49.88f, 87.22f)
                lineToRelative(207.08f, 207.07f)
                quadToRelative(8.3f, 8.18f, 8.3f, 20.82f)
                reflectiveQuadToRelative(-8.3f, 21.14f)
                quadToRelative(-8.31f, 8.5f, -21.08f, 8.31f)
                quadToRelative(-12.77f, -0.19f, -21.08f, -8.5f)
                lineTo(590.46f, 632.23f)
                quadToRelative(-39.08f, 32.0f, -87.6f, 49.88f)
                quadTo(454.34f, 700.0f, 400.0f, 700.0f)
                close()
            }
        }
        .build()
        return _searchInsights!!
    }

private var _searchInsights: ImageVector? = null
