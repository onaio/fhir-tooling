package org.smartregister.fct.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF74739A),
    secondary = Color(0xFF9C98B1),
    tertiary = Color(0xFFEC6C34),
    onPrimary = Color(0xFFF2EFFF),
    primaryContainer = Color(0xFFAEADD7),
    onPrimaryContainer = Color(0xFF1a1249),
    onSecondary = Color(0xFF262337),
    secondaryContainer = Color(0xFFC5C1DA),
    onSecondaryContainer = Color(0xFF221F32),
    onTertiary = Color(0xFFFFFFFF),
    onTertiaryContainer = Color(0xFFEFEEFF),
    tertiaryContainer = Color(0xFFEB292B),
    background = Color(0xfffbfcfd),
    onBackground = Color(0xFF1c1b20),
    surface = Color(0xffebecf0),
    surfaceContainer = Color(0xfff2f4f8),
    onSurface = Color(0xFF1c1b20),
    surfaceVariant = Color(0xFFDADADA),
    onSurfaceVariant = Color(0xFF47464f),
    error = Color(0xFFba1a1a),
    onError = Color(0xFFffffff),
    errorContainer = Color(0xFFffdad6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xff95999d),
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF74739A),
    secondary = Color(0xFF474459),
    tertiary = Color(0xFFEC6C34),
    onPrimary = Color(0xFFF2EFFF),
    primaryContainer = Color(0xFFAEADD7),
    onPrimaryContainer = Color(0xFFe4dfff),
    onSecondary = Color(0xFFBBB6CF),
    secondaryContainer = Color(0xFF8F8BA2),
    onSecondaryContainer = Color(0xFFe5dff9),
    onTertiary = Color(0xFFFFFFFF),
    onTertiaryContainer = Color(0xffffefee),
    tertiaryContainer = Color(0xFFEB292B),
    background = Color(0xff515254),
    onBackground = Color(0xFFe5e1e9),
    surface = Color(0xff383a3b),
    surfaceContainer = Color(0xff47484b),
    onSurface = Color(0xFFe5e1e9),
    surfaceVariant = Color(0xFFDADADA),
    onSurfaceVariant = Color(0xFFc9c5d0),
    error = Color(0xfffa5d5d),
    onError = Color(0xFFffffff),
    errorContainer = Color(0xFF93000a),
    onErrorContainer = Color(0xFFffdad6),
    outline = Color(0xff000000),

)

@Composable
fun AuroraTheme(
    isDarkModel: Boolean = false,
    content: @Composable () -> Unit
) {
    val typography = UbuntuTypography()

    MaterialTheme(
        colorScheme = if (isDarkModel) DarkColorScheme else LightColorScheme,
        typography = typography
    ) {
        ProvideTextStyle(value = typography.bodyMedium, content = content)
    }
}