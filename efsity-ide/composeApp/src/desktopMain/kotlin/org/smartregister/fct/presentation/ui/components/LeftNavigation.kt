package org.smartregister.fct.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import org.koin.compose.koinInject
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Cyclone
import org.smartregister.fct.aurora.auroraiconpack.Database
import org.smartregister.fct.aurora.auroraiconpack.DesignServices
import org.smartregister.fct.aurora.auroraiconpack.Folder
import org.smartregister.fct.aurora.auroraiconpack.LocalFireDepartment
import org.smartregister.fct.aurora.auroraiconpack.MoveDown
import org.smartregister.fct.aurora.auroraiconpack.Token
import org.smartregister.fct.aurora.auroraiconpack.Widgets
import org.smartregister.fct.aurora.presentation.ui.components.Icon
import org.smartregister.fct.aurora.presentation.ui.components.Tooltip
import org.smartregister.fct.aurora.presentation.ui.components.TooltipPosition
import org.smartregister.fct.common.domain.model.Config
import org.smartregister.fct.common.presentation.component.RootComponent
import org.smartregister.fct.engine.data.manager.AppSettingManager

context (ConstraintLayoutScope)
@Composable
fun LeftNavigation(
    rootComponent: RootComponent,
    leftNav: ConstrainedLayoutReference
) {
    Row(
        modifier = Modifier
            .width(60.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .constrainAs(leftNav) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }
    ) {
        NavigationBar(rootComponent)
        VerticalDivider()
    }
}

@Composable
private fun NavigationBar(rootComponent: RootComponent) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            var selectedNav by remember { mutableStateOf(0) }
            Spacer(Modifier.height(12.dp))
            navigationMenu().forEachIndexed { index, navButton ->
                Tooltip(
                    tooltip = navButton.title,
                    delayMillis = 100,
                    tooltipPosition = TooltipPosition.Right(),
                ) {
                    IconButton(
                        enabled = selectedNav != index, onClick = {
                            navButton.onClick(rootComponent)
                            selectedNav = index
                        }, colors = if (selectedNav == index) IconButtonDefaults.iconButtonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ) else IconButtonDefaults.iconButtonColors()
                    ) {
                        Icon(icon = navButton.icon)

                    }
                }
            }
        }
        ThemeChangerButton()
    }
}

@Composable
private fun ThemeChangerButton() {
    val appSettingManager = koinInject<AppSettingManager>()
    val appSetting = appSettingManager.appSetting

    Tooltip(
        tooltip = if (appSetting.isDarkTheme) "Light Mode" else "Dark Mode",
        delayMillis = 100,
        tooltipPosition = TooltipPosition.Right(),
    ) {
        IconButton(onClick = {
            appSetting.isDarkTheme = !appSetting.isDarkTheme
            appSettingManager.update()
        }) {
            val icon =
                if (appSetting.isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode
            Icon(icon = icon)
        }
    }

}


private fun navigationMenu(): List<NavigationButton> {

    return listOf(
        NavigationButton(title = "Dashboard",
            icon = AuroraIconPack.Widgets,
            onClick = {
                it.changeSlot(Config.Dashboard)
            }),
        NavigationButton(title = "StructureMap Transformation",
            icon = AuroraIconPack.MoveDown,
            onClick = {
                it.changeSlot(Config.StructureMap)
            }),
        NavigationButton(title = "Workflow",
            icon = AuroraIconPack.Cyclone,
            onClick = {
                it.changeSlot(Config.Workflow)
            }),
        NavigationButton(title = "CQL Transformation",
            icon = AuroraIconPack.Token,
            onClick = {
                it.changeSlot(Config.CQL)
            }),
        NavigationButton(title = "File Manager", icon = AuroraIconPack.Folder, onClick = {
            it.changeSlot(Config.FileManager)
        }),
        NavigationButton(title = "Database",
            icon = AuroraIconPack.Database,
            onClick = {
                it.changeSlot(Config.DeviceDatabase)
            }),
        NavigationButton(title = "Fhirman",
            icon = AuroraIconPack.LocalFireDepartment,
            onClick = {
                it.changeSlot(Config.Fhirman)
            }),
        NavigationButton(title = "Rule Designer",
            icon = AuroraIconPack.DesignServices,
            onClick = {
                it.changeSlot(Config.Rules)
            }),
    )
}

data class NavigationButton(
    val title: String,
    val icon: ImageVector,
    val onClick: (rootComponent: RootComponent) -> Unit
)