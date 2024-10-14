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

public val AuroraIconPack.Pause: ImageVector
    get() {
        if (_pause != null) {
            return _pause!!
        }
        _pause = Builder(name = "Pause", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(620.0f, 740.0f)
                quadToRelative(-24.54f, 0.0f, -42.27f, -17.73f)
                quadTo(560.0f, 704.54f, 560.0f, 680.0f)
                verticalLineToRelative(-400.0f)
                quadToRelative(0.0f, -24.54f, 17.73f, -42.27f)
                quadTo(595.46f, 220.0f, 620.0f, 220.0f)
                horizontalLineToRelative(30.0f)
                quadToRelative(24.54f, 0.0f, 42.27f, 17.73f)
                quadTo(710.0f, 255.46f, 710.0f, 280.0f)
                verticalLineToRelative(400.0f)
                quadToRelative(0.0f, 24.54f, -17.73f, 42.27f)
                quadTo(674.54f, 740.0f, 650.0f, 740.0f)
                horizontalLineToRelative(-30.0f)
                close()
                moveTo(310.0f, 740.0f)
                quadToRelative(-24.54f, 0.0f, -42.27f, -17.73f)
                quadTo(250.0f, 704.54f, 250.0f, 680.0f)
                verticalLineToRelative(-400.0f)
                quadToRelative(0.0f, -24.54f, 17.73f, -42.27f)
                quadTo(285.46f, 220.0f, 310.0f, 220.0f)
                horizontalLineToRelative(30.0f)
                quadToRelative(24.54f, 0.0f, 42.27f, 17.73f)
                quadTo(400.0f, 255.46f, 400.0f, 280.0f)
                verticalLineToRelative(400.0f)
                quadToRelative(0.0f, 24.54f, -17.73f, 42.27f)
                quadTo(364.54f, 740.0f, 340.0f, 740.0f)
                horizontalLineToRelative(-30.0f)
                close()
            }
        }
        .build()
        return _pause!!
    }

private var _pause: ImageVector? = null
