package org.smartregister.fct.serverconfig.domain.model

import org.smartregister.fct.apiclient.domain.model.AuthResponse

internal sealed class VerifyConfigState {
    data object Idle : VerifyConfigState()
    data object Authenticating : VerifyConfigState()
    data class Result(val authResponse: AuthResponse) : VerifyConfigState()
}