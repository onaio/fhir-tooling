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

public val AuroraIconPack.VerticalAlignBottom: ImageVector
    get() {
        if (_verticalAlignBottom != null) {
            return _verticalAlignBottom!!
        }
        _verticalAlignBottom = Builder(name = "VerticalAlignBottom", defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(180.0f, 820.0f)
                verticalLineToRelative(-60.0f)
                horizontalLineToRelative(600.0f)
                verticalLineToRelative(60.0f)
                lineTo(180.0f, 820.0f)
                close()
                moveTo(480.0f, 666.15f)
                lineTo(293.85f, 480.0f)
                lineTo(336.0f, 437.85f)
                lineToRelative(114.0f, 114.0f)
                lineTo(450.0f, 140.0f)
                horizontalLineToRelative(60.0f)
                verticalLineToRelative(411.85f)
                lineToRelative(114.0f, -114.0f)
                lineTo(666.15f, 480.0f)
                lineTo(480.0f, 666.15f)
                close()
            }
        }
        .build()
        return _verticalAlignBottom!!
    }

private var _verticalAlignBottom: ImageVector? = null
