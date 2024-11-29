package org.smartregister.fct.serverconfig.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import fct.server_config.generated.resources.Res
import fct.server_config.generated.resources.create_new
import org.smartregister.fct.aurora.presentation.ui.components.TextButton
import org.smartregister.fct.common.data.controller.SingleFieldDialogController
import org.smartregister.fct.serverconfig.util.asString

@Composable
internal fun CreateNewConfigButton(titleDialogController: SingleFieldDialogController) {

    TextButton(
        label = Res.string.create_new.asString(),
        icon = Icons.Outlined.Add,
        onClick = {
            titleDialogController.show()
        }
    )
}