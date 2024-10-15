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

public val AuroraIconPack.EditNote: ImageVector
    get() {
        if (_editNote != null) {
            return _editNote!!
        }
        _editNote = Builder(name = "EditNote", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(210.0f, 560.0f)
                quadToRelative(-12.75f, 0.0f, -21.37f, -8.63f)
                quadToRelative(-8.63f, -8.63f, -8.63f, -21.38f)
                quadToRelative(0.0f, -12.76f, 8.63f, -21.37f)
                quadTo(197.25f, 500.0f, 210.0f, 500.0f)
                horizontalLineToRelative(220.0f)
                quadToRelative(12.75f, 0.0f, 21.38f, 8.63f)
                quadToRelative(8.62f, 8.63f, 8.62f, 21.38f)
                quadToRelative(0.0f, 12.76f, -8.62f, 21.37f)
                quadTo(442.75f, 560.0f, 430.0f, 560.0f)
                lineTo(210.0f, 560.0f)
                close()
                moveTo(210.0f, 400.0f)
                quadToRelative(-12.75f, 0.0f, -21.37f, -8.63f)
                quadToRelative(-8.63f, -8.63f, -8.63f, -21.38f)
                quadToRelative(0.0f, -12.76f, 8.63f, -21.37f)
                quadTo(197.25f, 340.0f, 210.0f, 340.0f)
                horizontalLineToRelative(380.0f)
                quadToRelative(12.75f, 0.0f, 21.38f, 8.63f)
                quadToRelative(8.62f, 8.63f, 8.62f, 21.38f)
                quadToRelative(0.0f, 12.76f, -8.62f, 21.37f)
                quadTo(602.75f, 400.0f, 590.0f, 400.0f)
                lineTo(210.0f, 400.0f)
                close()
                moveTo(210.0f, 240.0f)
                quadToRelative(-12.75f, 0.0f, -21.37f, -8.63f)
                quadToRelative(-8.63f, -8.63f, -8.63f, -21.38f)
                quadToRelative(0.0f, -12.76f, 8.63f, -21.37f)
                quadTo(197.25f, 180.0f, 210.0f, 180.0f)
                horizontalLineToRelative(380.0f)
                quadToRelative(12.75f, 0.0f, 21.38f, 8.63f)
                quadToRelative(8.62f, 8.63f, 8.62f, 21.38f)
                quadToRelative(0.0f, 12.76f, -8.62f, 21.37f)
                quadTo(602.75f, 240.0f, 590.0f, 240.0f)
                lineTo(210.0f, 240.0f)
                close()
                moveTo(524.62f, 743.84f)
                verticalLineToRelative(-54.46f)
                quadToRelative(0.0f, -7.06f, 2.61f, -13.68f)
                quadToRelative(2.62f, -6.62f, 8.23f, -12.24f)
                lineToRelative(206.31f, -205.31f)
                quadToRelative(7.46f, -7.46f, 16.11f, -10.5f)
                quadToRelative(8.65f, -3.03f, 17.3f, -3.03f)
                quadToRelative(9.43f, 0.0f, 18.25f, 3.53f)
                quadToRelative(8.82f, 3.54f, 16.03f, 10.62f)
                lineToRelative(37.0f, 37.38f)
                quadToRelative(6.46f, 7.47f, 10.0f, 16.16f)
                quadTo(860.0f, 521.0f, 860.0f, 529.69f)
                reflectiveQuadToRelative(-3.23f, 17.69f)
                quadToRelative(-3.23f, 9.0f, -10.31f, 16.46f)
                lineTo(641.15f, 769.15f)
                quadToRelative(-5.61f, 5.62f, -12.23f, 8.23f)
                quadToRelative(-6.63f, 2.62f, -13.69f, 2.62f)
                horizontalLineToRelative(-54.46f)
                quadToRelative(-15.37f, 0.0f, -25.76f, -10.4f)
                quadToRelative(-10.39f, -10.39f, -10.39f, -25.76f)
                close()
                moveTo(812.31f, 529.69f)
                lineTo(775.31f, 492.31f)
                lineTo(812.31f, 529.69f)
                close()
                moveTo(572.31f, 732.31f)
                horizontalLineToRelative(38.0f)
                lineToRelative(129.84f, -130.47f)
                lineToRelative(-18.38f, -19.0f)
                lineToRelative(-18.62f, -18.76f)
                lineToRelative(-130.84f, 130.23f)
                verticalLineToRelative(38.0f)
                close()
                moveTo(721.77f, 582.84f)
                lineTo(703.15f, 564.08f)
                lineTo(740.15f, 601.84f)
                lineTo(721.77f, 582.84f)
                close()
            }
        }
        .build()
        return _editNote!!
    }

private var _editNote: ImageVector? = null
