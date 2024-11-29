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

public val AuroraIconPack.Delete: ImageVector
    get() {
        if (_delete != null) {
            return _delete!!
        }
        _delete = Builder(name = "Delete", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(292.31f, 820.0f)
                quadToRelative(-29.92f, 0.0f, -51.12f, -21.19f)
                quadTo(220.0f, 777.61f, 220.0f, 747.69f)
                lineTo(220.0f, 240.0f)
                horizontalLineToRelative(-40.0f)
                verticalLineToRelative(-60.0f)
                horizontalLineToRelative(180.0f)
                verticalLineToRelative(-35.38f)
                horizontalLineToRelative(240.0f)
                lineTo(600.0f, 180.0f)
                horizontalLineToRelative(180.0f)
                verticalLineToRelative(60.0f)
                horizontalLineToRelative(-40.0f)
                verticalLineToRelative(507.69f)
                quadTo(740.0f, 778.0f, 719.0f, 799.0f)
                quadToRelative(-21.0f, 21.0f, -51.31f, 21.0f)
                lineTo(292.31f, 820.0f)
                close()
                moveTo(680.0f, 240.0f)
                lineTo(280.0f, 240.0f)
                verticalLineToRelative(507.69f)
                quadToRelative(0.0f, 5.39f, 3.46f, 8.85f)
                reflectiveQuadToRelative(8.85f, 3.46f)
                horizontalLineToRelative(375.38f)
                quadToRelative(4.62f, 0.0f, 8.46f, -3.85f)
                quadToRelative(3.85f, -3.84f, 3.85f, -8.46f)
                lineTo(680.0f, 240.0f)
                close()
                moveTo(376.16f, 680.0f)
                horizontalLineToRelative(59.99f)
                verticalLineToRelative(-360.0f)
                horizontalLineToRelative(-59.99f)
                verticalLineToRelative(360.0f)
                close()
                moveTo(523.85f, 680.0f)
                horizontalLineToRelative(59.99f)
                verticalLineToRelative(-360.0f)
                horizontalLineToRelative(-59.99f)
                verticalLineToRelative(360.0f)
                close()
                moveTo(280.0f, 240.0f)
                verticalLineToRelative(520.0f)
                verticalLineToRelative(-520.0f)
                close()
            }
        }
        .build()
        return _delete!!
    }

private var _delete: ImageVector? = null
