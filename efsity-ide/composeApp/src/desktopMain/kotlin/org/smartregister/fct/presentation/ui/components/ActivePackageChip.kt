package org.smartregister.fct.presentation.ui.components

import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.TextStyle
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.common.data.enums.RightWindowState
import org.smartregister.fct.common.data.manager.SubWindowManager

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ActivePackageChip(subWindowManager: SubWindowManager) {

    val activePackage by DeviceManager.getActivePackage().collectAsState(initial = null)

    Chip(
        onClick = {
            subWindowManager.setRightWindowState(RightWindowState.PackageManager)
        },
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Text(
            text = activePackage?.name ?: activePackage?.packageId ?: "Select Package",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onTertiary,
                fontSize = MaterialTheme.typography.titleSmall.fontSize
            )
        )
    }
}