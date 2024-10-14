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

public val AuroraIconPack.MoveDown: ImageVector
    get() {
        if (_moveDown != null) {
            return _moveDown!!
        }
        _moveDown = Builder(name = "MoveDown", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(124.62f, 440.0f)
                quadToRelative(0.0f, 78.15f, 55.8f, 133.35f)
                quadToRelative(55.81f, 55.19f, 135.66f, 64.57f)
                lineToRelative(-49.85f, -49.84f)
                quadToRelative(-8.31f, -8.31f, -8.5f, -20.58f)
                quadToRelative(-0.19f, -12.27f, 8.42f, -21.58f)
                quadToRelative(9.39f, -9.3f, 21.47f, -9.3f)
                quadToRelative(12.07f, 0.0f, 21.38f, 9.3f)
                lineToRelative(98.77f, 98.77f)
                quadToRelative(10.84f, 10.85f, 10.84f, 25.31f)
                quadToRelative(0.0f, 14.46f, -10.84f, 25.31f)
                lineToRelative(-99.39f, 99.38f)
                quadToRelative(-8.3f, 8.31f, -20.88f, 8.5f)
                quadToRelative(-12.58f, 0.19f, -21.97f, -9.2f)
                quadToRelative(-8.61f, -8.61f, -8.61f, -20.38f)
                quadToRelative(0.0f, -11.76f, 8.7f, -21.07f)
                lineToRelative(52.15f, -53.39f)
                quadToRelative(-105.61f, -8.54f, -179.38f, -81.69f)
                reflectiveQuadTo(64.62f, 440.0f)
                quadToRelative(0.0f, -109.31f, 77.27f, -184.65f)
                quadTo(219.16f, 180.0f, 328.85f, 180.0f)
                lineTo(400.0f, 180.0f)
                quadToRelative(12.75f, 0.0f, 21.37f, 8.63f)
                quadToRelative(8.63f, 8.63f, 8.63f, 21.38f)
                quadToRelative(0.0f, 12.76f, -8.63f, 21.37f)
                quadTo(412.75f, 240.0f, 400.0f, 240.0f)
                horizontalLineToRelative(-70.77f)
                quadToRelative(-84.54f, 0.0f, -144.58f, 58.12f)
                quadToRelative(-60.03f, 58.11f, -60.03f, 141.88f)
                close()
                moveTo(566.33f, 780.0f)
                quadToRelative(-15.64f, 0.0f, -25.98f, -10.4f)
                quadTo(530.0f, 759.21f, 530.0f, 743.84f)
                verticalLineToRelative(-183.69f)
                quadToRelative(0.0f, -15.61f, 10.39f, -25.96f)
                quadToRelative(10.4f, -10.34f, 25.76f, -10.34f)
                horizontalLineToRelative(257.52f)
                quadToRelative(15.64f, 0.0f, 25.98f, 10.39f)
                quadTo(860.0f, 544.63f, 860.0f, 560.0f)
                verticalLineToRelative(183.69f)
                quadToRelative(0.0f, 15.62f, -10.4f, 25.96f)
                quadTo(839.21f, 780.0f, 823.84f, 780.0f)
                lineTo(566.33f, 780.0f)
                close()
                moveTo(566.33f, 436.15f)
                quadToRelative(-15.64f, 0.0f, -25.98f, -10.39f)
                quadTo(530.0f, 415.37f, 530.0f, 400.0f)
                verticalLineToRelative(-183.69f)
                quadToRelative(0.0f, -15.62f, 10.39f, -25.96f)
                quadTo(550.79f, 180.0f, 566.15f, 180.0f)
                horizontalLineToRelative(257.52f)
                quadToRelative(15.64f, 0.0f, 25.98f, 10.4f)
                quadTo(860.0f, 200.79f, 860.0f, 216.16f)
                verticalLineToRelative(183.69f)
                quadToRelative(0.0f, 15.61f, -10.4f, 25.96f)
                quadToRelative(-10.39f, 10.34f, -25.76f, 10.34f)
                lineTo(566.33f, 436.15f)
                close()
                moveTo(590.0f, 376.16f)
                horizontalLineToRelative(210.0f)
                lineTo(800.0f, 240.0f)
                lineTo(590.0f, 240.0f)
                verticalLineToRelative(136.16f)
                close()
            }
        }
        .build()
        return _moveDown!!
    }

private var _moveDown: ImageVector? = null
