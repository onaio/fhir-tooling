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

public val AuroraIconPack.Save: ImageVector
    get() {
        if (_save != null) {
            return _save!!
        }
        _save = Builder(name = "Save", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(820.0f, 288.46f)
                verticalLineToRelative(459.23f)
                quadTo(820.0f, 778.0f, 799.0f, 799.0f)
                quadToRelative(-21.0f, 21.0f, -51.31f, 21.0f)
                lineTo(212.31f, 820.0f)
                quadTo(182.0f, 820.0f, 161.0f, 799.0f)
                quadToRelative(-21.0f, -21.0f, -21.0f, -51.31f)
                verticalLineToRelative(-535.38f)
                quadTo(140.0f, 182.0f, 161.0f, 161.0f)
                quadToRelative(21.0f, -21.0f, 51.31f, -21.0f)
                horizontalLineToRelative(459.23f)
                lineTo(820.0f, 288.46f)
                close()
                moveTo(760.0f, 314.0f)
                lineTo(646.0f, 200.0f)
                lineTo(212.31f, 200.0f)
                quadToRelative(-5.39f, 0.0f, -8.85f, 3.46f)
                reflectiveQuadToRelative(-3.46f, 8.85f)
                verticalLineToRelative(535.38f)
                quadToRelative(0.0f, 5.39f, 3.46f, 8.85f)
                reflectiveQuadToRelative(8.85f, 3.46f)
                horizontalLineToRelative(535.38f)
                quadToRelative(5.39f, 0.0f, 8.85f, -3.46f)
                reflectiveQuadToRelative(3.46f, -8.85f)
                lineTo(760.0f, 314.0f)
                close()
                moveTo(480.0f, 690.77f)
                quadToRelative(41.54f, 0.0f, 70.77f, -29.23f)
                quadTo(580.0f, 632.31f, 580.0f, 590.77f)
                quadToRelative(0.0f, -41.54f, -29.23f, -70.77f)
                quadToRelative(-29.23f, -29.23f, -70.77f, -29.23f)
                quadToRelative(-41.54f, 0.0f, -70.77f, 29.23f)
                quadTo(380.0f, 549.23f, 380.0f, 590.77f)
                quadToRelative(0.0f, 41.54f, 29.23f, 70.77f)
                quadToRelative(29.23f, 29.23f, 70.77f, 29.23f)
                close()
                moveTo(255.39f, 395.38f)
                horizontalLineToRelative(328.45f)
                verticalLineToRelative(-139.99f)
                lineTo(255.39f, 255.39f)
                verticalLineToRelative(139.99f)
                close()
                moveTo(200.0f, 314.0f)
                verticalLineToRelative(446.0f)
                verticalLineToRelative(-560.0f)
                verticalLineToRelative(114.0f)
                close()
            }
        }
        .build()
        return _save!!
    }

private var _save: ImageVector? = null
