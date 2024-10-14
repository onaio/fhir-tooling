package org.smartregister.fct.rules.presentation.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Resource
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Database
import org.smartregister.fct.aurora.presentation.ui.components.AutoCompleteDropDown
import org.smartregister.fct.aurora.presentation.ui.components.Button
import org.smartregister.fct.aurora.presentation.ui.components.OutlinedTextField
import org.smartregister.fct.aurora.presentation.ui.components.SmallIconButton
import org.smartregister.fct.common.data.controller.DialogController
import org.smartregister.fct.common.presentation.ui.dialog.rememberDialog
import org.smartregister.fct.device_database.ui.presentation.dialog.rememberDBDataProviderDialog
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.decodeResourceFromString
import org.smartregister.fct.engine.util.listOfAllFhirResources
import org.smartregister.fct.engine.util.logicalId
import org.smartregister.fct.engine.util.uuid
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.rules.domain.model.DataSource
import org.smartregister.fct.rules.domain.model.Widget
import org.smartregister.fct.rules.domain.model.Workspace

@Composable
internal fun rememberNewWorkspaceDialog(
    componentContext: ComponentContext,
    title: String = "New Workspace",
    onDismiss: ((DialogController<Workspace>) -> Unit)? = null,
    onDone: (Workspace) -> Unit
): DialogController<Workspace> {

    val dialogController = rememberDialog(
        width = 500.dp,
        height = 400.dp,
        title = title,
        onDismiss = onDismiss,
        cancelOnTouchOutside = false
    ) { controller, existingDataSource ->

        NewWorkspaceDialog(
            componentContext = componentContext,
            controller = controller,
            existingDataSource = existingDataSource,
            onDone = onDone,
        )
    }

    return dialogController
}

@Composable
private fun NewWorkspaceDialog(
    componentContext: ComponentContext,
    controller: DialogController<Workspace>,
    existingDataSource: Workspace?,
    onDone: (Workspace) -> Unit,
) {

    val idRegex = "^\\w+".toRegex()

    var name by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var logicalId by remember { mutableStateOf("") }
    var resourceType by remember { mutableStateOf("") }
    var matchResource by remember { mutableStateOf(MatchResource.UseAny) }
    val focusRequester = remember { FocusRequester() }

    var nameError by remember { mutableStateOf(false) }
    var idError by remember { mutableStateOf(false) }
    var logicalIdError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val dbDataProviderDialog = rememberDBDataProviderDialog(
        componentContext = componentContext,
        defaultQuery = "SELECT * FROM ResourceEntity",
    ) { data ->
        componentContext.componentScope.launch {
            logicalId = try {
                data.decodeResourceFromString<Resource>().logicalId
            } catch (ex :Exception) {
                FCTLogger.e(ex)
                data
            }
        }
    }

    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            value = name,
            onValueChange = {
                val input = it.trim()
                nameError = input.isEmpty()
                name = it
            },
            placeholder = "Workspace Name",
            isError = nameError
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = id,
            onValueChange = {
                val input = it.trim()
                idError = if (input.isNotEmpty()) idRegex.matchEntire(input) == null else false
                id = input
            },
            placeholder = "Base Resource Id (Optional)",
            isError = idError
        )
        Spacer(Modifier.height(12.dp))
        AutoCompleteDropDown(
            modifier = Modifier.fillMaxWidth(),
            items = listOfAllFhirResources,
            label = { it },
            placeholder = "Select Base Resource",
            onTextChanged = { text, isMatch ->
                resourceType = if (isMatch) text else ""
            }
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = matchResource == MatchResource.UseAny,
                    onClick = {
                        matchResource = MatchResource.UseAny
                    }
                )
                Text(
                    text = "Use Any"
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = matchResource == MatchResource.UseLogicalId,
                        onClick = {
                            matchResource = MatchResource.UseLogicalId
                        }
                    )
                    Text(
                        text = "Use Logical Id"
                    )
                }
                if (matchResource == MatchResource.UseLogicalId) {
                    SmallIconButton(
                        icon = AuroraIconPack.Database,
                        onClick = {
                            dbDataProviderDialog.show()
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        if (matchResource == MatchResource.UseLogicalId) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = logicalId,
                onValueChange = {
                    val input = it.trim()
                    logicalIdError = input.isEmpty()
                    logicalId = input
                },
                placeholder = "Resource Logical Id",
                isError = logicalIdError
            )
            Spacer(Modifier.height(10.dp))
        } else {
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                modifier = Modifier,
                enable = !nameError && !idError && !logicalIdError && name.trim()
                    .isNotEmpty() && resourceType.trim()
                    .isNotEmpty() && (if (matchResource == MatchResource.UseLogicalId) logicalId.trim()
                    .isNotEmpty() else true),
                label = "Create",
                onClick = {
                    controller.hide()
                    onDone(
                        Workspace(
                            id = uuid(),
                            name = name,
                            dataSources = listOf(
                                Widget(
                                    body = DataSource(
                                        id = id,
                                        query = "SELECT * FROM ResourceEntity WHERE resourceType='$resourceType' ${if (matchResource == MatchResource.UseLogicalId) "AND resourceId='$logicalId'" else ""} LIMIT 1",
                                        resourceType = resourceType,
                                        isSingle = true
                                    ),
                                )
                            ),
                            rules = listOf()
                        )
                    )
                }
            )
        }
    }
}

private enum class MatchResource {
    UseAny, UseLogicalId
}