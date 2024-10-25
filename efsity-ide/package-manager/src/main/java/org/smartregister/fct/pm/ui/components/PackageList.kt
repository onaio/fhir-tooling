package org.smartregister.fct.pm.ui.components

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.aurora.presentation.ui.components.MiddleEllipsisText
import org.smartregister.fct.common.data.locals.LocalSnackbarHost
import org.smartregister.fct.common.presentation.ui.dialog.rememberSingleFieldDialog
import org.smartregister.fct.engine.domain.model.PackageInfo
import org.smartregister.fct.engine.util.uuid
import org.smartregister.fct.pm.domain.usecase.DeletePackage
import org.smartregister.fct.pm.domain.usecase.SaveNewPackage

@Composable
internal fun PackageList(
    packageList: List<PackageInfo>,
    isSavedPackage: Boolean
) {

    val snackbarHost = LocalSnackbarHost.current
    val scope = rememberCoroutineScope()
    val saveNewPackage = getKoin().get<SaveNewPackage>()
    val deletePackage = getKoin().get<DeletePackage>()
    val state = rememberLazyListState()

    val savePackageDialog = rememberSingleFieldDialog(
        title = "Save New Package"
    ) { text, controller ->
        val id = uuid()
        val packageId = controller.getExtra<String>()!!
        saveNewPackage(
            id = id,
            packageId = packageId,
            packageName = text
        )
        DeviceManager.getActivePackage().value?.packageId?.let { pid ->
            if (packageId == pid) {
                DeviceManager.setActivePackage(
                    PackageInfo(
                        id = id,
                        packageId = packageId,
                        name = text
                    )
                )
            }
        }
        snackbarHost.showSnackbar("Package $text has been saved")
    }

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = state
        ) {

            itemsIndexed(packageList) { index, packageInfo ->

                ContextMenuArea(
                    items = {
                        if (isSavedPackage) {
                            listOf(
                                ContextMenuItem("Delete") {
                                    packageInfo.id?.let { deletePackage(it) }
                                    DeviceManager.getActivePackage().value?.packageId?.let { pid ->
                                        if (packageInfo.packageId == pid) {
                                            scope.launch {
                                                DeviceManager.setActivePackage(packageInfo.copy(name = null))
                                            }
                                        }
                                    }
                                }
                            )
                        } else {
                            listOf(
                                ContextMenuItem("Save") {
                                    savePackageDialog.show(packageInfo.packageId)
                                }
                            )
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch {
                                    DeviceManager.setActivePackage(packageInfo)
                                }
                            }
                            .padding(vertical = 3.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.width(30.dp),
                            text = "${index + 1}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Column {
                            packageInfo.name?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            MiddleEllipsisText(
                                text = packageInfo.packageId,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}