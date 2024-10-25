package org.smartregister.fct.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.common.data.manager.SubWindowManager
import org.smartregister.fct.common.util.windowTitle

@Composable
fun TitleBar(
    componentContext: ComponentContext,
    subWindowManager: SubWindowManager
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {

                FhirAnimatedIcon()
                Spacer(Modifier.width(18.dp))
                DeviceSelectionMenu()
                Spacer(Modifier.width(10.dp))
                ActivePackageChip(subWindowManager = subWindowManager)
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = windowTitle.collectAsState().value,
                    style = MaterialTheme.typography.titleSmall,
                    color = colorScheme.onSurface
                )
                Spacer(Modifier.width(16.dp))
                Base64EncodeDecodeButton(componentContext)
                Spacer(Modifier.width(12.dp))
                TextViewerButton(componentContext)
                Spacer(Modifier.width(12.dp))
                SettingButton(componentContext)
            }
        }
    }
}