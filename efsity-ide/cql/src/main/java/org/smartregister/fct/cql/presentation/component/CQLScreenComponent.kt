package org.smartregister.fct.cql.presentation.component

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.KoinComponent
import org.smartregister.fct.common.presentation.component.ScreenComponent


class CQLScreenComponent(private val componentContext: ComponentContext) : ScreenComponent,
    KoinComponent, ComponentContext by componentContext {

    override fun onDestroy() {
        super.onDestroy()
    }

}