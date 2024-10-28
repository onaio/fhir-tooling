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

public val AuroraIconPack.FolderOpen: ImageVector
    get() {
        if (_folderOpen != null) {
            return _folderOpen!!
        }
        _folderOpen = Builder(name = "FolderOpen", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(170.0f, 780.0f)
                quadToRelative(-29.15f, 0.0f, -49.58f, -20.42f)
                quadTo(100.0f, 739.15f, 100.0f, 710.0f)
                verticalLineToRelative(-457.69f)
                quadToRelative(0.0f, -29.15f, 21.58f, -50.73f)
                reflectiveQuadTo(172.31f, 180.0f)
                horizontalLineToRelative(219.61f)
                lineToRelative(80.0f, 80.0f)
                horizontalLineToRelative(315.77f)
                quadToRelative(26.85f, 0.0f, 46.31f, 17.35f)
                quadToRelative(19.46f, 17.34f, 22.54f, 42.65f)
                lineTo(447.38f, 320.0f)
                lineToRelative(-80.0f, -80.0f)
                lineTo(172.31f, 240.0f)
                quadToRelative(-5.39f, 0.0f, -8.85f, 3.46f)
                reflectiveQuadToRelative(-3.46f, 8.85f)
                verticalLineToRelative(455.38f)
                quadToRelative(0.0f, 4.23f, 2.12f, 6.92f)
                quadToRelative(2.11f, 2.7f, 5.57f, 4.62f)
                lineTo(261.0f, 407.69f)
                horizontalLineToRelative(666.31f)
                lineToRelative(-96.85f, 322.62f)
                quadToRelative(-6.85f, 22.53f, -25.65f, 36.11f)
                quadTo(786.0f, 780.0f, 763.08f, 780.0f)
                lineTo(170.0f, 780.0f)
                close()
                moveTo(230.54f, 720.0f)
                horizontalLineToRelative(540.23f)
                lineToRelative(75.46f, -252.31f)
                lineTo(306.0f, 467.69f)
                lineTo(230.54f, 720.0f)
                close()
                moveTo(230.54f, 720.0f)
                lineTo(306.0f, 467.69f)
                lineTo(230.54f, 720.0f)
                close()
                moveTo(160.0f, 320.0f)
                lineTo(160.0f, 240.0f)
                verticalLineToRelative(80.0f)
                close()
            }
        }
        .build()
        return _folderOpen!!
    }

private var _folderOpen: ImageVector? = null
