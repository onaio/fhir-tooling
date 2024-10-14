package org.smartregister.fct.insights.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.insights.domain.model.Insights

@Composable
internal fun InsightDetail(insights: Insights) {

    Row(
        modifier = Modifier.fillMaxWidth().height(200.dp).padding(12.dp)
    ) {

        Box(Modifier.fillMaxHeight().padding(12.dp)) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Insights",
                style = typography.titleMedium
            )
        }
        Spacer(Modifier.width(12.dp))
        if (insights.unSyncedResources.isEmpty()) {
            AllResourceSynced()
        } else {
            UnSyncedResourcesList(insights.unSyncedResources)
        }

        Spacer(Modifier.width(12.dp))
        AssignmentInfo(insights)
        Spacer(Modifier.width(12.dp))
        AppInfo(insights)
    }
}

@Composable
internal fun AllResourceSynced() {
    InfoCard(
        modifier = Modifier.width(250.dp).fillMaxHeight(),
        title = "All Resources Synced"
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(Color(0xff82e750))
            ) {
                Icon(
                    modifier = Modifier.size(40.dp).align(Alignment.Center),
                    icon = Icons.Filled.Check,
                    tint = colorScheme.surfaceContainer
                )
            }
        }
    }
}

@Composable
internal fun UnSyncedResourcesList(unSyncResources: List<Pair<String, Int>>) {

    InfoCard(
        modifier = Modifier.width(250.dp).fillMaxHeight(),
        title = "Unsynced Resources"
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            items(unSyncResources) {
                RowItem(it.first, it.second.toString())
            }
        }
    }
}

@Composable
internal fun AssignmentInfo(insights: Insights) {
    InfoCard(
        modifier = Modifier.width(250.dp).fillMaxHeight(),
        title = "Assignment Info"
    ) {
        RowItem("Username", insights.userName)
        RowItem("Team(Organization)", insights.organization)
        RowItem("Care Team", insights.careTeam)
        RowItem("Location", insights.location)
    }
}

@Composable
internal fun AppInfo(insights: Insights) {
    InfoCard(
        modifier = Modifier.width(300.dp).fillMaxHeight(),
        title = "App Info"
    ) {
        RowItem("App version", insights.appVersion)
        RowItem("App version code", insights.appVersionCode)
        RowItem("Build date", insights.buildDate)
    }
}

@Composable
internal fun InfoCard(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface.copy(0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = typography.titleSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            content(this)
        }
    }
}

@Composable
fun RowItem(key: String, value: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = key,
            style = typography.titleSmall
        )
        Text(value?.ifEmpty { "-" } ?: "-")
    }
}