package org.smartregister.fhir.structuremaptool

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.apache.poi.ss.usermodel.Row
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.utils.FHIRPathEngine
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

// Get the hl7 resources
val contextR4 = FhirContext.forR4()
val fhirResources = contextR4.resourceTypes
fun getQuestionsPath(questionnaire: Questionnaire): HashMap<String, String> {
    val questionsMap = hashMapOf<String, String>()

    questionnaire.item.forEach { itemComponent ->
        getQuestionNames("", itemComponent, questionsMap)
    }
    return questionsMap
}

fun getQuestionNames(parentName: String, item: QuestionnaireItemComponent, questionsMap: HashMap<String, String>) {
    val currParentName = if (parentName.isEmpty()) "" else parentName
    questionsMap.put(item.linkId, currParentName)

    item.item.forEach { itemComponent ->
        getQuestionNames(currParentName + ".where(linkId = '${item.linkId}').item", itemComponent, questionsMap)
    }
}


class Group(
    entry: Map.Entry<String, MutableList<Instruction>>,
    val stringBuilder: StringBuilder,
    val questionsPath: HashMap<String, String>
) {

    var lineCounter = 0
    var groupName = entry.key
    val instructions = entry.value

    private fun generateReference(resourceName: String, resourceIndex: String): String {
        // Generate the reference based on the resourceName and resourceIndex
        val sb = StringBuilder()
        sb.append("create('Reference') as reference then {")
        sb.appendNewLine()
        sb.append("src-> reference.reference = evaluate(bundle, \$this.entry.where(resourceType = '$resourceName/$resourceIndex'))")
        sb.append(""" "rule_d";""".trimMargin())
        sb.appendNewLine()
        sb.append("}")
        return sb.toString()
    }

    fun generateGroup(questionnaireResponse: QuestionnaireResponse) {
        if(fhirResources.contains(groupName.dropLast(1))){
            val resourceName = instructions[0].resource

            // add target of reference to function if reference is not null
            val structureMapFunctionHead = "group Extract$groupName(source src : QuestionniareResponse, target bundle: Bundle) {"
            stringBuilder.appendNewLine()
            stringBuilder.append(structureMapFunctionHead)
                .appendNewLine()
            stringBuilder.append("src -> bundle.entry as  entry, entry.resource = create('$resourceName') as entity1 then {")
                .appendNewLine()

            val mainNest = Nest()
            mainNest.fullPath = ""
            mainNest.name = ""
            mainNest.resourceName = resourceName

            instructions.forEachIndexed { index, instruction ->
                mainNest.add(instruction)
            }

            mainNest.buildStructureMap(0, questionnaireResponse)


            stringBuilder.append("} ")
            addRuleNo()
            stringBuilder.appendNewLine()
            stringBuilder.append("}")
            stringBuilder.appendNewLine()
            stringBuilder.appendNewLine()
        } else{
            println("$groupName is not a valid hl7 resource name")
        }
    }

    fun addRuleNo() {
        stringBuilder.append(""" "${groupName}_${lineCounter++}"; """)
    }

    fun Instruction.getPropertyPath(): String {
        return questionsPath.getOrDefault(responseFieldId, "")
    }

    fun Instruction.getAnswerExpression(questionnaireResponse: QuestionnaireResponse): String {

        //1. If the answer is static/literal, just return it here
        // TODO: We should infer the resource element and add the correct conversion or code to assign this correctly
        if (constantValue != null) {
            return when {
                fieldPath == "id" -> "create('id') as id, id.value = '$constantValue'"
                fieldPath == "rank" -> {
                    val constValue = constantValue!!.replace(".0", "")
                    "create('positiveInt') as rank, rank.value = '$constValue'"
                }
                else -> "'$constantValue'"
            }
        }

        // 2. If the answer is from the QuestionnaireResponse, get the ID of the item in the "Questionnaire Response Field Id" and
        // get its value using FHIR Path expressions
        if (responseFieldId != null) {
            // TODO: Fix the 1st param inside the evaluate expression
            var expression = "${"$"}this.item${getPropertyPath()}.where(linkId = '$responseFieldId').answer.value"
            // TODO: Fix these to use infer
            if (fieldPath == "id" || fieldPath == "rank") {
                expression = "create('${if (fieldPath == "id") "id" else "positiveInt"}') as $fieldPath, $fieldPath.value = evaluate(src, $expression)"
            } else {

                // TODO: Infer the resource property type and answer to perform other conversions
                // TODO: Extend this to cover other corner cases
                if (expression.isCoding(questionnaireResponse) && fieldPath.isEnumeration(this)) {
                    expression = expression.replace("answer.value", "answer.value.code")
                } else if (inferType(fullPropertyPath()) == "CodeableConcept") {
                    return "''"
                }
                expression = "evaluate(src, $expression)"
            }
            return expression
        }

        // 3. If it's a FHIR Path/StructureMap function, add the contents directly from here to the StructureMap
        if (fhirPathStructureMapFunctions != null && fhirPathStructureMapFunctions!!.isNotEmpty()) {
            // TODO: Fix the 2nd param inside the evaluate expression --> Not sure what this is but check this
            return fhirPathStructureMapFunctions!!
        }
        // 4. If the answer is a conversion, (Assume this means it's being converted to a reference)
        if (conversion != null && conversion!!.isNotBlank() && conversion!!.isNotEmpty()) {
            println("current resource to reference is $conversion")

            val resourceName = conversion!!.replace("$", "")
            var resourceIndex = conversion!!.replace("$$resourceName", "")
            if (resourceIndex.isNotEmpty()) {
                resourceIndex = "[$resourceIndex]"
            }
            val reference = generateReference(resourceName = resourceName, resourceIndex = resourceIndex)
            return reference
        }

        /*
        5. You can use $Resource eg $Patient to reference another resource being extracted here,
        but how do we actually get its instance so that we can use it???? - This should be handled elsewhere
         */

        return "''"
    }



    inner class Nest {
        var instruction: Instruction? = null

        // We can change this to a linked list
        val nests = ArrayList<Nest>()
        lateinit var name: String
        lateinit var fullPath: String
        lateinit var resourceName: String

        fun add(instruction: Instruction) {
            /*if (instruction.fieldPath.startsWith(fullPath)) {

            }*/
            val remainingPath = instruction.fieldPath.replace(fullPath, "")

            remainingPath.run {
                if (contains(".")) {
                    val parts = split(".")
                    val partName = parts[0].ifEmpty {
                        parts[1]
                    }

                    // Search for the correct property to put this nested property
                    nests.forEach {
                        if (partName.startsWith(it.name)) {
                            val nextInstruction = Instruction().apply {
                                copyFrom(instruction)
                                var newFieldPath = ""
                                parts.forEachIndexed { index, s ->
                                    if (index != 0) {
                                        newFieldPath += s
                                    }

                                    if (index > 0 && index < parts.size - 1) {
                                        newFieldPath += "."
                                    }
                                }

                                fieldPath = newFieldPath
                            }

                            it.add(nextInstruction)

                            return@run
                        }
                    }

                    // If no match is found, let's create a new one
                    val newNest = Nest().apply {
                        name = partName

                        fullPath = if (this@Nest.fullPath.isNotEmpty()) {
                            "${this@Nest.fullPath}.$partName"
                        } else {
                            partName
                        }
                        resourceName = inferType("${this@Nest.resourceName}.$fullPath") ?: ""

                        if ((parts[0].isEmpty() && parts.size > 2) || (parts[0].isNotEmpty() && parts.size > 1)) {
                            val nextInstruction = Instruction().apply {
                                copyFrom(instruction)
                                var newFieldPath = ""
                                parts.forEachIndexed { index, s ->
                                    if (index != 0) {
                                        newFieldPath += s
                                    }
                                }

                                fieldPath = newFieldPath
                            }
                            add(nextInstruction)
                        } else {
                            this@apply.instruction = instruction
                        }
                    }
                    nests.add(newNest)
                } else {
                    this@Nest.nests.add(Nest().apply {
                        name = remainingPath
                        fullPath = instruction.fieldPath
                        this@apply.instruction = instruction
                        resourceName = inferType("${this@Nest.resourceName}.$fullPath") ?: ""
                    })
                }
            }
        }

        fun buildStructureMap(currLevel: Int, questionnaireResponse: QuestionnaireResponse) {
            if (instruction != null) {
                val answerExpression = instruction?.getAnswerExpression(questionnaireResponse)

                if (answerExpression != null) {
                    if (answerExpression.isNotEmpty() && answerExpression.isNotBlank() && answerExpression != "''") {
                        val propertyType = inferType(instruction!!.fullPropertyPath())
                        val answerType = answerExpression.getAnswerType(questionnaireResponse)

                        if (propertyType != "Type" && answerType != propertyType && propertyType?.canHandleConversion(
                                answerType ?: ""
                            )?.not() == true && answerExpression.startsWith("evaluate")
                        ) {
                            println("Failed type matching --> ${instruction!!.fullPropertyPath()} of type $answerType != $propertyType")
                            stringBuilder.append("src -> entity$currLevel.${instruction!!.fieldPath} = ")
                            stringBuilder.append("create('${propertyType.getFhirType()}') as randomVal, randomVal.value = ")
                            stringBuilder.append(answerExpression)
                            addRuleNo()
                            stringBuilder.appendNewLine()
                            return
                        }

                        stringBuilder.append("src -> entity$currLevel.${instruction!!.fieldPath} = ")
                        stringBuilder.append(answerExpression)
                        addRuleNo()
                        stringBuilder.appendNewLine()
                    }
                }
            } else if (nests.size > 0) {
                //val resourceType = inferType("entity$currLevel.$name", instruction)

                if (!name.equals("")) {
                    val resourceType = resourceName
                    stringBuilder.append("src -> entity$currLevel.$name = create('$resourceType') as entity${currLevel + 1} then {")
                    stringBuilder.appendNewLine()
                } else {
                    //stringBuilder.append("src -> entity$currLevel.$name = create('$resourceType') as entity${currLevel + 1} then {")

                }

                nests.forEach {
                    it.buildStructureMap(currLevel + 1, questionnaireResponse)
                }

                //nest!!.buildStructureMap(currLevel + 1)

                if (!name.equals("")) {
                    stringBuilder.append("}")
                    addRuleNo()
                } else {
                    //addRuleNo()
                }
                stringBuilder.appendNewLine()
            } else {
                throw Exception("nest & instruction are null inside Nest object")
            }
        }
    }

}

fun generateStructureMapLine(
    structureMapBody: StringBuilder,
    row: Row,
    resource: Resource,
    extractionResources: HashMap<String, Resource>
) {
    val fieldPath = row.getCell(4)?.stringCellValue ?: ""
    val cellValue = row.getCell(0)?.stringCellValue ?: ""

    // Determine the target FHIR data type
    val targetDataType = determineFhirDataType(cellValue)

    // Generate the mapping line for the StructureMap
    structureMapBody.append("src -> entity.$fieldPath = ")

    // Handle different data types
    when (targetDataType) {
        "string" -> {
            structureMapBody.append("create('string').value = '${cellValue.escapeQuotes()}'")
        }

        "integer" -> {
            structureMapBody.append("create('integer').value = ${cellValue.toIntOrNull() ?: 0}")
        }

        "boolean" -> {
            val booleanValue = if (cellValue.equals("true", ignoreCase = true)) "true" else "false"
            structureMapBody.append("create('boolean').value = $booleanValue")
        }

        "date" -> {
            // Handle date type
            structureMapBody.append("create('date').value = '${cellValue.escapeQuotes()}'")
        }

        "decimal" -> {
            // Handle decimal type
            structureMapBody.append("create('decimal').value = ${cellValue.toDoubleOrNull() ?: 0.0}")
        }

        "code" -> {
            // Handle code type
            structureMapBody.append("create('code').value = '${cellValue.escapeQuotes()}'")
        }

        else -> {
            structureMapBody.append("create('unsupportedDataType').value = '${cellValue.escapeQuotes()}'")
        }
    }
    structureMapBody.appendNewLine()
}

fun String.escapeQuotes(): String {
    return this.replace("'", "\\'")
}

fun determineFhirDataType(input: String?): String {
    if (input.isNullOrEmpty()) {
        return "Invalid Input: Null or Empty String"
    }

    // Clean and prepare the input for matching
    val cleanedValue = input.trim().toLowerCase()

    // Regular Expressions for FHIR Data Types
    val booleanRegex = "^(true|false)\$".toRegex(RegexOption.IGNORE_CASE)
    val integerRegex = "^-?\\d+\$".toRegex()
    val decimalRegex = "^-?\\d*\\.\\d+\$".toRegex()
    val dateRegex = "^\\d{4}-\\d{2}-\\d{2}\$".toRegex() // YYYY-MM-DD
    val dateTimeRegex = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(Z|[+-]\\d{2}:\\d{2})?\$".toRegex() // YYYY-MM-DDThh:mm:ssZ
    val stringRegex = "^[\\w\\s]+\$".toRegex()
    val quantityRegex = "^\\d+\\s?[a-zA-Z]+\$".toRegex() // e.g., "70 kg"
    val codeableConceptRegex = "^[\\w\\s]+\$".toRegex() // Simplified for now
    val codingRegex = "^\\w+\\|\$".toRegex() // Simplified for now
    val referenceRegex = "^\\w+/\\w+\$".toRegex() // e.g., "Patient/123"
    val periodRegex = "^\\d{4}-\\d{2}-\\d{2}\\/\\d{4}-\\d{2}-\\d{2}\$".toRegex() // e.g., "2023-01-01/2023-12-31"
    val timingRegex = "^\\d+[a-zA-Z]+\$".toRegex() // Simplified for now
    val rangeRegex = "^\\d+-\\d+\$".toRegex() // e.g., "10-20"
    val annotationRegex = """^.*\s+\S+""".toRegex() // A basic regex for general text or comments
    val attachmentRegex = """^[A-Za-z0-9+/=]+$""".toRegex() // Base64 encoded string (could be more complex)
    val base64BinaryRegex = """^[A-Za-z0-9+/=]+$""".toRegex() // Similar to attachment, but could have specific markers
    val contactPointRegex = """^\+?[1-9]\d{1,14}$""".toRegex() // Regex for phone numbers (E.164 format)
    val humanNameRegex = """^[A-Za-z\s'-]+$""".toRegex() // Simple regex for names
    val addressRegex = """^\d+\s[A-Za-z]+\s[A-Za-z]+""".toRegex() // Basic address pattern
    val durationRegex = """^\d+\s(hour|minute|second|day)$""".toRegex() // Duration like "1 hour"
    val moneyRegex = """^\d+(\.\d{2})?\s[A-Z]{3}$""".toRegex() // Money format like "100.00 USD"
    val ratioRegex = """^\d+:\d+$""".toRegex() // Simple ratio like "1:1000"
    val signatureRegex = """^[A-Za-z0-9+/=]+$""".toRegex() // Base64 signature
    val identifierRegex = """^[A-Za-z0-9-]+$""".toRegex() // Identifier format
    val uriRegex = """^https?://[^\s/$.?#].[^\s]*$""".toRegex() // Simple URI format
    val uuidRegex = """^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$""".toRegex() // UUID format
    val instantRegex = """^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d+)?Z$""".toRegex() // ISO 8601 instant format
    val narrativeRegex = """<div\s+xmlns="http://www.w3.org/1999/xhtml">.*<\/div>""".toRegex() // Narrative XHTML content
    val sampledDataRegex = """^.*\s+\S+""".toRegex() // Placeholder regex for complex observation data
    val backboneElementRegex = """^.*$""".toRegex() // Catch-all for complex structures (requires specific context)
    // Detect and Return FHIR Data Type
    return when {
        booleanRegex.matches(cleanedValue) -> "Boolean"
        integerRegex.matches(cleanedValue) -> "Integer"
        decimalRegex.matches(cleanedValue) -> "Decimal"
        dateRegex.matches(cleanedValue) -> "Date"
        dateTimeRegex.matches(cleanedValue) -> "DateTime"
        quantityRegex.matches(input) -> "Quantity"
        codeableConceptRegex.matches(input) -> "CodeableConcept"
        codingRegex.matches(input) -> "Coding"
        identifierRegex.matches(input) -> "Identifier"
        referenceRegex.matches(input) -> "Reference"
        periodRegex.matches(input) -> "Period"
        timingRegex.matches(input) -> "Timing"
        rangeRegex.matches(input) -> "Range"
        stringRegex.matches(input) -> "String"
        annotationRegex.matches(input) -> "Annotation"
        attachmentRegex.matches(input) -> "Attachment"
        base64BinaryRegex.matches(input) -> "Base64Binary"
        contactPointRegex.matches(input) -> "ContactPoint"
        humanNameRegex.matches(input) -> "HumanName"
        addressRegex.matches(input) -> "Address"
        durationRegex.matches(input) -> "Duration"
        moneyRegex.matches(input) -> "Money"
        ratioRegex.matches(input) -> "Ratio"
        signatureRegex.matches(input) -> "Signature"
        identifierRegex.matches(input) -> "Identifier"
        uriRegex.matches(input) -> "Uri"
        uuidRegex.matches(input) -> "Uuid"
        instantRegex.matches(input) -> "Instant"
        narrativeRegex.matches(input) -> "Narrative"
        sampledDataRegex.matches(input) -> "SampledData"
        backboneElementRegex.matches(input) -> "BackboneElement"
        else -> "String"
    }
}


fun StringBuilder.appendNewLine(): StringBuilder {
    append(System.lineSeparator())
    return this
}


private val Field.isList: Boolean
    get() = isParameterized && type == List::class.java

private val Field.isParameterized: Boolean
    get() = genericType is ParameterizedType

/** The non-parameterized type of this field (e.g. `String` for a field of type `List<String>`). */
private val Field.nonParameterizedType: Class<*>
    get() =
        if (isParameterized) (genericType as ParameterizedType).actualTypeArguments[0] as Class<*>
        else type

private fun Class<*>.getFieldOrNull(name: String): Field? {
    return try {
        getDeclaredField(name)
    } catch (ex: NoSuchFieldException) {
        superclass?.getFieldOrNull(name)
    }
}

private fun String.isCoding(questionnaireResponse: QuestionnaireResponse): Boolean {
    val answerType = getType(questionnaireResponse)
    return if (answerType != null) {
        answerType == "org.hl7.fhir.r4.model.Coding"
    } else {
        false
    }
}

private fun String.getType(questionnaireResponse: QuestionnaireResponse): String? {
    val answer = fhirPathEngine.evaluate(questionnaireResponse, this)

    return answer.firstOrNull()?.javaClass?.name
}


internal val fhirPathEngine: FHIRPathEngine =
    with(FhirContext.forCached(FhirVersionEnum.R4)) {
        FHIRPathEngine(HapiWorkerContext(this, this.validationSupport)).apply {
            hostServices = FhirPathEngineHostServices
        }
    }

private fun String.isEnumeration(instruction: Instruction): Boolean {
    return inferType(instruction.fullPropertyPath())?.contains("Enumeration") ?: false
}


fun String.getAnswerType(questionnaireResponse: QuestionnaireResponse): String? {
    return if (isEvaluateExpression()) {
        val fhirPath = substring(indexOf(",") + 1, length - 1)

        fhirPath.getType(questionnaireResponse)
            ?.replace("org.hl7.fhir.r4.model.", "")
    } else {
        // TODO: WE can run the actual line against StructureMapUtilities.runTransform to get the actual one that is generated and confirm if we need more conversions
        "StringType";
    }
}

// TODO: Confirm and fix this
fun String.isEvaluateExpression(): Boolean = startsWith("evaluate(")


/**
 * Infer's the type and return the short class name eg `HumanName` for org.fhir.hl7.r4.model.Patient
 * when given the path `Patient.name`
 */
fun inferType(propertyPath: String): String? {
    // TODO: Handle possible errors
    // TODO: Handle inferring nested types
    val parts = propertyPath.split(".")
    val parentResourceClassName = parts[0]
    lateinit var parentClass: Class<*>

    if (fhirResources.contains(parentResourceClassName)) {
        parentClass = Class.forName("org.hl7.fhir.r4.model.$parentResourceClassName")
        return inferType(parentClass, parts, 1)
    } else {
        return null
    }
}

fun inferType(parentClass: Class<*>?, parts: List<String>, index: Int): String? {
    val resourcePropertyName = parts[index]
    val propertyField = parentClass?.getFieldOrNull(resourcePropertyName)

    val propertyType = if (propertyField?.isList == true)
        propertyField.nonParameterizedType
    // TODO: Check if this is required
    else if (propertyField?.type == Enumeration::class.java)
    // TODO: Check if this works
        propertyField.nonParameterizedType
    else
        propertyField?.type

    return if (parts.size > index + 1) {
        return inferType(propertyType, parts, index + 1)
    } else
        propertyType?.name
            ?.replace("org.hl7.fhir.r4.model.", "")
}

fun String.isMultipleTypes(): Boolean = this == "Type"

// Assuming a mock annotation to simulate the @Child.type annotation in FHIR
annotation class Child(val type: KClass<out Type>)
fun String.getPossibleTypes(): List<Type> {
    val clazz = Class.forName("org.hl7.fhir.r4.model.$this")
    val possibleTypes = mutableListOf<Type>()

    clazz.declaredFields.forEach { field ->
        val annotation = field.annotations.find { it is Child } as? Child
        annotation?.let {
            val typeInstance = it.type.java.getDeclaredConstructor().newInstance()
            possibleTypes.add(typeInstance)
        }
    }

    return possibleTypes
}


fun String.canHandleConversion(sourceType: String): Boolean {
    val propertyClass = Class.forName("org.hl7.fhir.r4.model.$this")
    val targetType2 = if (sourceType == "StringType") String::class.java else Class.forName("org.hl7.fhir.r4.model.$sourceType")

    val possibleConversions = listOf(
        "BooleanType" to "StringType",
        "DateType" to "StringType",
        "DecimalType" to "IntegerType",
        "AdministrativeGender" to "CodeType",
        "DateTimeType" to "StringType",
        "TimeType" to "StringType",
        "InstantType" to "DateTimeType",
        "UriType" to "StringType",
        "UuidType" to "StringType",
        "CodeType" to "StringType",
        "MarkdownType" to "StringType",
        "Base64BinaryType" to "StringType",
        "OidType" to "StringType",
        "PositiveIntType" to "IntegerType",
        "UnsignedIntType" to "IntegerType",
        "IdType" to "StringType",
        "CanonicalType" to "StringType"
    )

    possibleConversions.forEach {
        if (this.contains(it.first) && sourceType == it.second) {
            return true
        }
    }

    // Check if the source type can be converted to any of the possible types for this target type
    val possibleTypes = this.getPossibleTypes()
    possibleTypes.forEach { possibleType ->
        if (possibleType::class.simpleName == sourceType) {
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

fun String.getFhirType(): String = replace("Type", "")
    .lowercase()