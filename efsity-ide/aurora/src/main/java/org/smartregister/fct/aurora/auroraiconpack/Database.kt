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

public val AuroraIconPack.Database: ImageVector
    get() {
        if (_database != null) {
            return _database!!
        }
        _database = Builder(name = "Database", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(480.0f, 820.0f)
                quadToRelative(-145.61f, 0.0f, -242.81f, -41.12f)
                quadTo(140.0f, 737.77f, 140.0f, 676.15f)
                lineTo(140.0f, 280.0f)
                quadToRelative(0.0f, -57.92f, 99.54f, -98.96f)
                quadTo(339.08f, 140.0f, 480.0f, 140.0f)
                quadToRelative(140.92f, 0.0f, 240.46f, 41.04f)
                quadTo(820.0f, 222.08f, 820.0f, 280.0f)
                verticalLineToRelative(396.15f)
                quadToRelative(0.0f, 61.62f, -97.19f, 102.73f)
                quadTo(625.61f, 820.0f, 480.0f, 820.0f)
                close()
                moveTo(480.0f, 358.31f)
                quadToRelative(87.46f, 0.0f, 176.12f, -24.73f)
                quadToRelative(88.65f, -24.73f, 102.73f, -53.35f)
                quadToRelative(-13.7f, -29.38f, -101.66f, -54.81f)
                quadTo(569.23f, 200.0f, 480.0f, 200.0f)
                quadToRelative(-89.08f, 0.0f, -176.58f, 24.73f)
                quadToRelative(-87.5f, 24.73f, -103.04f, 53.96f)
                quadToRelative(15.16f, 30.0f, 102.27f, 54.81f)
                quadToRelative(87.12f, 24.81f, 177.35f, 24.81f)
                close()
                moveTo(480.0f, 558.46f)
                quadToRelative(41.62f, 0.0f, 81.0f, -4.0f)
                reflectiveQuadToRelative(75.27f, -11.69f)
                quadToRelative(35.88f, -7.69f, 67.19f, -19.08f)
                quadToRelative(31.31f, -11.38f, 56.54f, -25.77f)
                lineTo(760.0f, 356.0f)
                quadToRelative(-25.23f, 14.38f, -56.54f, 25.77f)
                quadToRelative(-31.31f, 11.38f, -67.19f, 19.07f)
                quadToRelative(-35.89f, 7.7f, -75.27f, 11.7f)
                quadToRelative(-39.38f, 4.0f, -81.0f, 4.0f)
                quadToRelative(-42.38f, 0.0f, -82.58f, -4.2f)
                quadToRelative(-40.19f, -4.19f, -75.88f, -11.88f)
                reflectiveQuadToRelative(-66.5f, -18.88f)
                quadTo(224.23f, 370.38f, 200.0f, 356.0f)
                verticalLineToRelative(141.92f)
                quadToRelative(24.23f, 14.39f, 55.04f, 25.58f)
                quadToRelative(30.81f, 11.19f, 66.5f, 18.88f)
                quadToRelative(35.69f, 7.7f, 75.88f, 11.89f)
                quadToRelative(40.2f, 4.19f, 82.58f, 4.19f)
                close()
                moveTo(480.0f, 760.0f)
                quadToRelative(48.69f, 0.0f, 95.62f, -6.42f)
                quadToRelative(46.92f, -6.43f, 85.38f, -17.54f)
                quadToRelative(38.46f, -11.12f, 64.88f, -25.81f)
                quadToRelative(26.43f, -14.69f, 34.12f, -30.85f)
                verticalLineToRelative(-121.46f)
                quadToRelative(-25.23f, 14.39f, -56.54f, 25.77f)
                quadToRelative(-31.31f, 11.39f, -67.19f, 19.08f)
                quadToRelative(-35.89f, 7.69f, -75.27f, 11.69f)
                quadToRelative(-39.38f, 4.0f, -81.0f, 4.0f)
                quadToRelative(-42.38f, 0.0f, -82.58f, -4.19f)
                quadToRelative(-40.19f, -4.19f, -75.88f, -11.89f)
                quadToRelative(-35.69f, -7.69f, -66.5f, -18.88f)
                quadToRelative(-30.81f, -11.19f, -55.04f, -25.58f)
                lineTo(200.0f, 680.0f)
                quadToRelative(7.69f, 16.54f, 33.81f, 30.73f)
                quadToRelative(26.11f, 14.19f, 64.57f, 25.31f)
                quadToRelative(38.47f, 11.11f, 85.7f, 17.54f)
                quadTo(431.31f, 760.0f, 480.0f, 760.0f)
                close()
            }
        }
        .build()
        return _database!!
    }

private var _database: ImageVector? = null
