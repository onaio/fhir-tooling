package org.smartregister.fct.fhirman.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import org.koin.compose.koinInject
import org.smartregister.fct.aurora.presentation.ui.components.AutoCompleteDropDown
import org.smartregister.fct.aurora.presentation.ui.components.HorizontalButtonStrip
import org.smartregister.fct.aurora.presentation.ui.components.OutlinedButton
import org.smartregister.fct.aurora.presentation.ui.components.OutlinedTextField
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.common.domain.model.ResizeOption
import org.smartregister.fct.common.presentation.ui.components.HorizontalSplitPane
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.editor.presentation.ui.view.CodeEditor
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.domain.model.HttpMethodType
import org.smartregister.fct.engine.util.listOfAllFhirResources
import org.smartregister.fct.fhirman.presentation.components.FhirmanScreenComponent
import org.smartregister.fct.fhirman.presentation.ui.components.NoConfig

@Composable
fun FhirmanScreen(component: FhirmanScreenComponent) {

    val appSettingManager = koinInject<AppSettingManager>()
    val configs by appSettingManager.appSetting.getServerConfigsAsFlow().collectAsState()

    val options = remember {
        listOf(
            HttpMethodType.Get,
            HttpMethodType.Post,
            HttpMethodType.Put,
            HttpMethodType.Delete,
        )
    }

    Aurora(
        componentContext = component
    ) { auroraManager ->

        auroraManager.showSnackbar(component.info.collectAsState().value) {
            component.showInfo(null)
        }

        auroraManager.showErrorSnackbar(component.error.collectAsState().value) {
            component.showError(null)
        }

        if (component.loading.collectAsState().value) {
            auroraManager.showLoader()
        } else {
            auroraManager.hideLoader()
        }

        if (configs.isNotEmpty()) {
            Column {

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {

                    val (btnResConfig,
                        methodType,
                        resType,
                        resId,
                        btnSend
                    ) = createRefs()

                    val initialSelectedConfig by component.selectedConfig.collectAsState()
                    OutlinedButton(
                        modifier = Modifier.constrainAs(btnResConfig) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                        label = initialSelectedConfig?.title ?: "Select Config",
                        onClick = {
                            auroraManager.selectServerConfig(initialSelectedConfig) { serverConfig ->
                                component.selectConfig(serverConfig)
                            }
                        }
                    )

                    HorizontalButtonStrip(
                        modifier = Modifier.width(400.dp).constrainAs(methodType) {
                            start.linkTo(btnResConfig.end, margin = 12.dp)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.preferredWrapContent
                        },
                        options = options,
                        label = { it.name },
                        isExpanded = true,
                        initialSelectedIndex = options.indexOf(component.methodType.value),
                        stripBackgroundColor = MaterialTheme.colorScheme.background,
                        onClick = {
                            component.selectMethodType(this)
                        }
                    )

                    Box(modifier = Modifier
                        .width(240.dp).constrainAs(resType) {
                            start.linkTo(methodType.end, margin = 12.dp)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.preferredWrapContent
                        }) {

                        AutoCompleteDropDown(
                            modifier = Modifier.fillMaxWidth(),
                            items = listOfAllFhirResources,
                            label = { it },
                            placeholder = "Resource",
                            defaultValue = component.resourceType.value,
                            onTextChanged = { text, isMatch ->
                                component.selectResourceType(text)
                            }
                        )
                    }

                    OutlinedTextField(
                        modifier = Modifier
                            .constrainAs(resId) {
                                start.linkTo(resType.end, margin = 12.dp)
                                top.linkTo(parent.top)
                                end.linkTo(btnSend.start, margin = 12.dp)
                                bottom.linkTo(parent.bottom)
                                width = Dimension.fillToConstraints
                            }
                            .onPreviewKeyEvent { keyEvent ->
                                when {
                                    keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp -> {
                                        component.send()
                                        true
                                    }

                                    else -> false
                                }
                            },
                        value = component.resourceId.collectAsState().value,
                        onValueChange = {
                            component.setResourceId(it)
                        },
                        label = "Id"
                    )

                    Tooltip(
                        modifier = Modifier.constrainAs(btnSend) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        },
                        tooltip = "Ctrl+Enter",
                        tooltipPosition = TooltipPosition.Bottom(),
                    ) {
                        OutlinedButton(
                            label = "SEND",
                            onClick = component::send
                        )
                    }
                }

                HorizontalDivider()
                HorizontalSplitPane(
                    resizeOption = ResizeOption.Flexible(
                        sizeRatio = 0.5f,
                        minSizeRatio = 0.1f,
                        maxSizeRatio = 0.9f,
                    ),
                    leftContent = {
                        CodeEditor(
                            modifier = Modifier.fillMaxSize(),
                            component = component.requestCodeEditorComponent
                        )
                    },
                    rightContent = {
                        CodeEditor(
                            modifier = Modifier.fillMaxSize(),
                            component = component.responseCodeEditorComponent,
                            enableFileImport = false,
                            enableDBImport = false,
                            toolbarOptions = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        text = component.responseStatus.collectAsState().value,
                                        style = typography.bodySmall
                                    )
                                }
                            }
                        )
                    }
                )
            }
        } else {
            NoConfig(component)
            component.reset()
        }
    }
}