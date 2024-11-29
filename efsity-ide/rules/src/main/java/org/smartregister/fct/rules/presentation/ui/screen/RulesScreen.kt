package org.smartregister.fct.rules.presentation.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import org.smartregister.fct.aurora.presentation.ui.components.LinearIndicator
import org.smartregister.fct.common.presentation.ui.container.Aurora
import org.smartregister.fct.rules.presentation.components.RulesScreenComponent
import org.smartregister.fct.rules.presentation.ui.components.Board
import org.smartregister.fct.rules.presentation.ui.components.BoardScaleSlider
import org.smartregister.fct.rules.presentation.ui.components.CenterBoardButton
import org.smartregister.fct.rules.presentation.ui.components.CreateDataSourceButton
import org.smartregister.fct.rules.presentation.ui.components.CreateNewWorkspaceButton
import org.smartregister.fct.rules.presentation.ui.components.CreateRuleButton
import org.smartregister.fct.rules.presentation.ui.components.DataSourceWidget
import org.smartregister.fct.rules.presentation.ui.components.ExecuteRulesButton
import org.smartregister.fct.rules.presentation.ui.components.ImportExportButton
import org.smartregister.fct.rules.presentation.ui.components.RuleWidget
import org.smartregister.fct.rules.presentation.ui.components.RulesEngineMethodList
import org.smartregister.fct.rules.presentation.ui.components.RulesList
import org.smartregister.fct.rules.presentation.ui.components.SaveWorkspaceButton
import org.smartregister.fct.rules.presentation.ui.components.ShowAllWorkspacesButton
import org.smartregister.fct.rules.presentation.ui.components.ShowAllWorkspacesPanel
import org.smartregister.fct.rules.presentation.ui.components.TogglePathButton

@Composable
fun RulesScreen(component: RulesScreenComponent) {

    var controlAlpha by remember { mutableStateOf(0f) }
    val animatedControlAlpha by animateFloatAsState(controlAlpha)

    LaunchedEffect(Unit) {
        controlAlpha = 1f
    }

    Aurora(component) { auroraManager ->

        LaunchedEffect(Unit) {
            component.error.collectLatest {
                auroraManager.showErrorSnackbar(it)
            }
        }

        LaunchedEffect(Unit) {
            component.info.collectLatest {
                auroraManager.showSnackbar(it)
            }
        }

        Board(component) { boardProperty ->

            DataSourceWidget(
                component = component,
                boardProperty = boardProperty
            )

            RuleWidget(
                component = component,
                boardProperty = boardProperty
            )
        }

        if (component.loading.collectAsState().value) {
            LinearIndicator(Modifier.fillMaxWidth().align(Alignment.TopCenter))
        }

        Box(Modifier.fillMaxSize().alpha(animatedControlAlpha)) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RulesEngineMethodList()
                Spacer(Modifier.width(12.dp))
                CreateNewWorkspaceButton(component)
                Spacer(Modifier.width(12.dp))
                ShowAllWorkspacesButton(component)
                Spacer(Modifier.width(12.dp))
                SaveWorkspaceButton(component)
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CreateRuleButton(component)
                Spacer(Modifier.width(12.dp))
                CreateDataSourceButton(component)
                Spacer(Modifier.width(12.dp))
                RulesList(component)
            }

            CenterBoardButton(component)

            Row(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
            ) {
                TogglePathButton(component)
                Spacer(Modifier.width(12.dp))
                ImportExportButton(component)
            }

            ExecuteRulesButton(component)
            BoardScaleSlider(component)
        }

        ShowAllWorkspacesPanel(component)
    }

}








