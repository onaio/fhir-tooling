package org.smartregister.fct.presentation.ui.components

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Encrypted
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.base64.ui.dialog.rememberBase64EncodeDecodeDialog

@Composable
internal fun Base64EncodeDecodeButton(
    componentContext: ComponentContext
) {

    val base64EncodeDecodeDialog = rememberBase64EncodeDecodeDialog(
        componentContext = componentContext
    )

    SmallIconButton(
        tooltip = "Base64 Encode / Decode",
        tooltipPosition = TooltipPosition.Bottom(),
        onClick = {
            base64EncodeDecodeDialog.show()
        },
        tint = colorScheme.onSurface,
        icon = AuroraIconPack.Encrypted,
    )
}