package org.smartregister.fct.fhirman.util

import org.smartregister.fct.engine.domain.model.HttpMethodType
import org.smartregister.fct.engine.domain.model.ServerConfig

internal object FhirmanConfig {
    var requestText = ""
    var responseText = ""
    var selectedConfig: ServerConfig? = null
    var methodType: HttpMethodType = HttpMethodType.Get
    var resourceType = ""
    var resourceId = ""
    var responseStatus = ""

    fun reset() {
        requestText = ""
        responseText = ""
        selectedConfig = null
        methodType = HttpMethodType.Get
        resourceType = ""
        resourceId = ""
        responseStatus = ""
    }
}