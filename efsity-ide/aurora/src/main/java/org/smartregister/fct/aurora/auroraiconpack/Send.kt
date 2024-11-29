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

public val AuroraIconPack.Send: ImageVector
    get() {
        if (_send != null) {
            return _send!!
        }
        _send = Builder(name = "Send", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(140.0f, 770.0f)
                verticalLineToRelative(-580.0f)
                lineToRelative(688.46f, 290.0f)
                lineTo(140.0f, 770.0f)
                close()
                moveTo(200.0f, 680.0f)
                lineTo(674.0f, 480.0f)
                lineTo(200.0f, 280.0f)
                verticalLineToRelative(147.69f)
                lineTo(416.92f, 480.0f)
                lineTo(200.0f, 532.31f)
                lineTo(200.0f, 680.0f)
                close()
                moveTo(200.0f, 680.0f)
                verticalLineToRelative(-400.0f)
                verticalLineToRelative(400.0f)
                close()
            }
        }
        .build()
        return _send!!
    }

private var _send: ImageVector? = null
