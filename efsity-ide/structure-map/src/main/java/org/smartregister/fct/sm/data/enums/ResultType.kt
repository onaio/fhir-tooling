package org.smartregister.fct.sm.data.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.ui.graphics.vector.ImageVector

internal enum class ResultType(val label: String, val icon: ImageVector) {
    Json("JSON", Icons.Outlined.DataObject),
    Tree("Tree", Icons.Outlined.AccountTree)
}

internal val ResultType.inverse: ResultType get() {
    return if (this == ResultType.Json) ResultType.Tree else ResultType.Json
}