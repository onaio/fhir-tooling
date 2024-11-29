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

public val AuroraIconPack.Add: ImageVector
    get() {
        if (_add != null) {
            return _add!!
        }
        _add = Builder(name = "Add", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth
                = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(450.0f, 510.0f)
                lineTo(220.0f, 510.0f)
                verticalLineToRelative(-60.0f)
                horizontalLineToRelative(230.0f)
                verticalLineToRelative(-230.0f)
                horizontalLineToRelative(60.0f)
                verticalLineToRelative(230.0f)
                horizontalLineToRelative(230.0f)
                verticalLineToRelative(60.0f)
                lineTo(510.0f, 510.0f)
                verticalLineToRelative(230.0f)
                horizontalLineToRelative(-60.0f)
                verticalLineToRelative(-230.0f)
                close()
            }
        }
        .build()
        return _add!!
    }

private var _add: ImageVector? = null
