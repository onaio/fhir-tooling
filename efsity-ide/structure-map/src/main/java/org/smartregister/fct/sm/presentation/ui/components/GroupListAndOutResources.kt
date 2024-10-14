package org.smartregister.fct.sm.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
internal fun GroupListAndOutResources(showLinkIcon: Boolean, items: List<String>) {

    Box {

        val scrollState = rememberScrollState()
        val uriHandler = LocalUriHandler.current

        LazyColumn(Modifier.verticalScroll(scrollState).heightIn(min = 50.dp, max = 250.dp)) {
            items(items) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(if (showLinkIcon) 0.85f else 1f)
                            .clickable {}.padding(horizontal = 8.dp, vertical = 4.dp),
                        text = item,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = MaterialTheme.typography.labelMedium.fontSize
                        )
                    )

                    if (showLinkIcon) {
                        Image(
                            modifier = Modifier.width(20.dp).padding(end = 5.dp).clickable {
                                uriHandler.openUri("https://hl7.org/fhir/R4B/${item.lowercase()}.html")
                            },
                            imageVector = Icons.Outlined.Link,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                            contentDescription = null
                        )
                    }
                }

            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).heightIn(min = 50.dp, max = 250.dp),
            adapter = rememberScrollbarAdapter(
                scrollState = scrollState
            )
        )
    }

}