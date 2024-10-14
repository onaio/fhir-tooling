package org.smartregister.fct.apiclient.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class AuthResponse {

    @Serializable
    data class Success(
        @SerialName( "access_token")
        val accessToken: String,

        @SerialName("expires_in")
        val expiresIn: Long,

        @SerialName("refresh_expires_in")
        val refreshExpiresIn: Long,

        @SerialName("refresh_token")
        val refreshToken: String,

        @SerialName("token_type")
        val tokenType: String,

        @SerialName("id_token")
        val idToken: String,

        @SerialName("not-before-policy")
        val notBeforePolicy: Int,

        @SerialName("session_state")
        val sessionState: String,

        val scope: String,
    ) : AuthResponse()

    @Serializable
    data class Failed(

        val error: String,

        @SerialName("error_description")
        val description: String? = null,
    ) : AuthResponse()

    @kotlinx.serialization.Transient
    var httpStatusCode: Int = -1

    @kotlinx.serialization.Transient
    var httpStatus: String = "Unknown Status Code"
}

