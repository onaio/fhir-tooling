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

public val AuroraIconPack.ElectricBoltFill: ImageVector
    get() {
        if (_electricBoltFill != null) {
            return _electricBoltFill!!
        }
        _electricBoltFill = Builder(name = "ElectricBoltFill", defaultWidth = 24.0.dp, defaultHeight
                = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(293.46f, 860.38f)
                lineTo(454.23f, 570.0f)
                lineTo(140.0f, 531.92f)
                lineToRelative(465.0f, -432.3f)
                horizontalLineToRelative(62.31f)
                lineToRelative(-162.7f, 290.77f)
                lineTo(820.0f, 428.08f)
                lineTo(355.0f, 860.38f)
                horizontalLineToRelative(-61.54f)
                close()
            }
        }
        .build()
        return _electricBoltFill!!
    }

private var _electricBoltFill: ImageVector? = null
