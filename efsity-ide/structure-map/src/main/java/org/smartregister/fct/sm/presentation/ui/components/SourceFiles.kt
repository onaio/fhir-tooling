package org.smartregister.fct.sm.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Description
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.PanelHeading
import org.smartregister.fct.sm.presentation.component.StructureMapScreenComponent
import org.smartregister.fct.sm.util.SMConfig

@Composable
internal fun SourceFiles(
    component: StructureMapScreenComponent,
) {

    val sourceFileType by component.sourceFileType.collectAsState()
    val smModel = component.activeStructureMap.value!!
    val mapPath = smModel.mapPath
    val sourcePath = smModel.sourcePath

    Box(Modifier.width(300.dp)) {
        Column(
            modifier = Modifier.fillMaxSize().background(colorScheme.surfaceContainer.copy(0.5f))
        ) {
            PanelHeading(text = "Source Files")
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                item {
                    listOf(mapPath, sourcePath).forEach { path ->
                        Row(modifier = Modifier.fillMaxWidth()
                            .background(itemBackground(component, path))
                            .clickable {
                                component.openPath(path)
                            },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(Modifier.width(12.dp))
                            Icon(
                                modifier = Modifier.size(20.dp), icon = AuroraIconPack.Description
                            )

                            val sourceName = if (path == sourcePath && sourceFileType.trim().isNotEmpty()) {
                                buildAnnotatedString {
                                    append(SMConfig.getFileName(path))

                                    withStyle(SpanStyle(
                                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                                    )) {
                                        append(" - $sourceFileType")
                                    }
                                }
                            } else {
                                buildAnnotatedString {
                                    append(SMConfig.getFileName(path))
                                }
                            }

                            Text(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                text = sourceName
                            )
                        }

                        HorizontalDivider()
                    }
                }
            }
        }
        VerticalDivider(Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun itemBackground(component: StructureMapScreenComponent, path: String): Color {
    return if (path == component.openPath.collectAsState().value) colorScheme.surface.copy(0.8f) else colorScheme.surfaceContainer
}