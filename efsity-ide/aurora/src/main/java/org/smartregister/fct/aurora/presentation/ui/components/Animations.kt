package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import fct.aurora.generated.resources.Res
import io.github.alexzhirkevich.compottie.LottieComposition
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun getLottieFireComposition(): LottieComposition? {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/fire_animation.json").decodeToString()
        )
    }

    return composition
}