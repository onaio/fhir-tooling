package org.smartregister.structuremaptool.extensions

import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Type
import org.smartregister.structuremaptool.*
import org.smartregister.structuremaptool.fhirPathEngine

fun String.addIndentation(): String {
  var currLevel = 0
  val lines = split("\n")

  val sb = StringBuilder()
  lines.forEach { line ->
    if (line.endsWith("{")) {
      sb.append(line.addIndentation(currLevel))
      sb.appendNewLine()
      currLevel++
    } else if (line.startsWith("}")) {
      currLevel--
      sb.append(line.addIndentation(currLevel))
      sb.appendNewLine()
    } else {
      sb.append(line.addIndentation(currLevel))
      sb.appendNewLine()
    }
  }
  return sb.toString()
}

fun String.addIndentation(times: Int): String {
  var processedString = ""
  for (k in 1..times) {
    processedString += "\t"
  }

  processedString += this
  return processedString
}

fun String.clean(): String {
  return this.replace("-", "").replace("_", "").replace(" ", "")
}

fun String.isCoding(questionnaireResponse: QuestionnaireResponse): Boolean {
  val answerType = getType(questionnaireResponse)
  return if (answerType != null) {
    answerType == "org.hl7.fhir.r4.model.Coding"
  } else {
    false
  }
}

fun String.getType(questionnaireResponse: QuestionnaireResponse): String? {
  val answer = fhirPathEngine.evaluate(questionnaireResponse, this)

  return answer.firstOrNull()?.javaClass?.name
}

fun String.getAnswerType(questionnaireResponse: QuestionnaireResponse): String? {
  return if (isEvaluateExpression()) {
    val fhirPath = substring(indexOf(",") + 1, length - 1)

    fhirPath.getType(questionnaireResponse)?.replace("org.hl7.fhir.r4.model.", "")
  } else {
    // TODO: WE can run the actual line against StructureMapUtilities.runTransform to get the actual
    // one that is generated and confirm if we need more conversions
    "StringType"
  }
}

fun String.isEnumeration(instruction: Instruction): Boolean {
  return inferType(instruction.fullPropertyPath())?.contains("Enumeration") ?: false
}

fun String.isEvaluateExpression(): Boolean = startsWith("evaluate(")

fun String.isMultipleTypes(): Boolean = this == "Type"

// TODO: Finish this. Use the annotation @Chid.type
fun String.getPossibleTypes(): List<Type> {
  return listOf()
}

fun String.canHandleConversion(sourceType: String): Boolean {
  val propertyClass = Class.forName("org.hl7.fhir.r4.model.$this")
  val targetType2 =
    if (sourceType == "StringType") String::class.java
    else Class.forName("org.hl7.fhir.r4.model.$sourceType")

  val possibleConversions =
    listOf(
      "BooleanType" to "StringType",
      "DateType" to "StringType",
      "DecimalType" to "IntegerType",
      "AdministrativeGender" to "CodeType"
    )

  possibleConversions.forEach {
    if (this.contains(it.first) && sourceType == it.second) {
      return true
    }
  }

  try {
    propertyClass.getDeclaredMethod("fromCode", targetType2)
  } catch (ex: NoSuchMethodException) {
    return false
  }

  return true
}

fun String.getParentResource(): String? {
  return substring(0, lastIndexOf('.'))
}

fun String.getResourceProperty(): String? {
  return substring(lastIndexOf('.') + 1)
}

fun String.getFhirType(): String = replace("Type", "").lowercase()
