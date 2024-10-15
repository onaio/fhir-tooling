package org.smartregister.fct.common.domain.model

sealed class TabType {
    data object Scrollable : TabType()
    data object Filled : TabType()
}