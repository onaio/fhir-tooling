package org.smartregister.fct.rules.presentation.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import org.koin.compose.koinInject
import org.smartregister.fct.aurora.presentation.ui.components.AutoCompleteDropDown
import org.smartregister.fct.aurora.presentation.ui.components.Button
import org.smartregister.fct.aurora.presentation.ui.components.OutlinedTextField
import org.smartregister.fct.common.data.controller.DialogController
import org.smartregister.fct.common.presentation.ui.dialog.rememberConfirmationDialog
import org.smartregister.fct.common.presentation.ui.dialog.rememberDialog
import org.smartregister.fct.device_database.data.transformation.SQLQueryTransformation
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.domain.model.AppSetting
import org.smartregister.fct.engine.util.listOfAllFhirResources
import org.smartregister.fct.rules.domain.model.DataSource
import org.smartregister.fct.rules.domain.model.Widget

@Composable
internal fun rememberNewDataSourceDialog(
    title: String = "New Data Source",
    onDismiss: ((DialogController<Widget<DataSource>>) -> Unit)? = null,
    onDeleteDataSource: ((Widget<DataSource>) -> Unit)? = null,
    onDone: (Widget<DataSource>, Boolean) -> Unit
): DialogController<Widget<DataSource>> {

    val scope = rememberCoroutineScope()

    val dialogController = rememberDialog(
        width = 600.dp,
        height = 400.dp,
        title = title,
        onDismiss = onDismiss,
        cancelOnTouchOutside = false,
    ) { controller, existingWidget ->


        NewDataSourceDialog(
            controller = controller,
            existingWidget = existingWidget,
            onDeleteDataSource = onDeleteDataSource,
            onDone = onDone,
        )
    }

    return dialogController
}

@Composable
private fun NewDataSourceDialog(
    controller: DialogController<Widget<DataSource>>,
    existingWidget: Widget<DataSource>?,
    onDeleteDataSource: ((Widget<DataSource>) -> Unit)?,
    onDone: (Widget<DataSource>, Boolean) -> Unit,
) {

    val appSetting: AppSetting = koinInject<AppSettingManager>().appSetting
    val isDarkTheme = appSetting.isDarkTheme
    val focusRequester = remember { FocusRequester() }
    val idRegex = "^\\w+".toRegex()

    val existingDataSource = existingWidget?.body
    var id by remember { mutableStateOf(existingDataSource?.id ?: "") }
    var query by remember { mutableStateOf(existingDataSource?.query ?: "") }
    val isSingle = remember { existingDataSource?.isSingle ?: false }
    var resourceType = existingDataSource?.resourceType

    var idError by remember { mutableStateOf(false) }
    var queryError by remember { mutableStateOf(false) }

    val deleteDataSourceDialog = rememberConfirmationDialog<Widget<DataSource>> { _, widget ->
        controller.hide()
        onDeleteDataSource?.invoke(widget!!)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            value = id,
            onValueChange = {
                val input = it.trim()
                idError = !isSingle && if (input.isNotEmpty()) idRegex.matchEntire(input) == null else true
                id = it
            },
            placeholder = "Id",
            isError = idError
        )
        Spacer(Modifier.height(10.dp))
        AutoCompleteDropDown(
            modifier = Modifier.fillMaxWidth(),
            items = listOfAllFhirResources,
            label = { it },
            placeholder = "Resource",
            defaultValue = resourceType,
            onTextChanged = { text, isMatch ->
                resourceType = if (isMatch) text else null
                if (resourceType != null) {
                    val limit =
                        if (existingWidget != null && existingWidget.body.isSingle) "LIMIT 1" else ""
                    query = "SELECT * FROM ResourceEntity WHERE resourceType='$resourceType' $limit"
                }
            }
        )
        Spacer(Modifier.height(10.dp))
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            val (queryRef, btnRef) = createRefs()

            OutlinedTextField(
                modifier = Modifier.constrainAs(queryRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(btnRef.top, 8.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
                value = query,
                onValueChange = {
                    query = it
                    queryError = query.trim().isEmpty()
                },
                placeholder = "SELECT * FROM ...",
                singleLine = false,
                isError = queryError,
                visualTransformation = SQLQueryTransformation(
                    isDarkTheme = isDarkTheme,
                    colorScheme = MaterialTheme.colorScheme
                )
            )

            Row(
                modifier = Modifier.constrainAs(btnRef) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            ) {

                if (existingWidget != null && !existingWidget.body.isSingle) {
                    Button(
                        modifier = Modifier,
                        label = "Delete",
                        onClick = {
                            deleteDataSourceDialog.show(
                                title = "Delete Data Source",
                                message = "Are you sure you want to delete this data source?",
                                data = existingWidget
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    )
                    Spacer(Modifier.width(12.dp))
                }

                Button(
                    enable = (id.trim().isNotEmpty() || resourceType != null) && query.trim()
                        .isNotEmpty() && !idError && !queryError,
                    label = if (existingWidget != null) "Update" else "Add",
                    onClick = {
                        val widget = existingWidget?.let {
                            it.body = DataSource(
                                id = id.trim(),
                                query = query.trim(),
                                resourceType = resourceType ?: "",
                                isSingle = it.body.isSingle
                            )
                            it
                        } ?: Widget(
                            body = DataSource(
                                id = id.trim(),
                                resourceType = resourceType ?: "",
                                isSingle = false,
                                query = query.trim(),
                            )
                        )
                        controller.hide()
                        onDone(widget, existingWidget != null)
                    }
                )
            }
        }
    }
}