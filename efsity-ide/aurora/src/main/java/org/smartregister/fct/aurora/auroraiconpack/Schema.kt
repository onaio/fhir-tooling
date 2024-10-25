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

public val AuroraIconPack.Schema: ImageVector
    get() {
        if (_schema != null) {
            return _schema!!
        }
        _schema = Builder(name = "Schema", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(177.69f, 902.31f)
                verticalLineToRelative(-204.62f)
                lineTo(270.0f, 697.69f)
                verticalLineToRelative(-115.38f)
                horizontalLineToRelative(-92.31f)
                verticalLineToRelative(-204.62f)
                lineTo(270.0f, 377.69f)
                verticalLineToRelative(-115.38f)
                horizontalLineToRelative(-92.31f)
                verticalLineToRelative(-204.62f)
                horizontalLineToRelative(244.62f)
                verticalLineToRelative(204.62f)
                lineTo(330.0f, 262.31f)
                verticalLineToRelative(115.38f)
                horizontalLineToRelative(92.31f)
                lineTo(422.31f, 450.0f)
                horizontalLineToRelative(155.38f)
                verticalLineToRelative(-72.31f)
                horizontalLineToRelative(244.62f)
                verticalLineToRelative(204.62f)
                lineTo(577.69f, 582.31f)
                lineTo(577.69f, 510.0f)
                lineTo(422.31f, 510.0f)
                verticalLineToRelative(72.31f)
                lineTo(330.0f, 582.31f)
                verticalLineToRelative(115.38f)
                horizontalLineToRelative(92.31f)
                verticalLineToRelative(204.62f)
                lineTo(177.69f, 902.31f)
                close()
                moveTo(237.69f, 842.31f)
                horizontalLineToRelative(124.62f)
                verticalLineToRelative(-84.62f)
                lineTo(237.69f, 757.69f)
                verticalLineToRelative(84.62f)
                close()
                moveTo(237.69f, 522.31f)
                horizontalLineToRelative(124.62f)
                verticalLineToRelative(-84.62f)
                lineTo(237.69f, 437.69f)
                verticalLineToRelative(84.62f)
                close()
                moveTo(637.69f, 522.31f)
                horizontalLineToRelative(124.62f)
                verticalLineToRelative(-84.62f)
                lineTo(637.69f, 437.69f)
                verticalLineToRelative(84.62f)
                close()
                moveTo(237.69f, 202.31f)
                horizontalLineToRelative(124.62f)
                verticalLineToRelative(-84.62f)
                lineTo(237.69f, 117.69f)
                verticalLineToRelative(84.62f)
                close()
                moveTo(300.0f, 160.0f)
                close()
                moveTo(300.0f, 480.0f)
                close()
                moveTo(700.0f, 480.0f)
                close()
                moveTo(300.0f, 800.0f)
                close()
            }
        }
        .build()
        return _schema!!
    }

private var _schema: ImageVector? = null
