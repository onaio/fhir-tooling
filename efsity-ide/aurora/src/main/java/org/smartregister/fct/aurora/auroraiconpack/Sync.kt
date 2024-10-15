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

public val AuroraIconPack.Sync: ImageVector
    get() {
        if (_sync != null) {
            return _sync!!
        }
        _sync = Builder(name = "Sync", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(173.08f, 786.92f)
                verticalLineToRelative(-60.0f)
                lineTo(290.0f, 726.92f)
                lineToRelative(-29.08f, -27.84f)
                quadToRelative(-49.3f, -45.62f, -70.11f, -102.12f)
                quadTo(170.0f, 540.46f, 170.0f, 482.77f)
                quadToRelative(0.0f, -103.69f, 60.73f, -185.96f)
                quadTo(291.46f, 214.54f, 390.0f, 183.85f)
                verticalLineToRelative(63.23f)
                quadToRelative(-72.39f, 28.3f, -116.19f, 92.73f)
                quadTo(230.0f, 404.23f, 230.0f, 482.77f)
                quadToRelative(0.0f, 46.92f, 17.77f, 91.15f)
                quadToRelative(17.77f, 44.23f, 55.31f, 81.77f)
                lineToRelative(25.38f, 25.39f)
                verticalLineToRelative(-109.54f)
                horizontalLineToRelative(60.0f)
                verticalLineToRelative(215.38f)
                lineTo(173.08f, 786.92f)
                close()
                moveTo(570.0f, 776.15f)
                verticalLineToRelative(-63.23f)
                quadToRelative(72.39f, -28.3f, 116.19f, -92.73f)
                quadTo(730.0f, 555.77f, 730.0f, 477.23f)
                quadToRelative(0.0f, -46.92f, -17.77f, -91.15f)
                quadToRelative(-17.77f, -44.23f, -55.31f, -81.77f)
                lineToRelative(-25.38f, -25.39f)
                verticalLineToRelative(109.54f)
                horizontalLineToRelative(-60.0f)
                verticalLineToRelative(-215.38f)
                horizontalLineToRelative(215.38f)
                verticalLineToRelative(60.0f)
                lineTo(670.0f, 233.08f)
                lineToRelative(29.08f, 27.84f)
                quadToRelative(47.46f, 47.47f, 69.19f, 103.04f)
                quadTo(790.0f, 419.54f, 790.0f, 477.23f)
                quadToRelative(0.0f, 103.69f, -60.73f, 185.96f)
                quadTo(668.54f, 745.46f, 570.0f, 776.15f)
                close()
            }
        }
        .build()
        return _sync!!
    }

private var _sync: ImageVector? = null
