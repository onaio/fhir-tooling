package org.smartregister.fct.apiclient.data.deserializer

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.ResourceType
import org.smartregister.fct.apiclient.util.asOperationOutcome
import org.smartregister.fct.logger.FCTLogger
import java.lang.reflect.Type

internal class OperationOutcomeTypeAdapter : JsonSerializer<OperationOutcome>,
    JsonDeserializer<OperationOutcome> {

    override fun serialize(
        src: OperationOutcome?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {

        val jsonArray = JsonArray()

        src?.issue?.forEach {
            jsonArray.add(JsonObject().apply {
                addProperty("severity", it.severity.toCode())
                addProperty("code", it.code.toCode())
                addProperty("diagnostics", it.diagnostics)
            })
        }


        val jsonObject = JsonObject()
        jsonObject.addProperty("resourceType", ResourceType.OperationOutcome.name)
        jsonObject.add("issue", jsonArray)

        return jsonObject
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): OperationOutcome {

        return try {
            val jsonObject: JsonObject = json!!.asJsonObject

            OperationOutcome().apply {

                jsonObject
                    .getAsJsonArray("issue")
                    .map {
                        OperationOutcome.OperationOutcomeIssueComponent().apply {
                            severity =
                                OperationOutcome.IssueSeverity.fromCode(it.asJsonObject.get("severity").asString)
                            code =
                                OperationOutcome.IssueType.fromCode(it.asJsonObject.get("code").asString)
                            diagnostics = it.asJsonObject.get("diagnostics").asString
                        }
                    }
                    .onEach(::addIssue)
            }

        } catch (ex: Exception) {
            FCTLogger.e(ex)
            ex.asOperationOutcome()
        }
    }
}