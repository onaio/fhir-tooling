package org.smartregister.fct.engine.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PackageInfo(
    val packageId: String,
    val id: String? = null,
    val name: String? = null
)