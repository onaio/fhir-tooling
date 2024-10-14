package org.smartregister.fct.presentation.ui.components

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.EditNote
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.text_viewer.ui.dialog.rememberTextViewerDialog

@Composable
internal fun TextViewerButton(
    componentContext: ComponentContext
) {

    val textViewerDialog = rememberTextViewerDialog(
        componentContext = componentContext
    )

    SmallIconButton(
        tooltip = "Text Viewer / Formatter",
        tooltipPosition = TooltipPosition.Bottom(),
        onClick = {
            textViewerDialog.show()
        },
        tint = colorScheme.onSurface,
        icon = AuroraIconPack.EditNote,
    )
}