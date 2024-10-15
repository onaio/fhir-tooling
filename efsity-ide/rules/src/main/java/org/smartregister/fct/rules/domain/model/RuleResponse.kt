package org.smartregister.fct.rules.domain.model

import kotlinx.serialization.Serializable

@Serializable
internal data class RuleResponse(
    var error: String?,
    val result: Map<String, String> = mutableMapOf()
)