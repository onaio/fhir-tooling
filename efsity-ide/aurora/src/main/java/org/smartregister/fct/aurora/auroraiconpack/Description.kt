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

public val AuroraIconPack.Description: ImageVector
    get() {
        if (_description != null) {
            return _description!!
        }
        _description = Builder(name = "Description", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(330.0f, 710.0f)
                horizontalLineToRelative(300.0f)
                verticalLineToRelative(-60.0f)
                lineTo(330.0f, 650.0f)
                verticalLineToRelative(60.0f)
                close()
                moveTo(330.0f, 550.0f)
                horizontalLineToRelative(300.0f)
                verticalLineToRelative(-60.0f)
                lineTo(330.0f, 490.0f)
                verticalLineToRelative(60.0f)
                close()
                moveTo(252.31f, 860.0f)
                quadTo(222.0f, 860.0f, 201.0f, 839.0f)
                quadToRelative(-21.0f, -21.0f, -21.0f, -51.31f)
                verticalLineToRelative(-615.38f)
                quadTo(180.0f, 142.0f, 201.0f, 121.0f)
                quadToRelative(21.0f, -21.0f, 51.31f, -21.0f)
                lineTo(570.0f, 100.0f)
                lineToRelative(210.0f, 210.0f)
                verticalLineToRelative(477.69f)
                quadTo(780.0f, 818.0f, 759.0f, 839.0f)
                quadToRelative(-21.0f, 21.0f, -51.31f, 21.0f)
                lineTo(252.31f, 860.0f)
                close()
                moveTo(540.0f, 340.0f)
                verticalLineToRelative(-180.0f)
                lineTo(252.31f, 160.0f)
                quadToRelative(-4.62f, 0.0f, -8.46f, 3.85f)
                quadToRelative(-3.85f, 3.84f, -3.85f, 8.46f)
                verticalLineToRelative(615.38f)
                quadToRelative(0.0f, 4.62f, 3.85f, 8.46f)
                quadToRelative(3.84f, 3.85f, 8.46f, 3.85f)
                horizontalLineToRelative(455.38f)
                quadToRelative(4.62f, 0.0f, 8.46f, -3.85f)
                quadToRelative(3.85f, -3.84f, 3.85f, -8.46f)
                lineTo(720.0f, 340.0f)
                lineTo(540.0f, 340.0f)
                close()
                moveTo(240.0f, 160.0f)
                verticalLineToRelative(180.0f)
                verticalLineToRelative(-180.0f)
                lineTo(240.0f, 800.0f)
                verticalLineToRelative(-640.0f)
                close()
            }
        }
        .build()
        return _description!!
    }

private var _description: ImageVector? = null
