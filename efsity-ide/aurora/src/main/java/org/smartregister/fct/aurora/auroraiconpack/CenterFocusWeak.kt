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

public val AuroraIconPack.CenterFocusWeak: ImageVector
    get() {
        if (_centerFocusWeak != null) {
            return _centerFocusWeak!!
        }
        _centerFocusWeak = Builder(name = "CenterFocusWeak", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(480.0f, 620.0f)
                quadToRelative(-57.75f, 0.0f, -98.87f, -41.13f)
                quadTo(340.0f, 537.75f, 340.0f, 480.0f)
                quadToRelative(0.0f, -57.75f, 41.13f, -98.87f)
                quadTo(422.25f, 340.0f, 480.0f, 340.0f)
                quadToRelative(57.75f, 0.0f, 98.87f, 41.13f)
                quadTo(620.0f, 422.25f, 620.0f, 480.0f)
                quadToRelative(0.0f, 57.75f, -41.13f, 98.87f)
                quadTo(537.75f, 620.0f, 480.0f, 620.0f)
                close()
                moveTo(480.0f, 560.0f)
                quadToRelative(33.0f, 0.0f, 56.5f, -23.5f)
                reflectiveQuadTo(560.0f, 480.0f)
                quadToRelative(0.0f, -33.0f, -23.5f, -56.5f)
                reflectiveQuadTo(480.0f, 400.0f)
                quadToRelative(-33.0f, 0.0f, -56.5f, 23.5f)
                reflectiveQuadTo(400.0f, 480.0f)
                quadToRelative(0.0f, 33.0f, 23.5f, 56.5f)
                reflectiveQuadTo(480.0f, 560.0f)
                close()
                moveTo(480.0f, 480.0f)
                close()
                moveTo(212.44f, 820.0f)
                quadTo(182.0f, 820.0f, 161.0f, 799.0f)
                quadToRelative(-21.0f, -21.0f, -21.0f, -51.44f)
                lineTo(140.0f, 600.0f)
                horizontalLineToRelative(60.0f)
                verticalLineToRelative(147.69f)
                quadToRelative(0.0f, 4.62f, 3.85f, 8.46f)
                quadToRelative(3.84f, 3.85f, 8.46f, 3.85f)
                lineTo(360.0f, 760.0f)
                verticalLineToRelative(60.0f)
                lineTo(212.44f, 820.0f)
                close()
                moveTo(600.0f, 820.0f)
                verticalLineToRelative(-60.0f)
                horizontalLineToRelative(147.69f)
                quadToRelative(4.62f, 0.0f, 8.46f, -3.85f)
                quadToRelative(3.85f, -3.84f, 3.85f, -8.46f)
                lineTo(760.0f, 600.0f)
                horizontalLineToRelative(60.0f)
                verticalLineToRelative(147.56f)
                quadTo(820.0f, 778.0f, 799.0f, 799.0f)
                quadToRelative(-21.0f, 21.0f, -51.44f, 21.0f)
                lineTo(600.0f, 820.0f)
                close()
                moveTo(140.0f, 360.0f)
                verticalLineToRelative(-147.56f)
                quadTo(140.0f, 182.0f, 161.0f, 161.0f)
                quadToRelative(21.0f, -21.0f, 51.44f, -21.0f)
                lineTo(360.0f, 140.0f)
                verticalLineToRelative(60.0f)
                lineTo(212.31f, 200.0f)
                quadToRelative(-4.62f, 0.0f, -8.46f, 3.85f)
                quadToRelative(-3.85f, 3.84f, -3.85f, 8.46f)
                lineTo(200.0f, 360.0f)
                horizontalLineToRelative(-60.0f)
                close()
                moveTo(760.0f, 360.0f)
                verticalLineToRelative(-147.69f)
                quadToRelative(0.0f, -4.62f, -3.85f, -8.46f)
                quadToRelative(-3.84f, -3.85f, -8.46f, -3.85f)
                lineTo(600.0f, 200.0f)
                verticalLineToRelative(-60.0f)
                horizontalLineToRelative(147.56f)
                quadTo(778.0f, 140.0f, 799.0f, 161.0f)
                quadToRelative(21.0f, 21.0f, 21.0f, 51.44f)
                lineTo(820.0f, 360.0f)
                horizontalLineToRelative(-60.0f)
                close()
            }
        }
        .build()
        return _centerFocusWeak!!
    }

private var _centerFocusWeak: ImageVector? = null
