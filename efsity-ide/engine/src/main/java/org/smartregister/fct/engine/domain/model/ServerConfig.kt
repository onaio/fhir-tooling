package org.smartregister.fct.engine.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ServerConfig(
    val id: String,
    val title: String,
    val fhirBaseUrl: String = "",
    val oAuthUrl: String = "",
    val clientId: String = "",
    val clientSecret: String = "",
    val username: String = "",
    val password: String = "",
    var authToken: String = "",
)