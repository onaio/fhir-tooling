package org.smartregister.fhir.structuremaptool

import org.apache.poi.ss.usermodel.Row
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.Resource
import java.io.File
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
        stringBuilder.append("group $groupName(source src : QuestionniareResponse, target bundle: Bundle) {").appendNewLine()
        stringBuilder.append("src -> bundle.entry as  entry, entry.resource = create('$resourceName') as entity then {").appendNewLine()

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
        if (constantValue != null) {
            return "'$constantValue'"
        }

        // 2. If the answer is from the QuestionnaireResponse, get the ID of the item in the "Questionnaire Response Field Id" and
        // get it's value using FHIR Path expressions
        if (responseFieldId != null) {
            // TODO: Fix the 2nd param inside the evaluate expression
            return """evaluate(src, ${"$"}this.item${getPropertyPath()}.where(linkId = '$responseFieldId').answer.value)"""
        }

        // 3. If it's a FHIR Path/StructureMap function, add the contents directly from here to the StructureMap
        if (fhirPathStructureMapFunctions != null && !fhirPathStructureMapFunctions!!.isEmpty()) {
            // TODO: Fix the 2nd param inside the evaluate expression
            return fhirPathStructureMapFunctions!!
        }

        // If it's a conversion
        // 4. If the answer is a conversion, (Assume this means it's being convered to a reference)
        if (conversion != null) {
            val resourceName = conversion!!.replace("$", "")
            var resourceIndex = conversion!!.replace("$" + resourceName, "")

            if (resourceIndex.isNotEmpty()) {
                resourceIndex = "[$resourceIndex]"
            }

            //return """reference(evaluate(bundle, ${"$"}this.entry.where(resourceType = '$resourceName')$resourceIndex))"""
            return "''"
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
                    })
                }
            }
        }

        fun buildStructureMap(currLevel: Int) {
            if (instruction != null) {
                stringBuilder.append("src -> entity$currLevel.${instruction!!.fieldPath} = ")
                stringBuilder.append(instruction!!.getAnswerExpression())
                addRuleNo()
                stringBuilder.appendNewLine()
            } else if (nests.size > 0) {
                val resourceType = inferType()

                if (!name.equals("")) {
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

        fun inferType() : String {
            //TODO("Complete this")
            return "Sample"
        }
    }

}
fun generateStructureMapLine(structureMapBody: String, row: Row, resource: Resource, extractionResources: HashMap<String, Resource>)  {


}

fun StringBuilder.appendNewLine() : StringBuilder {
    append(System.lineSeparator())
    return this
}
