package org.smartregister.fct.common.presentation.component

import com.arkivanov.decompose.ComponentContext
import org.smartregister.fct.common.domain.model.Config

abstract class RootComponent(
    componentContext: ComponentContext
) : SlotComponent<ScreenComponent, Config>, ComponentContext by componentContext