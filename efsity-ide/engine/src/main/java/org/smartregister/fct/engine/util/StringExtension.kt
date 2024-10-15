package org.smartregister.fct.engine.util

import org.apache.commons.text.CaseUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun uuid(): String {
    return UUID.randomUUID().toString()
}

fun String.compress(): String {
    val byteStream = ByteArrayOutputStream()
    GZIPOutputStream(byteStream).bufferedWriter().use { it.write(this) }
    return Base64.getEncoder().encodeToString(byteStream.toByteArray())
}

fun String.decompress(): String {
    val compressedBytes = Base64.getDecoder().decode(this)
    val byteArrayInputStream = ByteArrayInputStream(compressedBytes)
    return GZIPInputStream(byteArrayInputStream).bufferedReader().use { it.readText() }
}

fun String.replaceLast(oldValue: String, newValue: String): String {
    val lastIndex = lastIndexOf(oldValue)
    if (lastIndex == -1) {
        return this
    }
    val prefix = substring(0, lastIndex)
    val suffix = substring(lastIndex + oldValue.length)
    return "$prefix$newValue$suffix"
}

/**
 * Wrapper method around the Java text formatter
 *
 * Example string format: Name {0} {1}, Age {2}
 *
 * @param locale this is the Locale to use e.g. Locale.ENGLISH
 * @param arguments this is a variable number of values to replace placeholders in order
 * @return the interpolated string with the placeholder variables replaced with the arguments
 *   values.
 *
 * In the example above, the result for passing arguments John, Doe, 35 would be: Name John Doe, Age
 * 35
 */
fun String.messageFormat(locale: Locale?, vararg arguments: Any?): String? =
    MessageFormat(this, locale).format(arguments)

/**
 * This property returns the substring of the filepath after the last period '.' which is the
 * extension
 *
 * e.g /file/path/to/strings.txt would return txt
 */
val String.fileExtension
    get() = this.substringAfterLast('.')

/** Function that converts snake_case string to camelCase */
fun String.camelCase(): String = CaseUtils.toCamelCase(this, false, '_')

/** Remove double white spaces from text and also remove space before comma */
fun String.removeExtraWhiteSpaces(): String =
    this.replace("\\s+".toRegex(), " ").replace(" ,", ",").trim()

/** Return an abbreviation for the provided string */
fun String?.abbreviate() = this?.firstOrNull() ?: ""

fun String.parseDate(pattern: String): Date? =
    SimpleDateFormat(pattern, Locale.ENGLISH).tryParse(this)

/** Compare characters of identical strings */
fun String.compare(anotherString: String): Boolean =
    this.toSortedSet().containsAll(anotherString.toSortedSet())

//fun String.lastOffset() = this.uppercase() + "_" + SharedPreferenceKey.LAST_OFFSET.name

fun String.spaceByUppercase() =
    this.split(Regex("(?=\\p{Upper})")).joinToString(separator = " ").trim()

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
