package org.smartregister.fct.sm.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import org.smartregister.fct.common.data.controller.SingleFieldDialogController
import org.smartregister.fct.sm.presentation.component.StructureMapScreenComponent

@Composable
internal fun MainContainer(
    component: StructureMapScreenComponent,
    newWorkflowDialog: SingleFieldDialogController
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Toolbar(component, newWorkflowDialog)
        Row(Modifier.fillMaxSize()) {

            val openPath by component.openPath.collectAsState()

            SourceFiles(component)

            if (openPath == component.activeStructureMap.value!!.mapPath) {
                ConstraintLayout(Modifier.fillMaxSize()) {
                    val (editorRef, insightRef) = createRefs()

                    Box(Modifier.constrainAs(editorRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(insightRef.start)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }) {
                        SourceEditor(component)
                    }

                    Box(Modifier.constrainAs(insightRef) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }) {
                        MapInsights(component)
                    }
                }
            } else {
                SourceEditor(component)
            }
        }

    }
}

