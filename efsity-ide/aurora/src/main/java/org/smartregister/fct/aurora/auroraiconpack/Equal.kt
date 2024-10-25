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

public val AuroraIconPack.Equal: ImageVector
    get() {
        if (_equal != null) {
            return _equal!!
        }
        _equal = Builder(name = "Equal", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(200.0f, 640.0f)
                verticalLineToRelative(-80.0f)
                horizontalLineToRelative(560.0f)
                verticalLineToRelative(80.0f)
                lineTo(200.0f, 640.0f)
                close()
                moveTo(200.0f, 400.0f)
                verticalLineToRelative(-80.0f)
                horizontalLineToRelative(560.0f)
                verticalLineToRelative(80.0f)
                lineTo(200.0f, 400.0f)
                close()
            }
        }
        .build()
        return _equal!!
    }

private var _equal: ImageVector? = null
