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

public val AuroraIconPack.OpenInNew: ImageVector
    get() {
        if (_openInNew != null) {
            return _openInNew!!
        }
        _openInNew = Builder(name = "OpenInNew", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(212.31f, 820.0f)
                quadTo(182.0f, 820.0f, 161.0f, 799.0f)
                quadToRelative(-21.0f, -21.0f, -21.0f, -51.31f)
                verticalLineToRelative(-535.38f)
                quadTo(140.0f, 182.0f, 161.0f, 161.0f)
                quadToRelative(21.0f, -21.0f, 51.31f, -21.0f)
                horizontalLineToRelative(252.3f)
                verticalLineToRelative(60.0f)
                horizontalLineToRelative(-252.3f)
                quadToRelative(-4.62f, 0.0f, -8.46f, 3.85f)
                quadToRelative(-3.85f, 3.84f, -3.85f, 8.46f)
                verticalLineToRelative(535.38f)
                quadToRelative(0.0f, 4.62f, 3.85f, 8.46f)
                quadToRelative(3.84f, 3.85f, 8.46f, 3.85f)
                horizontalLineToRelative(535.38f)
                quadToRelative(4.62f, 0.0f, 8.46f, -3.85f)
                quadToRelative(3.85f, -3.84f, 3.85f, -8.46f)
                verticalLineToRelative(-252.3f)
                horizontalLineToRelative(60.0f)
                verticalLineToRelative(252.3f)
                quadTo(820.0f, 778.0f, 799.0f, 799.0f)
                quadToRelative(-21.0f, 21.0f, -51.31f, 21.0f)
                lineTo(212.31f, 820.0f)
                close()
                moveTo(388.77f, 613.38f)
                lineTo(346.62f, 571.23f)
                lineTo(717.85f, 200.0f)
                lineTo(560.0f, 200.0f)
                verticalLineToRelative(-60.0f)
                horizontalLineToRelative(260.0f)
                verticalLineToRelative(260.0f)
                horizontalLineToRelative(-60.0f)
                verticalLineToRelative(-157.85f)
                lineTo(388.77f, 613.38f)
                close()
            }
        }
        .build()
        return _openInNew!!
    }

private var _openInNew: ImageVector? = null
