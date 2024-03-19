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


    fun generateGroup(questionnaireResponse: QuestionnaireResponse) {
        if(fhirResources.contains(groupName.dropLast(1))){
            val resourceName = instructions[0].resource

            stringBuilder.appendNewLine()
            stringBuilder.append("group Extract$groupName(source src : QuestionniareResponse, target bundle: Bundle) {")
                .appendNewLine()
            stringBuilder.append("src -> bundle.entry as  entry, entry.resource = create('$resourceName') as entity1 then {")
                .appendNewLine()
            // TODO: Remove below and replace with Nest.buildStructureMap
                /*instructions.forEachIndexed { index, instruction ->

                //if (instruction.fi)

                stringBuilder.append("src -> entity.${instruction.fieldPath} = ")
                stringBuilder.append(instruction.getAnswerExpression())
                addRuleNo()
                stringBuilder.appendNewLine()
            }*/
            val instructionStartMap = hashMapOf<String, List<Instruction>>()

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
            if (fieldPath.equals("id")) {
                return "create('id') as id, id.value = '$constantValue'";
            } else if (fieldPath.equals("rank")) {
                val constValue = constantValue!!.replace(".0", "")
                return "create('positiveInt') as rank, rank.value = '$constValue'";
            } else {
                return "'$constantValue'"
            }
        }

        // 2. If the answer is from the QuestionnaireResponse, get the ID of the item in the "Questionnaire Response Field Id" and
        // get it's value using FHIR Path expressions
        if (responseFieldId != null) {
            // TODO: Fix the 1st param inside the evaluate expression
            // It needs to reference the specific resource in this bundle

            // TODO: Fix these to use infer
            if (fieldPath.equals("id")) {
                return "create('id') as id, id.value = evaluate(src, ${"$"}this.item${getPropertyPath()}.where(linkId = '$responseFieldId').answer.value)";
            } else if (fieldPath.equals("rank")) {
                return "create('positiveInt') as rank, rank.value = evaluate(src, ${"$"}this.item${getPropertyPath()}.where(linkId = '$responseFieldId').answer.value)";
            } else {

                // TODO: Infer the resource property type and answer to perform other conversions
                // TODO: Extend this to cover other corner cases
                var expression = "${"$"}this.item${getPropertyPath()}.where(linkId = '$responseFieldId').answer.value"

                if (expression.isCoding(questionnaireResponse) && fieldPath.isEnumeration(this)) {
                    expression = expression.replace("answer.value", "answer.value.code")
                } else if (inferType(fullPropertyPath()) == "CodeableConcept") {
                    return "''";
                }

                return "evaluate(src, $expression)"

            }
        }

        // 3. If it's a FHIR Path/StructureMap function, add the contents directly from here to the StructureMap
        if (fhirPathStructureMapFunctions != null && fhirPathStructureMapFunctions!!.isNotEmpty()) {
            // TODO: Fix the 2nd param inside the evaluate expression --> Not sure what this is but check this
            return fhirPathStructureMapFunctions!!
        }

        // If it's a conversion
        // 4. If the answer is a conversion, (Assume this means it's being convered to a reference)
        if (conversion != null && conversion!!.isNotBlank() && conversion!!.isNotEmpty()) {
            val resourceName = conversion!!.replace("$", "")
            var resourceIndex = conversion!!.replace("$$resourceName", "")

            if (resourceIndex.isNotEmpty()) {
                resourceIndex = "[$resourceIndex]"
            }

            // TODO: Create a GROUP that generates a reference to encapsulate this
            //return "reference(evaluate(bundle, ${"$"}this.entry.where(resourceType = '$resourceName')$resourceIndex))"
            return "reference(src)"
        }

        /*
        5. You can use $Resource eg $Patient to reference another resource being extracted here, but how do we actually get it's instance so that we can use it????
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
                val answerExpression = instruction!!.getAnswerExpression(questionnaireResponse)

                if (answerExpression.isNotEmpty() && answerExpression.isNotBlank() && answerExpression != "''") {
                    val propertyType = inferType(instruction!!.fullPropertyPath())
                    val answerType = answerExpression.getAnswerType(questionnaireResponse)

                    if (propertyType != "Type" && answerType != propertyType && propertyType?.canHandleConversion(
                            answerType ?: ""
                        )?.not() == true && answerExpression.startsWith("evaluate")
                    ) {
                        println("Failed type matching --> ${instruction!!.fullPropertyPath()} of type $answerType != $propertyType")

                        /*val possibleTypes = listOf<>()
                        if ()*/

                        stringBuilder.append("src -> entity$currLevel.${instruction!!.fieldPath} = ")
                        stringBuilder.append("create('${propertyType.getFhirType()}') as randomVal, randomVal.value = ")
                        stringBuilder.append(answerExpression)
                        addRuleNo()
                        stringBuilder.appendNewLine()

                        return
                    }

                    stringBuilder.append("src -> entity$currLevel.${instruction!!.fieldPath} = ")

                    // TODO: Skip this instruction if empty and probably log this
                    stringBuilder.append(answerExpression)
                    addRuleNo()
                    stringBuilder.appendNewLine()
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
    row.forEachIndexed { index, cell ->
        val cellValue = cell.stringCellValue
        val fieldPath = row.getCell(4).stringCellValue
        val targetDataType = determineFhirDataType(cellValue)
        structureMapBody.append("src -> entity.${fieldPath}=")

        when (targetDataType) {
            "string" -> {
                structureMapBody.append("create('string').value ='$cellValue'")
            }

            "integer" -> {
                structureMapBody.append("create('integer').value = $cellValue")
            }

            "boolean" -> {
                val booleanValue =
                    if (cellValue.equals("true", ignoreCase = true)) "true" else "false"
                structureMapBody.append("create('boolean').value = $booleanValue")
            }

            else -> {
                structureMapBody.append("create('unsupportedDataType').value = '$cellValue'")
            }
        }
        structureMapBody.appendNewLine()
    }
}

fun determineFhirDataType(cellValue: String): String {
    val cleanedValue = cellValue.trim().toLowerCase()

    when {
        cleanedValue == "true" || cleanedValue == "false" -> return "boolean"
        cleanedValue.matches(Regex("-?\\d+")) -> return "boolean"
        cleanedValue.matches(Regex("-?\\d*\\.\\d+")) -> return "decimal"
        cleanedValue.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> return "date"
        cleanedValue.matches(Regex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) -> return "dateTime"
        else -> {
            return "string"
        }
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
            hostServices = FHIRPathEngineHostServices
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

// TODO: Finish this. Use the annotation @Chid.type
fun String.getPossibleTypes(): List<Type> {
    return listOf()
}


fun String.canHandleConversion(sourceType: String): Boolean {
    val propertyClass = Class.forName("org.hl7.fhir.r4.model.$this")
    val targetType2 =
        if (sourceType == "StringType") String::class.java else Class.forName("org.hl7.fhir.r4.model.$sourceType")

    val possibleConversions = listOf(
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

fun String.getFhirType(): String = replace("Type", "")
    .lowercase()