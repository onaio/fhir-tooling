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

public val AuroraIconPack.Dataset: ImageVector
    get() {
        if (_dataset != null) {
            return _dataset!!
        }
        _dataset = Builder(name = "Dataset", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(290.0f, 670.0f)
                horizontalLineToRelative(140.0f)
                verticalLineToRelative(-140.0f)
                lineTo(290.0f, 530.0f)
                verticalLineToRelative(140.0f)
                close()
                moveTo(530.0f, 670.0f)
                horizontalLineToRelative(140.0f)
                verticalLineToRelative(-140.0f)
                lineTo(530.0f, 530.0f)
                verticalLineToRelative(140.0f)
                close()
                moveTo(290.0f, 430.0f)
                horizontalLineToRelative(140.0f)
                verticalLineToRelative(-140.0f)
                lineTo(290.0f, 290.0f)
                verticalLineToRelative(140.0f)
                close()
                moveTo(530.0f, 430.0f)
                horizontalLineToRelative(140.0f)
                verticalLineToRelative(-140.0f)
                lineTo(530.0f, 290.0f)
                verticalLineToRelative(140.0f)
                close()
                moveTo(212.31f, 820.0f)
                quadTo(182.0f, 820.0f, 161.0f, 799.0f)
                quadToRelative(-21.0f, -21.0f, -21.0f, -51.31f)
                verticalLineToRelative(-535.38f)
                quadTo(140.0f, 182.0f, 161.0f, 161.0f)
                quadToRelative(21.0f, -21.0f, 51.31f, -21.0f)
                horizontalLineToRelative(535.38f)
                quadTo(778.0f, 140.0f, 799.0f, 161.0f)
                quadToRelative(21.0f, 21.0f, 21.0f, 51.31f)
                verticalLineToRelative(535.38f)
                quadTo(820.0f, 778.0f, 799.0f, 799.0f)
                quadToRelative(-21.0f, 21.0f, -51.31f, 21.0f)
                lineTo(212.31f, 820.0f)
                close()
                moveTo(212.31f, 760.0f)
                horizontalLineToRelative(535.38f)
                quadToRelative(4.62f, 0.0f, 8.46f, -3.85f)
                quadToRelative(3.85f, -3.84f, 3.85f, -8.46f)
                verticalLineToRelative(-535.38f)
                quadToRelative(0.0f, -4.62f, -3.85f, -8.46f)
                quadToRelative(-3.84f, -3.85f, -8.46f, -3.85f)
                lineTo(212.31f, 200.0f)
                quadToRelative(-4.62f, 0.0f, -8.46f, 3.85f)
                quadToRelative(-3.85f, 3.84f, -3.85f, 8.46f)
                verticalLineToRelative(535.38f)
                quadToRelative(0.0f, 4.62f, 3.85f, 8.46f)
                quadToRelative(3.84f, 3.85f, 8.46f, 3.85f)
                close()
                moveTo(200.0f, 200.0f)
                verticalLineToRelative(560.0f)
                verticalLineToRelative(-560.0f)
                close()
            }
        }
        .build()
        return _dataset!!
    }

private var _dataset: ImageVector? = null
