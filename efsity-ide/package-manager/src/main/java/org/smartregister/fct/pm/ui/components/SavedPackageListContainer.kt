package org.smartregister.fct.pm.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.koin.java.KoinJavaComponent.getKoin
import org.smartregister.fct.pm.domain.usecase.GetSavedPackages

@Composable
internal fun SavedPackageListContainer() {

    val getSavedPackages = getKoin().get<GetSavedPackages>()
    val allPackages by getSavedPackages().collectAsState(initial = listOf())
    val searchText = remember { mutableStateOf("") }
    val filteredPackages = allPackages.filter { it.packageId.contains(searchText.value) }

    SearchPackage(searchText)
    NoPackageFound(filteredPackages.isNotEmpty())
    PackageList(filteredPackages, true)
}