package org.smartregister.fct.apiclient.domain.model

import org.smartregister.fct.engine.domain.model.HttpMethodType
import org.smartregister.fct.engine.domain.model.ServerConfig

data class Request(
    val config: ServerConfig,
    val methodType: HttpMethodType,
    val resourceType: String,
    val resourceId: String,
    val body: String?
)
