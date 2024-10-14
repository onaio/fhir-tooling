package org.smartregister.fct.settings.domain.model

import androidx.compose.ui.graphics.vector.ImageVector
import org.smartregister.fct.aurora.AuroraIconPack
import org.smartregister.fct.aurora.auroraiconpack.Dns
import org.smartregister.fct.aurora.auroraiconpack.Subtitles

sealed class Setting(val label: String, val icon: ImageVector) {
    data object ServerConfigs : Setting("Server Configs", AuroraIconPack.Dns)
    data object CodeEditor : Setting("Code Editor", AuroraIconPack.Subtitles)
}