package org.smartregister.fct.engine.domain.model

sealed class HttpMethodType(val name: String) {

    data object Get : HttpMethodType("GET")
    data object Post : HttpMethodType("POST")
    data object Put : HttpMethodType("PUT")
    data object Delete : HttpMethodType("DELETE")
}