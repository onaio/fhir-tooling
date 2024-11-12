package org.smartregister.fct.apiclient.domain.model

data class AuthRequest(
    val oAuthUrl: String,
    val clientId: String,
    val clientSecret: String,
    val username: String,
    val password: String
)
