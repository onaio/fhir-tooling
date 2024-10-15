package org.smartregister.fct.device_database.ui.presentation.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.TextButton
import org.smartregister.fct.device_database.domain.model.QueryResponse

@Composable
internal fun CheckQueryResponseError(
    queryResponse: QueryResponse,
    reloadAction: (() -> Unit)? = null
) {

    if (queryResponse.error != null) {

        val verticalScrollState = rememberScrollState()

        Box {
            Column(
                Modifier.verticalScroll(verticalScrollState)
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = queryResponse.error,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.error
                        )
                    )
                    reloadAction?.let {
                        TextButton(
                            contentPadding = PaddingValues(8.dp, 0.dp),
                            label = "Reload",
                            onClick = it
                        )
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = verticalScrollState
                )
            )
        }
    }
}