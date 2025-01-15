package org.smartregister.fct.fm.presentation.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.smartregister.fct.common.presentation.ui.dialog.DialogType
import org.smartregister.fct.common.presentation.ui.dialog.rememberAlertDialog
import org.smartregister.fct.common.presentation.ui.dialog.rememberSingleFieldDialog
import org.smartregister.fct.common.util.folderNameValidation
import org.smartregister.fct.fm.presentation.components.InAppFileManagerComponent

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun CreateNewFolder(component: InAppFileManagerComponent) {

    val errorAlertDialog = rememberAlertDialog<String>(
        title = "Error",
        dialogType = DialogType.Error
    ) { _, errorMessage ->
        Text(errorMessage ?: "Error on creating new folder")
    }

    val newFolderDialog = rememberSingleFieldDialog(
        title = "Create New Folder",
        validations = listOf(folderNameValidation)
    ) { folderName, _ ->
        val result = component.createNewFolder(folderName)
        if (result.isFailure) {
            errorAlertDialog.show(result.exceptionOrNull()?.message)
        }
    }

    Chip(
        modifier = Modifier.height(30.dp),
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colorScheme.surface
        ),
        onClick = {
            newFolderDialog.show()
        },
    ) {
        Text(
            text = "New Folder",
            style = MaterialTheme.typography.bodySmall
        )
    }
}