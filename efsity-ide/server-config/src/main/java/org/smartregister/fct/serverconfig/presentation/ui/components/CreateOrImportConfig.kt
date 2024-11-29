package org.smartregister.fct.serverconfig.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.smartregister.fct.common.data.controller.SingleFieldDialogController
import org.smartregister.fct.serverconfig.presentation.components.ServerConfigPanelComponent

context (ServerConfigPanelComponent)
@Composable
internal fun CreateOrImportConfig(
    titleDialogController: SingleFieldDialogController
) {
    var showButton by remember { mutableStateOf(false) }

    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = showButton,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 800,
                easing = LinearEasing
            )
        )
    ) {
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CreateNewConfigButton(titleDialogController)
                Text(
                    text = "   |   ",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Thin
                    )
                )
                ImportConfigButton()
            }
        }
    }

    LaunchedEffect(showButton) {
        delay(100)
        showButton = true
    }
}