package org.smartregister.fct.rules.domain.model

import kotlinx.serialization.Serializable

@Serializable
internal data class RequestBundle(
    val dataSources: List<DataSource>,
    val rules: List<Rule>
)
