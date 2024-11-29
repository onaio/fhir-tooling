package org.smartregister.fct.apiclient.domain.model

data class UploadResourceRequest(
    val url: String,
    val accessToken: String,
    val resourceType: String,
    val id: String,
    val body: String
)
