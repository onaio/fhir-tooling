package org.smartregister.fct.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.smartregister.fct.aurora.presentation.ui.components.getLottieFireComposition

@Composable
internal fun FhirAnimatedIcon() {
    Image(
        modifier = Modifier.height(24.dp).padding(start = 12.dp),
        painter = rememberLottiePainter(
            composition = getLottieFireComposition(),
            iterations = Compottie.IterateForever
        ),
        contentDescription = null
    )
}