package org.smartregister.fct.rules.presentation.ui.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import org.smartregister.fct.rules.data.enums.Placement
import org.smartregister.fct.rules.domain.model.PointF
import org.smartregister.fct.rules.domain.model.Rule
import org.smartregister.fct.rules.domain.model.Widget
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent
import java.io.Serializable

@Composable
internal fun DrawConnections(
    component: RulesScreenComponent,
    widget: Widget<Rule>,
    highlight: Boolean,
    isSelected: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val showConnection by component.showConnection.collectAsState()
    val completion by remember(highlight, showConnection, isSelected) {
        mutableStateOf(if (highlight || showConnection || isSelected) 1f else 0f)
    }
    val pathCompletion by animateFloatAsState(completion, tween(1000, easing = EaseInOutCubic))

    Canvas(Modifier.fillMaxSize()) {
        component.findParents(widget).forEach { parentWidget ->

            val path = createPath(widget, parentWidget)
            val pathSegment = calculatePathSegment(path, pathCompletion)



            drawPath(
                path = pathSegment,
                color = colorScheme.onSurface.copy(if (highlight) 1f else 0.4f),
                style = Stroke(
                    width = 2f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Bevel
                )
            )
        }
    }
}

private fun createPath(widget: Widget<Rule>, parentWidget: Widget<out Serializable>): Path {

    val placement = widget.placement

    return Path().apply {

        //if (widget.x <= parentWidget.x)
        val childX =
            if (widget.x <= parentWidget.x) widget.x + widget.size.width else widget.x
        val childY = widget.y + widget.size.height / 2f

        moveTo(childX, childY)

        val parentX =
            if (placement == Placement.Left) parentWidget.x else parentWidget.x + parentWidget.size.width
        val parentY = parentWidget.y + parentWidget.size.height / 2f

        val stretch = 100f
        val cp1 = PointF(
            if (widget.x <= parentWidget.x) childX + stretch else childX - stretch,
            childY
        )
        val cp2 = PointF(
            if (placement == Placement.Left) parentX - stretch else parentX + stretch,
            parentY
        )

        val lift = 2f

        cubicTo(
            x1 = cp1.x,
            y1 = cp1.y,
            x2 = cp2.x,
            y2 = cp2.y,
            x3 = parentX,
            y3 = parentY - lift
        )

        val arrowX = if (placement == Placement.Left) parentX - 8f else parentX + 8f
        lineTo(arrowX, parentY - (6f + lift))
        lineTo(parentX, parentY - lift)
        lineTo(arrowX, parentY + (6f - lift))
    }
}

private fun calculatePathSegment(path: Path, pathCompletion: Float): Path {
    // Create a PathMeasure instance for the given path
    val pathMeasure = PathMeasure().apply {
        setPath(path, false)
    }

    // Create a new Path to store the segment
    val pathSegment = Path()

    // Calculate the distance to start drawing the path segment
    val startDistance = 0f

    // Calculate the distance to stop drawing the path segment
    val stopDistance = pathCompletion * pathMeasure.length


    // Retrieve the segment of the path based on start and stop distances
    pathMeasure.getSegment(startDistance, stopDistance, pathSegment, true)
    return pathSegment
}