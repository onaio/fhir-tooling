package org.smartregister.fct.engine.util

import com.google.gson.FormattingStyle
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.smartregister.fct.engine.data.manager.AppSettingManager

val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    isLenient = true
    useAlternativeNames = true
    prettyPrint = true
    explicitNulls = false
}

/**
 * Decode string to an entity of type [T]
 *
 * @param jsonInstance the custom json object used to decode the specified string
 * @return the decoded object of type [T]
 */
inline fun <reified T> String.decodeJson(jsonInstance: Json? = null): T =
    jsonInstance?.decodeFromString(this) ?: json.decodeFromString(this)

/**
 * Decode string to an entity of type [T] or return null if json is invalid
 *
 * @param jsonInstance the custom json object used to decode the specified string
 * @return the decoded object of type [T]
 */
inline fun <reified T> String.tryDecodeJson(jsonInstance: Json? = null): T? =
    kotlin.runCatching { this.decodeJson<T>(jsonInstance) }.getOrNull()

/**
 * Encode the type [T] into a Json string
 *
 * @param jsonInstance the custom json object used to decode the specified string
 * @return the encoded json string from given type [T]
 */
inline fun <reified T> T.encodeJson(jsonInstance: Json? = null): String =
    jsonInstance?.encodeToString(this) ?: json.encodeToString(this)

fun String.prettyJson(indent: Int? = null): String {

    val appSetting = getKoinInstance<AppSettingManager>().appSetting
    val tabIndent = indent ?: appSetting.codeEditorConfig.indent

    val gson = GsonBuilder()
        .disableHtmlEscaping()
        .setFormattingStyle(
            FormattingStyle.PRETTY.withIndent(
                " ".repeat(
                    tabIndent
                )
            )
        )
        .create()
    val jsonParser = JsonParser.parseString(this)
    return gson.toJson(jsonParser)
}

fun String.compactJson(): String {

    val appSetting = getKoinInstance<AppSettingManager>().appSetting

    val gson = GsonBuilder()
        .disableHtmlEscaping()
        .setFormattingStyle(
            FormattingStyle.COMPACT
        )
        .create()
    val jsonParser = JsonParser.parseString(this)
    return gson.toJson(jsonParser)
}