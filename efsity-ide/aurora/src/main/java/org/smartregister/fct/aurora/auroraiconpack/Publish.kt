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

public val AuroraIconPack.Publish: ImageVector
    get() {
        if (_publish != null) {
            return _publish!!
        }
        _publish = Builder(name = "Publish", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(450.0f, 780.0f)
                verticalLineToRelative(-336.0f)
                lineToRelative(-98.61f, 98.61f)
                lineToRelative(-42.16f, -43.38f)
                lineTo(480.0f, 328.46f)
                lineToRelative(170.77f, 170.77f)
                lineToRelative(-42.16f, 43.38f)
                lineTo(510.0f, 444.0f)
                verticalLineToRelative(336.0f)
                horizontalLineToRelative(-60.0f)
                close()
                moveTo(180.0f, 360.77f)
                verticalLineToRelative(-108.46f)
                quadTo(180.0f, 222.0f, 201.0f, 201.0f)
                quadToRelative(21.0f, -21.0f, 51.31f, -21.0f)
                horizontalLineToRelative(455.38f)
                quadTo(738.0f, 180.0f, 759.0f, 201.0f)
                quadToRelative(21.0f, 21.0f, 21.0f, 51.31f)
                verticalLineToRelative(108.46f)
                horizontalLineToRelative(-60.0f)
                verticalLineToRelative(-108.46f)
                quadToRelative(0.0f, -4.62f, -3.85f, -8.46f)
                quadToRelative(-3.84f, -3.85f, -8.46f, -3.85f)
                lineTo(252.31f, 240.0f)
                quadToRelative(-4.62f, 0.0f, -8.46f, 3.85f)
                quadToRelative(-3.85f, 3.84f, -3.85f, 8.46f)
                verticalLineToRelative(108.46f)
                horizontalLineToRelative(-60.0f)
                close()
            }
        }
        .build()
        return _publish!!
    }

private var _publish: ImageVector? = null
