package org.smartregister.fct.fm.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.launch
import org.smartregister.fct.common.data.locals.LocalSnackbarHost
import org.smartregister.fct.common.presentation.ui.dialog.rememberResourceUploadDialog
import org.smartregister.fct.fm.presentation.components.FileManagerScreenComponent
import org.smartregister.fct.fm.presentation.ui.components.InAppFileManager
import org.smartregister.fct.fm.presentation.ui.components.SystemFileManager

@Composable
fun FileManagerScreen(component: FileManagerScreenComponent) {

    Row(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            SystemFileManager(component)
        }
        VerticalDivider()
        Box(Modifier.weight(1f)) {
            InAppFileManager(component)
        }
    }

    with(component) {
        ResourceUpload()
    }
}

@Composable
private fun FileManagerScreenComponent.ResourceUpload() {
    val snackbarHost = LocalSnackbarHost.current
    val scope = rememberCoroutineScope()
    val parseError by error.subscribeAsState()
    val fileContent by jsonContent.collectAsState()

    if (parseError.trim().isNotEmpty()) {
        scope.launch {
            snackbarHost.showSnackbar(parseError)
            error.value = ""
        }
    }

    val uploadResourceDialog = rememberResourceUploadDialog(
        componentContext = this,
        onDismiss = { resetContent() }
    )

    fileContent?.let(uploadResourceDialog::show)
}

