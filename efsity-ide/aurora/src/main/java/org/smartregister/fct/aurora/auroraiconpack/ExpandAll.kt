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

public val AuroraIconPack.ExpandAll: ImageVector
    get() {
        if (_expandAll != null) {
            return _expandAll!!
        }
        _expandAll = Builder(name = "ExpandAll", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(480.0f, 866.15f)
                lineTo(253.85f, 640.0f)
                lineToRelative(42.77f, -42.77f)
                lineTo(480.0f, 779.85f)
                lineToRelative(183.38f, -182.62f)
                lineTo(706.15f, 640.0f)
                lineTo(480.0f, 866.15f)
                close()
                moveTo(297.23f, 362.15f)
                lineTo(253.85f, 320.0f)
                lineTo(480.0f, 93.85f)
                lineTo(706.15f, 320.0f)
                lineToRelative(-43.38f, 42.15f)
                lineToRelative(-182.77f, -182.0f)
                lineToRelative(-182.77f, 182.0f)
                close()
            }
        }
        .build()
        return _expandAll!!
    }

private var _expandAll: ImageVector? = null
