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

public val AuroraIconPack.KeyboardArrowUp: ImageVector
    get() {
        if (_keyboardArrowUp != null) {
            return _keyboardArrowUp!!
        }
        _keyboardArrowUp = Builder(name = "KeyboardArrowUp", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(480.0f, 418.15f)
                lineToRelative(-184.0f, 184.0f)
                lineTo(253.85f, 560.0f)
                lineTo(480.0f, 333.85f)
                lineTo(706.15f, 560.0f)
                lineTo(664.0f, 602.15f)
                lineToRelative(-184.0f, -184.0f)
                close()
            }
        }
        .build()
        return _keyboardArrowUp!!
    }

private var _keyboardArrowUp: ImageVector? = null
