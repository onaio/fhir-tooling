package org.smartregister.fct.dashboard.ui.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.OpenInNew
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.OutlinedTextField
import org.smartregister.fct.aurora.presentation.ui.components.PanelHeading
import org.smartregister.fct.engine.util.listOfAllFhirResources

@Composable
internal fun FhirResourcesList() {

    val uriHandler = LocalUriHandler.current
    var searchText by remember { mutableStateOf("") }

    PanelHeading("Fhir Resources")
    Box(Modifier.padding(8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchText,
            onValueChange = {
                searchText = it
            },
            placeholder = "Search Resource"
        )
    }
    HorizontalDivider()
    LazyColumn {

        itemsIndexed(listOfAllFhirResources.filter { it.contains(searchText, ignoreCase = true) }) { index, item ->

            Box {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${index + 1}. $item")
                    IconButton(
                        onClick = {
                            uriHandler.openUri("https://hl7.org/fhir/R4B/${item.lowercase()}.html")
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            icon = AuroraIconPack.OpenInNew,
                        )
                    }
                }
                HorizontalDivider(Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}