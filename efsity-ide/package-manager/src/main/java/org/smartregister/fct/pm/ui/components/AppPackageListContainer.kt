package org.smartregister.fct.pm.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.koin.java.KoinJavaComponent.getKoin
import org.smartregister.fct.adb.domain.model.Device
import org.smartregister.fct.pm.domain.usecase.GetAllPackages

@Composable
internal fun AppPackageListContainer(device: Device) {

    val getAllPackage = getKoin().get<GetAllPackages>()
    val allPackages by getAllPackage(device).collectAsState(initial = listOf())
    val searchText = remember { mutableStateOf("") }
    val filteredPackages = allPackages.filter { it.packageId.contains(searchText.value) }

    SearchPackage(searchText)
    NoPackageFound(filteredPackages.isNotEmpty())
    PackageList(filteredPackages, false)
}