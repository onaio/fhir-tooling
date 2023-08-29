package org.smartregister.fhir.structuremaptool

import org.apache.poi.ss.usermodel.Row
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.Resource
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.util.ArrayList
import kotlin.text.StringBuilder

fun getQuestionsPath(questionnaire: Questionnaire) : HashMap<String, String> {
    val questionsMap = hashMapOf<String, String>()

    questionnaire.item.forEachIndexed { index, itemComponent ->
        getQuestionNames("", itemComponent, questionsMap)
    }
    return questionsMap
}

fun getQuestionNames(parentName: String, item: QuestionnaireItemComponent, questionsMap: HashMap<String, String>) {
    val currParentName = if (parentName.isEmpty()) "" else parentName
    questionsMap.put(item.linkId, currParentName)

    item.item.forEachIndexed { index, itemComponent ->
        getQuestionNames(currParentName + "[$index].item", itemComponent, questionsMap)
    }
}


class Group (entry : Map.Entry<String, MutableList<Instruction>>, val stringBuilder : StringBuilder, val questionsPath : HashMap<String, String>) {

    var lineCounter = 0
    var groupName = entry.key
    val instructions = entry.value


    fun generateGroup() {
        val resourceName = instructions[0].resource

        stringBuilder.appendNewLine()
        stringBuilder.append("group Extract$groupName(source src : QuestionniareResponse, target bundle: Bundle) {").appendNewLine()
        stringBuilder.append("src -> bundle.entry as  entry, entry.resource = create('$resourceName') as entity1 then {").appendNewLine()

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

        mainNest.buildStructureMap(0)


        stringBuilder.append("} ")
        addRuleNo()
        stringBuilder.appendNewLine()
        stringBuilder.append("}")
        stringBuilder.appendNewLine()
        stringBuilder.appendNewLine()
    }

    fun addRuleNo() {
        stringBuilder.append(""" "${groupName}_${lineCounter++}"; """)
    }

    fun Instruction.getPropertyPath() : String {
        return questionsPath.getOrDefault(responseFieldId, "")
    }

    fun Instruction.getAnswerExpression() : String {

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
                return "evaluate(src, ${"$"}this.item${getPropertyPath()}.where(linkId = '$responseFieldId').answer.value)"
            }
        }

        // 3. If it's a FHIR Path/StructureMap function, add the contents directly from here to the StructureMap
        if (fhirPathStructureMapFunctions != null && !fhirPathStructureMapFunctions!!.isEmpty()) {
            // TODO: Fix the 2nd param inside the evaluate expression --> Not sure what this is but check this
            return fhirPathStructureMapFunctions!!
        }

        // If it's a conversion
        // 4. If the answer is a conversion, (Assume this means it's being convered to a reference)
        if (conversion != null && conversion!!.isNotBlank() && conversion!!.isNotEmpty()) {
            val resourceName = conversion!!.replace("$", "")
            var resourceIndex = conversion!!.replace("$" + resourceName, "")

            if (resourceIndex.isNotEmpty()) {
                resourceIndex = "[$resourceIndex]"
            }

            // TODO: Create a GROUP that generates a reference to encapsulate this
            //return "reference(evaluate(bundle, ${"$"}this.entry.where(resourceType = '$resourceName')$resourceIndex))"
            return "reference(src)"
        }


        // TODO: FINISH THIS WHICH SEEMS LIKE THE LAST PART

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
                    val partName = if (!parts[0].isEmpty()) {
                        parts[0]
                    } else {
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

                        if (!this@Nest.fullPath.isEmpty()) {
                            fullPath = "${this@Nest.fullPath}.$partName"
                        } else {
                            fullPath = partName
                        }
                        resourceName = inferType("${this@Nest.resourceName}.$fullPath")

                        if ((parts[0].isEmpty() && parts.size > 2) || (!parts[0].isEmpty() && parts.size > 1) ) {
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
                        resourceName = inferType("${this@Nest.resourceName}.$fullPath")
                    })
                }
            }
        }

        fun buildStructureMap(currLevel: Int) {
            if (instruction != null) {

                val answerExpression = instruction!!.getAnswerExpression()

                if (answerExpression.isNotEmpty() && answerExpression.isNotBlank() && answerExpression != "''") {

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
                    it.buildStructureMap(currLevel + 1)
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

        fun inferType(propertyPath: String) : String {
            // TODO: Handle possible errors
            val parentClass = Class.forName("org.hl7.fhir.r4.model.${propertyPath.getParentResource()}")

            val propertyField = parentClass.getFieldOrNull(propertyPath.getResourceProperty()!!)!!

            val propertyType = if (propertyField.isList)
                propertyField.nonParameterizedType
            else
                propertyField.type

            return propertyType.name
                .replace("org.hl7.fhir.r4.model.", "")
        }

        fun String.getParentResource() : String? {
            return substring(0, lastIndexOf('.'))
        }


        fun String.getResourceProperty() : String? {
            return substring(lastIndexOf('.') + 1)
        }
    }

}
fun generateStructureMapLine(structureMapBody: String, row: Row, resource: Resource, extractionResources: HashMap<String, Resource>)  {


}

fun StringBuilder.appendNewLine() : StringBuilder {
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