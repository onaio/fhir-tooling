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

public val AuroraIconPack.LocalFireDepartment: ImageVector
    get() {
        if (_localFireDepartment != null) {
            return _localFireDepartment!!
        }
        _localFireDepartment = Builder(name = "LocalFireDepartment", defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(480.0f, 860.0f)
                quadToRelative(-125.54f, 0.0f, -212.77f, -87.23f)
                reflectiveQuadTo(180.0f, 560.0f)
                quadToRelative(0.0f, -101.46f, 58.54f, -197.19f)
                reflectiveQuadToRelative(160.54f, -168.35f)
                quadToRelative(19.3f, -14.23f, 40.5f, -2.84f)
                quadToRelative(21.19f, 11.38f, 21.19f, 34.92f)
                verticalLineToRelative(33.92f)
                quadToRelative(0.0f, 36.69f, 29.84f, 64.12f)
                quadTo(520.46f, 352.0f, 561.0f, 352.0f)
                quadToRelative(17.77f, 0.0f, 34.42f, -6.92f)
                quadToRelative(16.66f, -6.92f, 30.58f, -19.77f)
                quadToRelative(8.0f, -8.08f, 18.96f, -9.42f)
                quadToRelative(10.96f, -1.35f, 20.42f, 5.88f)
                quadToRelative(53.77f, 42.31f, 84.2f, 105.19f)
                quadTo(780.0f, 489.85f, 780.0f, 560.0f)
                quadToRelative(0.0f, 125.54f, -87.23f, 212.77f)
                reflectiveQuadTo(480.0f, 860.0f)
                close()
                moveTo(240.0f, 560.0f)
                quadToRelative(0.0f, 60.46f, 28.89f, 112.54f)
                quadToRelative(28.88f, 52.08f, 79.42f, 84.0f)
                quadToRelative(-4.46f, -8.46f, -6.39f, -16.89f)
                quadTo(340.0f, 731.23f, 340.0f, 722.0f)
                quadToRelative(0.0f, -27.77f, 10.66f, -52.31f)
                quadToRelative(10.65f, -24.54f, 30.57f, -44.46f)
                lineTo(480.0f, 528.08f)
                lineToRelative(99.15f, 97.15f)
                quadToRelative(19.93f, 19.92f, 30.39f, 44.46f)
                reflectiveQuadTo(620.0f, 722.0f)
                quadToRelative(0.0f, 9.23f, -1.92f, 17.65f)
                quadToRelative(-1.93f, 8.43f, -6.39f, 16.89f)
                quadToRelative(50.54f, -31.92f, 79.42f, -84.0f)
                quadTo(720.0f, 620.46f, 720.0f, 560.0f)
                quadToRelative(0.0f, -50.0f, -18.5f, -94.5f)
                reflectiveQuadTo(648.0f, 386.0f)
                quadToRelative(-20.0f, 13.0f, -42.0f, 19.5f)
                reflectiveQuadToRelative(-45.0f, 6.5f)
                quadToRelative(-62.38f, 0.0f, -107.88f, -41.0f)
                quadToRelative(-45.5f, -41.0f, -52.12f, -101.77f)
                quadToRelative(-78.0f, 65.62f, -119.5f, 140.69f)
                quadTo(240.0f, 485.0f, 240.0f, 560.0f)
                close()
                moveTo(480.0f, 612.0f)
                lineTo(423.0f, 668.0f)
                quadToRelative(-11.0f, 11.0f, -17.0f, 25.0f)
                reflectiveQuadToRelative(-6.0f, 29.0f)
                quadToRelative(0.0f, 32.0f, 23.5f, 55.0f)
                reflectiveQuadToRelative(56.5f, 23.0f)
                quadToRelative(33.0f, 0.0f, 56.5f, -23.0f)
                reflectiveQuadToRelative(23.5f, -55.0f)
                quadToRelative(0.0f, -16.0f, -6.0f, -29.5f)
                reflectiveQuadTo(537.0f, 668.0f)
                lineToRelative(-57.0f, -56.0f)
                close()
            }
        }
        .build()
        return _localFireDepartment!!
    }

private var _localFireDepartment: ImageVector? = null
