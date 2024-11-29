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

public val AuroraIconPack.PhoneIphone: ImageVector
    get() {
        if (_phoneIphone != null) {
            return _phoneIphone!!
        }
        _phoneIphone = Builder(name = "PhoneIphone", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(292.31f, 900.0f)
                quadTo(262.0f, 900.0f, 241.0f, 879.0f)
                quadToRelative(-21.0f, -21.0f, -21.0f, -51.31f)
                verticalLineToRelative(-695.38f)
                quadTo(220.0f, 102.0f, 241.0f, 81.0f)
                quadToRelative(21.0f, -21.0f, 51.31f, -21.0f)
                horizontalLineToRelative(375.38f)
                quadTo(698.0f, 60.0f, 719.0f, 81.0f)
                quadToRelative(21.0f, 21.0f, 21.0f, 51.31f)
                verticalLineToRelative(695.38f)
                quadTo(740.0f, 858.0f, 719.0f, 879.0f)
                quadToRelative(-21.0f, 21.0f, -51.31f, 21.0f)
                lineTo(292.31f, 900.0f)
                close()
                moveTo(280.0f, 710.77f)
                verticalLineToRelative(116.92f)
                quadToRelative(0.0f, 4.62f, 3.85f, 8.46f)
                quadToRelative(3.84f, 3.85f, 8.46f, 3.85f)
                horizontalLineToRelative(375.38f)
                quadToRelative(4.62f, 0.0f, 8.46f, -3.85f)
                quadToRelative(3.85f, -3.84f, 3.85f, -8.46f)
                verticalLineToRelative(-116.92f)
                lineTo(280.0f, 710.77f)
                close()
                moveTo(480.0f, 810.77f)
                quadToRelative(14.69f, 0.0f, 25.04f, -10.35f)
                quadToRelative(10.34f, -10.34f, 10.34f, -25.04f)
                quadToRelative(0.0f, -14.69f, -10.34f, -25.03f)
                quadTo(494.69f, 740.0f, 480.0f, 740.0f)
                reflectiveQuadToRelative(-25.04f, 10.35f)
                quadToRelative(-10.34f, 10.34f, -10.34f, 25.03f)
                quadToRelative(0.0f, 14.7f, 10.34f, 25.04f)
                quadToRelative(10.35f, 10.35f, 25.04f, 10.35f)
                close()
                moveTo(280.0f, 650.77f)
                horizontalLineToRelative(400.0f)
                lineTo(680.0f, 230.0f)
                lineTo(280.0f, 230.0f)
                verticalLineToRelative(420.77f)
                close()
                moveTo(280.0f, 170.0f)
                horizontalLineToRelative(400.0f)
                verticalLineToRelative(-37.69f)
                quadToRelative(0.0f, -4.62f, -3.85f, -8.46f)
                quadToRelative(-3.84f, -3.85f, -8.46f, -3.85f)
                lineTo(292.31f, 120.0f)
                quadToRelative(-4.62f, 0.0f, -8.46f, 3.85f)
                quadToRelative(-3.85f, 3.84f, -3.85f, 8.46f)
                lineTo(280.0f, 170.0f)
                close()
                moveTo(280.0f, 710.77f)
                lineTo(280.0f, 840.0f)
                verticalLineToRelative(-129.23f)
                close()
                moveTo(280.0f, 170.0f)
                verticalLineToRelative(-50.0f)
                verticalLineToRelative(50.0f)
                close()
            }
        }
        .build()
        return _phoneIphone!!
    }

private var _phoneIphone: ImageVector? = null
