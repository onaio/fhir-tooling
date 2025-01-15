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

public val AuroraIconPack.Dns: ImageVector
    get() {
        if (_dns != null) {
            return _dns!!
        }
        _dns = Builder(name = "Dns", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth
                = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(300.05f, 262.31f)
                quadToRelative(-20.82f, 0.0f, -35.43f, 14.57f)
                quadTo(250.0f, 291.45f, 250.0f, 312.26f)
                quadToRelative(0.0f, 20.82f, 14.57f, 35.43f)
                quadToRelative(14.57f, 14.62f, 35.38f, 14.62f)
                quadToRelative(20.82f, 0.0f, 35.43f, -14.57f)
                quadTo(350.0f, 333.17f, 350.0f, 312.35f)
                quadToRelative(0.0f, -20.81f, -14.57f, -35.43f)
                quadToRelative(-14.57f, -14.61f, -35.38f, -14.61f)
                close()
                moveTo(300.05f, 637.69f)
                quadToRelative(-20.82f, 0.0f, -35.43f, 14.57f)
                quadTo(250.0f, 666.83f, 250.0f, 687.65f)
                quadToRelative(0.0f, 20.81f, 14.57f, 35.43f)
                quadToRelative(14.57f, 14.61f, 35.38f, 14.61f)
                quadToRelative(20.82f, 0.0f, 35.43f, -14.57f)
                quadTo(350.0f, 708.55f, 350.0f, 687.74f)
                quadToRelative(0.0f, -20.82f, -14.57f, -35.43f)
                quadToRelative(-14.57f, -14.62f, -35.38f, -14.62f)
                close()
                moveTo(175.39f, 152.31f)
                horizontalLineToRelative(609.22f)
                quadToRelative(15.04f, 0.0f, 25.22f, 10.15f)
                quadTo(820.0f, 172.6f, 820.0f, 187.6f)
                verticalLineToRelative(247.01f)
                quadToRelative(0.0f, 16.24f, -10.17f, 26.97f)
                quadToRelative(-10.18f, 10.73f, -25.22f, 10.73f)
                lineTo(175.39f, 472.31f)
                quadToRelative(-15.04f, 0.0f, -25.22f, -10.73f)
                quadTo(140.0f, 450.85f, 140.0f, 434.61f)
                lineTo(140.0f, 187.6f)
                quadToRelative(0.0f, -15.0f, 10.17f, -25.14f)
                quadToRelative(10.18f, -10.15f, 25.22f, -10.15f)
                close()
                moveTo(200.0f, 212.31f)
                verticalLineToRelative(200.0f)
                horizontalLineToRelative(560.0f)
                verticalLineToRelative(-200.0f)
                lineTo(200.0f, 212.31f)
                close()
                moveTo(175.39f, 527.69f)
                horizontalLineToRelative(608.45f)
                quadToRelative(15.85f, 0.0f, 26.0f, 10.62f)
                quadTo(820.0f, 548.92f, 820.0f, 564.62f)
                verticalLineToRelative(244.61f)
                quadToRelative(0.0f, 17.0f, -10.16f, 27.73f)
                quadToRelative(-10.15f, 10.73f, -26.0f, 10.73f)
                lineTo(176.16f, 847.69f)
                quadToRelative(-15.85f, 0.0f, -26.0f, -10.73f)
                quadTo(140.0f, 826.23f, 140.0f, 809.23f)
                verticalLineToRelative(-244.61f)
                quadToRelative(0.0f, -15.7f, 9.77f, -26.31f)
                quadToRelative(9.77f, -10.62f, 25.62f, -10.62f)
                close()
                moveTo(200.0f, 587.69f)
                verticalLineToRelative(200.0f)
                horizontalLineToRelative(560.0f)
                verticalLineToRelative(-200.0f)
                lineTo(200.0f, 587.69f)
                close()
                moveTo(200.0f, 212.31f)
                verticalLineToRelative(200.0f)
                verticalLineToRelative(-200.0f)
                close()
                moveTo(200.0f, 587.69f)
                verticalLineToRelative(200.0f)
                verticalLineToRelative(-200.0f)
                close()
            }
        }
        .build()
        return _dns!!
    }

private var _dns: ImageVector? = null
