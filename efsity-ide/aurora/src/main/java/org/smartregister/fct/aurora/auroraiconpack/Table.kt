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

public val AuroraIconPack.Table: ImageVector
    get() {
        if (_table != null) {
            return _table!!
        }
        _table = Builder(name = "Table", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(140.0f, 747.69f)
                verticalLineToRelative(-535.38f)
                quadToRelative(0.0f, -29.83f, 21.24f, -51.07f)
                quadTo(182.48f, 140.0f, 212.31f, 140.0f)
                horizontalLineToRelative(535.38f)
                quadToRelative(29.83f, 0.0f, 51.07f, 21.24f)
                quadTo(820.0f, 182.48f, 820.0f, 212.31f)
                verticalLineToRelative(535.38f)
                quadToRelative(0.0f, 29.83f, -21.24f, 51.07f)
                quadTo(777.52f, 820.0f, 747.69f, 820.0f)
                lineTo(212.31f, 820.0f)
                quadToRelative(-29.83f, 0.0f, -51.07f, -21.24f)
                quadTo(140.0f, 777.52f, 140.0f, 747.69f)
                close()
                moveTo(200.0f, 363.08f)
                horizontalLineToRelative(560.0f)
                verticalLineToRelative(-150.77f)
                quadToRelative(0.0f, -5.39f, -3.46f, -8.85f)
                reflectiveQuadToRelative(-8.85f, -3.46f)
                lineTo(212.31f, 200.0f)
                quadToRelative(-5.39f, 0.0f, -8.85f, 3.46f)
                reflectiveQuadToRelative(-3.46f, 8.85f)
                verticalLineToRelative(150.77f)
                close()
                moveTo(406.46f, 561.54f)
                horizontalLineToRelative(147.08f)
                verticalLineToRelative(-138.46f)
                lineTo(406.46f, 423.08f)
                verticalLineToRelative(138.46f)
                close()
                moveTo(406.46f, 760.0f)
                horizontalLineToRelative(147.08f)
                verticalLineToRelative(-138.46f)
                lineTo(406.46f, 621.54f)
                lineTo(406.46f, 760.0f)
                close()
                moveTo(200.0f, 561.54f)
                horizontalLineToRelative(146.46f)
                verticalLineToRelative(-138.46f)
                lineTo(200.0f, 423.08f)
                verticalLineToRelative(138.46f)
                close()
                moveTo(613.54f, 561.54f)
                lineTo(760.0f, 561.54f)
                verticalLineToRelative(-138.46f)
                lineTo(613.54f, 423.08f)
                verticalLineToRelative(138.46f)
                close()
                moveTo(212.31f, 760.0f)
                horizontalLineToRelative(134.15f)
                verticalLineToRelative(-138.46f)
                lineTo(200.0f, 621.54f)
                verticalLineToRelative(126.15f)
                quadToRelative(0.0f, 5.39f, 3.46f, 8.85f)
                reflectiveQuadToRelative(8.85f, 3.46f)
                close()
                moveTo(613.54f, 760.0f)
                horizontalLineToRelative(134.15f)
                quadToRelative(5.39f, 0.0f, 8.85f, -3.46f)
                reflectiveQuadToRelative(3.46f, -8.85f)
                verticalLineToRelative(-126.15f)
                lineTo(613.54f, 621.54f)
                lineTo(613.54f, 760.0f)
                close()
            }
        }
        .build()
        return _table!!
    }

private var _table: ImageVector? = null
