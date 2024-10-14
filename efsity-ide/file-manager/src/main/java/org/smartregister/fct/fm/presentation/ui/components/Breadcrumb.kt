package org.smartregister.fct.fm.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import org.smartregister.fct.fm.presentation.components.FileManagerComponent

@Composable
internal fun ConstraintLayoutScope.Breadcrumb(
    component: FileManagerComponent,
    pathRef: ConstrainedLayoutReference,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f))
            .constrainAs(pathRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
    ) {
        HorizontalDivider()
        Text(
            modifier = Modifier.padding(6.dp),
            text = component.getActivePath().collectAsState().value.toString(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodySmall
        )
    }
}