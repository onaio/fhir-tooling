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

public val AuroraIconPack.PhoneAndroid: ImageVector
    get() {
        if (_phoneAndroid != null) {
            return _phoneAndroid!!
        }
        _phoneAndroid = Builder(name = "PhoneAndroid", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(424.62f, 786.15f)
                horizontalLineToRelative(110.76f)
                quadToRelative(6.47f, 0.0f, 10.93f, -4.46f)
                reflectiveQuadToRelative(4.46f, -10.92f)
                quadToRelative(0.0f, -6.46f, -4.46f, -10.92f)
                quadToRelative(-4.46f, -4.47f, -10.93f, -4.47f)
                lineTo(424.62f, 755.38f)
                quadToRelative(-6.47f, 0.0f, -10.93f, 4.47f)
                quadToRelative(-4.46f, 4.46f, -4.46f, 10.92f)
                reflectiveQuadToRelative(4.46f, 10.92f)
                quadToRelative(4.46f, 4.46f, 10.93f, 4.46f)
                close()
                moveTo(304.62f, 880.0f)
                quadTo(277.0f, 880.0f, 258.5f, 861.5f)
                quadTo(240.0f, 843.0f, 240.0f, 815.38f)
                verticalLineToRelative(-670.76f)
                quadToRelative(0.0f, -27.62f, 18.5f, -46.12f)
                quadTo(277.0f, 80.0f, 304.62f, 80.0f)
                horizontalLineToRelative(350.76f)
                quadToRelative(27.62f, 0.0f, 46.12f, 18.5f)
                quadTo(720.0f, 117.0f, 720.0f, 144.62f)
                verticalLineToRelative(670.76f)
                quadToRelative(0.0f, 27.62f, -18.5f, 46.12f)
                quadTo(683.0f, 880.0f, 655.38f, 880.0f)
                lineTo(304.62f, 880.0f)
                close()
                moveTo(280.0f, 701.54f)
                verticalLineToRelative(113.84f)
                quadToRelative(0.0f, 9.24f, 7.69f, 16.93f)
                quadToRelative(7.69f, 7.69f, 16.93f, 7.69f)
                horizontalLineToRelative(350.76f)
                quadToRelative(9.24f, 0.0f, 16.93f, -7.69f)
                quadToRelative(7.69f, -7.69f, 7.69f, -16.93f)
                verticalLineToRelative(-113.84f)
                lineTo(280.0f, 701.54f)
                close()
                moveTo(280.0f, 661.54f)
                horizontalLineToRelative(400.0f)
                lineTo(680.0f, 220.0f)
                lineTo(280.0f, 220.0f)
                verticalLineToRelative(441.54f)
                close()
                moveTo(280.0f, 180.0f)
                horizontalLineToRelative(400.0f)
                verticalLineToRelative(-35.38f)
                quadToRelative(0.0f, -9.24f, -7.69f, -16.93f)
                quadToRelative(-7.69f, -7.69f, -16.93f, -7.69f)
                lineTo(304.62f, 120.0f)
                quadToRelative(-9.24f, 0.0f, -16.93f, 7.69f)
                quadToRelative(-7.69f, 7.69f, -7.69f, 16.93f)
                lineTo(280.0f, 180.0f)
                close()
                moveTo(280.0f, 701.54f)
                lineTo(280.0f, 840.0f)
                verticalLineToRelative(-138.46f)
                close()
                moveTo(280.0f, 180.0f)
                verticalLineToRelative(-60.0f)
                verticalLineToRelative(60.0f)
                close()
            }
        }
        .build()
        return _phoneAndroid!!
    }

private var _phoneAndroid: ImageVector? = null
