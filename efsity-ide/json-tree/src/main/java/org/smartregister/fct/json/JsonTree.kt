package org.smartregister.fct.json

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import org.smartregister.fct.json.node.Branch
import org.smartregister.fct.json.node.Leaf
import org.smartregister.fct.json.tree.Tree
import org.smartregister.fct.json.tree.TreeScope

fun JsonStyle(colorScheme: ColorScheme): FCTJsonStyle<JsonElement> =
    FCTJsonStyle(
        colorScheme = colorScheme,
        nodeNameTextStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = colorScheme.onBackground
        )
    )

@Composable
fun JsonTree(
    json: String,
    key: Any? = null,
): Tree<JsonElement> =
    Tree(key = key) {
        JsonNode(
            key = null,
            jsonElement = Json.parseToJsonElement(json)
        )
    }

@Composable
private fun TreeScope.JsonNode(
    key: String?,
    jsonElement: JsonElement
) {
    when (jsonElement) {
        is JsonNull -> JsonPrimitiveNode(key, jsonElement)
        is JsonPrimitive -> JsonPrimitiveNode(key, jsonElement)
        is JsonObject -> JsonObjectNode(key, jsonElement)
        is JsonArray -> JsonArrayNode(key, jsonElement)
    }
}

@Composable
private fun TreeScope.JsonPrimitiveNode(
    key: String?,
    jsonPrimitive: JsonPrimitive
) {
    Leaf(
        content = jsonPrimitive,
        name = "${getFormattedKey(key)}${getFormattedValue(jsonPrimitive)}",
    )
}

@Composable
private fun TreeScope.JsonObjectNode(
    key: String?,
    jsonObject: JsonObject
) {
    Branch(
        content = jsonObject,
        name = "${getFormattedKey(key)}{object}"
    ) {
        jsonObject.entries.forEach { (key, jsonElement) ->
            JsonNode(key, jsonElement)
        }
    }
}

@Composable
private fun TreeScope.JsonArrayNode(
    key: String?,
    jsonArray: JsonArray
) {
    Branch(
        content = jsonArray,
        name = "${getFormattedKey(key)}[array]"
    ) {
        jsonArray.forEachIndexed { index, jsonElement ->
            JsonNode(index.toString(), jsonElement)
        }
    }
}

private fun getFormattedKey(key: String?) =
    if (key.isNullOrBlank()) ""
    else "$key: "

private fun getFormattedValue(jsonPrimitive: JsonPrimitive) =
    if (jsonPrimitive.isString) "\"${jsonPrimitive.contentOrNull}\""
    else jsonPrimitive.contentOrNull
