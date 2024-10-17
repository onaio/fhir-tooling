package org.smartregister.fct.pm.ui.components

import androidx.compose.runtime.Composable
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.aurora.presentation.ui.components.Tabs

@Composable
internal fun PackageTabs(device: Device?) {

    Tabs(
        tabs = listOf("Saved Packages", "All Packages"),
        title = {it},
        onSelected = { tabIndex, _ ->
            when (tabIndex) {
                0 -> SavedPackageListContainer()
                1 -> device?.let { AppPackageListContainer(it) }
            }
        }
    )
}