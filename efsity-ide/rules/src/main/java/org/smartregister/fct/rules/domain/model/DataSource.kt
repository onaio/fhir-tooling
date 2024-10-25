package org.smartregister.fct.rules.domain.model

import kotlinx.serialization.Serializable

@Serializable
internal data class DataSource(
    val id: String,
    val query: String,
    val resourceType: String,
    val isSingle: Boolean
) : java.io.Serializable
