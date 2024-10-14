package org.smartregister.fct.device_database.domain.model

import kotlinx.serialization.Serializable
import org.smartregister.fct.engine.util.encodeJson

@Serializable
data class QueryRequest(
    val database: String,
    val query: String,
    val sortColumn: String? = null,
    val offset: Int = 0,
    val limit: Int = 50,
) {
    fun asJSONString() = this.encodeJson()
}
