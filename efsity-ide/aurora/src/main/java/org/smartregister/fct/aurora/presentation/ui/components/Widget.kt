package org.smartregister.fct.aurora.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardWidget(
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainer.copy(0.5f)
        ),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(
            width = 1.dp,
            color = colorScheme.onSurface.copy(0.2f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        content(this)
    }
}