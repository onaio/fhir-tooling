package org.smartregister.fct.insights.domain.model

import kotlinx.serialization.Serializable

@Serializable
internal data class UserInfo(
    val questionnairePublisher: String? = null,
    val organization: String? = null,
    val location: String? = null,
    val familyName: String? = null,
    val givenName: String? = null,
    val name: String? = null,
    val preferredUsername: String? = null,
    val keycloakUuid: String? = null,
    val appId: String? = null,
)