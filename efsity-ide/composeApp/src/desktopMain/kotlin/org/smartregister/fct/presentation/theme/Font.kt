package org.smartregister.fct.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import fct.composeapp.generated.resources.Res
import fct.composeapp.generated.resources.Ubuntu_Bold
import fct.composeapp.generated.resources.Ubuntu_Light
import fct.composeapp.generated.resources.Ubuntu_Medium
import fct.composeapp.generated.resources.Ubuntu_Regular
import org.jetbrains.compose.resources.Font

@Composable
fun UbuntuFontFamily() = FontFamily(
    Font(Res.font.Ubuntu_Light, weight = FontWeight.Light),
    Font(Res.font.Ubuntu_Regular, weight = FontWeight.Normal),
    Font(Res.font.Ubuntu_Medium, weight = FontWeight.Medium),
    Font(Res.font.Ubuntu_Bold, weight = FontWeight.Bold)
)

@Composable
fun UbuntuTypography() = Typography().run {

    val fontFamily = UbuntuFontFamily()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}