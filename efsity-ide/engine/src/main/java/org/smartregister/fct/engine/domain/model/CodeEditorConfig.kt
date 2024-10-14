package org.smartregister.fct.engine.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CodeEditorConfig(
    var indent: Int = 4
)