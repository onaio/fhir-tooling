package org.smartregister.fhir.structuremaptool

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.google.gson.GsonBuilder
import org.apache.commons.io.FileUtils
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.utilities.npm.FilesystemPackageCacheManager
import org.hl7.fhir.utilities.npm.ToolsVersion
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset

fun main(args: Array<String>) {
    Application().main(args)
}

/*fun main(args: Array<String>) {
    val values = hashMapOf(Pair("username", "Bilira"), Pair("client_id", "uBr6UUy5VprEcvCnWndSWcALKivttaqk25"))
    val digest = MessageDigest.getInstance("MD5")
    val bytes = digest.digest(values.toString().toByteArray(charset("UTF-8")))
    val answ = String.format("%032x", BigInteger(1, bytes))
    System.out.println(answ)

}*/


/*

REMAINING TASKS
==================

1. Allow converting of a StructureMap text to JSON
2. Add support for processing "StructureMap XLS-old.xlsx" file. Allow multiple similar properties eg multiple RelatedPerson.telecom

 */

class Application : CliktCommand() {
    val xlsFileName: String by option(help = "XLS filepath").prompt("Kindly enter the XLS filename")
   // val xlsfile: String by option(help = "XLS filepath").prompt("Kindly enter the XLS filepath")
    val questionnaireFileName: String by option(help = "Questionnaire filename").prompt("Kindly enter the questionnaire filename")
    //val questionnairefile : String by option(help = "Questionnaire filepath").prompt("Kindly enter the questionnaire filepath")

    override fun run() {
        var xlsfile= ""
        var questionnairefile = ""

        val xlsUrl = Application::class.java.getResource("/$xlsFileName")
        val questionnaireUrl = Application::class.java.getResource("/$questionnaireFileName")

        // Check if the resource exists
        if (xlsUrl != null && questionnaireUrl != null) {
            // Xls file path extraction
            val xlsfilePath = File(xlsUrl.toURI())
            xlsfile = xlsfilePath.absolutePath

            // questionnaire file path extraction
            val questionnaireFilePath = File(questionnaireUrl.toURI())
            questionnairefile = questionnaireFilePath.absolutePath
        } else {
            println("Resource not found: $xlsFileName")
            println("Resource not found: $questionnaireFileName")
        }

        /*

        ALGORITHM / PSEUDOCODE
        =============================

        1. Read the settings and save them in a Settings object
        2. Read the main sheet "Field Mappings". Group the rules into end Resources
            a) Index it into a Hashmap. The hashmap should have a ResourceExtractionDetails object that contains
                - Resources
                - GroupName
                - Parameters of the group
                - the Field mappings
        3. Start creating the StructureMap
            a) First create the bundle and all the resources, each in it's own group

        4. Loop through all the questions and check if the QR field is used somewhere and fix the complete path
        5. Add an errors list
        6. Loop through each end Resource from step (2) and generate the Group. Keep a variable to track the line number + line
            a) Add the function/group declaration with params
            b) Start looping each of the Resource field and use FHIR Path to get the value
            c) Check the expected type for that field in the resource rather Resource.field type and have a utility that converts
                from the current type to the final type or skips and add this field and the error to the errors list
            d) Create the line to assign the equation to the Resource.field with the line number
        7. Close the Resource loop by adding the closing } for the group
        8. Validate the generated StructureMap and throw an error if it fails to build with the error thrown by the validator
        9. Else, Loop through the errors

         */


        // Create a map of Resource -> questionnaire name or path -> value
        // For each resource loop through creating or adding the correct instructions


        lateinit var questionnaireResponse:QuestionnaireResponse
        val contextR4 = FhirContext.forR4()
        val fhirJsonParser = contextR4.newJsonParser()
        val questionnaire : Questionnaire = fhirJsonParser.parseResource(Questionnaire::class.java, FileUtils.readFileToString(File(questionnairefile), Charset.defaultCharset()))
        val questionnaireResponseFile = File(javaClass.classLoader.getResource("questionnaire-response.json")?.file)
        if (questionnaireResponseFile.exists()) {
            questionnaireResponse = fhirJsonParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseFile.readText(Charset.defaultCharset()))
        } else {
            println("File not found: questionnaire-response.json")
        }
        val xlsFile = FileInputStream(xlsfile)
        val xlWb = WorkbookFactory.create(xlsFile)

        // TODO: Check that all the Resource(s) ub the Resource column are the correct name and type eg. RiskFlag in the previous XLSX was not valid
        // TODO: Check that all the path's and other entries in the excel sheet are valid
        // TODO: Add instructions for adding embedded classes like `RiskAssessment$RiskAssessmentPredictionComponent` to the TransformSupportServices

        /*

        READ THE SETTINGS SHEET

         */
        val settingsWorkbook = xlWb.getSheet("Settings")
        var questionnaireId : String? = null

        for (i in 0..settingsWorkbook.lastRowNum) {
            val cell = settingsWorkbook.getRow(i).getCell(0)

            if (cell.stringCellValue == "questionnaire-id") {
                questionnaireId = settingsWorkbook.getRow(i).getCell(1).stringCellValue
            }
        }

        /*

        END OF READ SETTINGS SHEET

         */

        /*
        TODO: Fix Groups calling sequence so that Groups that depend on other resources to be generated need to be called first
           We can also throw an exception if to figure out cyclic dependency. Good candidate for Floyd's tortoise and/or topological sorting 😁. Cool!!!!
         */

        val sb = StringBuilder()
        val structureMapHeader = """
            map "http://hl7.org/fhir/StructureMap/$questionnaireId" = '${questionnaireId?.clean()}'
            
            
            uses "http://hl7.org/fhir/StructureDefinition/QuestionnaireReponse" as source
            uses "http://hl7.org/fhir/StructureDefinition/Bundle" as target
        """.trimIndent()

        val structureMapBody = """
            group ${questionnaireId?.clean()}(source src : QuestionnaireResponse, target bundle: Bundle) {
            src -> bundle.id = uuid() "rule_c";
            src -> bundle.type = 'collection' "rule_b";
            src -> bundle.entry as entry then """.trimIndent()

        /*

        Create a mapping of COLUMN_NAMES to COLUMN indexes

         */
        //val mapColumns


        val lineNos = 1
        var firstResource = true
        val extractionResources = hashMapOf<String, Resource>()
        val resourceConversionInstructions = hashMapOf<String, MutableList<Instruction>>()

        // Group the rules according to the resource
        val fieldMappingsSheet = xlWb.getSheet("Field Mappings")
        fieldMappingsSheet.forEachIndexed { index, row ->
            if (index == 0) return@forEachIndexed

            if (row.isEmpty()) {
                return@forEachIndexed
            }

            val instruction = row.getInstruction()
            if (instruction.resource.isNotEmpty()) {
                resourceConversionInstructions.computeIfAbsent(instruction.searchKey(), { key -> mutableListOf() })
                    .add(instruction)
            }
        }
        //val resource =  ?: Class.forName("org.hl7.fhir.r4.model.$resourceName").newInstance() as Resource


        // Perform the extraction for the row
        /*generateStructureMapLine(structureMapBody, row, resource, extractionResources)

        extractionResources[resourceName + resourceIndex] = resource*/

        sb.append(structureMapHeader)
        sb.appendNewLine().appendNewLine().appendNewLine()
        sb.append(structureMapBody)

        // Fix the questions path
        val questionsPath = getQuestionsPath(questionnaire)

        // TODO: Generate the links to the group names here
        var index = 0
        var len = resourceConversionInstructions.size
        resourceConversionInstructions.forEach { entry ->
            val resourceName = entry.key.capitalize()
            if (index++ != 0) sb.append(", ")
            sb.append("Extract$resourceName(src, bundle)")
        }
        sb.append(""" "rule_a";""".trimMargin())
        sb.appendNewLine()
        sb.append("}")

        // Add the embedded instructions
        val groupNames = mutableListOf<String>()

        sb.appendNewLine().appendNewLine().appendNewLine()

        resourceConversionInstructions.forEach {
            Group(it, sb, questionsPath)
                .generateGroup(questionnaireResponse)
        }

        val structureMapString = sb.toString()
        try {
            val simpleWorkerContext = SimpleWorkerContext().apply {
                setExpansionProfile(Parameters())
                isCanRunWithoutTerminology = true
            }
            val transformSupportServices = TransformSupportServices(simpleWorkerContext)
            val scu = org.hl7.fhir.r4.utils.StructureMapUtilities(simpleWorkerContext, transformSupportServices)
            val structureMap = scu.parse(structureMapString, questionnaireId!!.clean())
            // DataFormatException | FHIRLexerException

            val bundle = Bundle()

            scu.transform(contextR4, questionnaireResponse, structureMap, bundle)

            val jsonParser = FhirContext.forR4().newJsonParser()

            println(jsonParser.encodeResourceToString(bundle))
        } catch (ex: Exception) {
            System.out.println("The generated StructureMap has a formatting error")
            ex.printStackTrace()
        }
        writeStructureMapOutput(sb.toString().addIdentation())
    }

    fun Row.getInstruction() : Instruction {
        return Instruction().apply {
            responseFieldId = getCell(0) ?.stringCellValue
            constantValue = getCellAsString(1)
            resource = getCell(2).stringCellValue
            resourceIndex = getCell(3) ?.numericCellValue?.toInt() ?: 0
            fieldPath = getCell(4) ?.stringCellValue ?: ""
            fullFieldPath = fieldPath
            field = getCell(5) ?.stringCellValue
            conversion = getCell(6) ?.stringCellValue
            fhirPathStructureMapFunctions = getCell(7) ?.stringCellValue
        }
    }

    fun Row.getCellAsString(cellnum: Int) : String? {
        val cell = getCell(cellnum) ?: return null
        return when (cell.cellTypeEnum) {
            CellType.STRING -> cell.stringCellValue
            CellType.BLANK -> null
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.NUMERIC -> cell.numericCellValue.toString()
            else -> null
        }
    }

    fun Row.isEmpty() : Boolean {
        return getCell(0) == null && getCell(1) == null && getCell(2) == null
    }

    fun String.clean() : String {
        return this.replace("-", "")
            .replace("_", "")
            .replace(" ", "")
    }

}

class Instruction {
    var responseFieldId : String? = null
    var constantValue: String? = null
    var resource: String = ""
    var resourceIndex: Int = 0
    var fieldPath: String = ""
    var field: String? = null
    var conversion : String? = null
    var fhirPathStructureMapFunctions: String? = null


    // TODO: Clean the following properties
    var fullFieldPath = ""
    fun fullPropertyPath() : String = "$resource.$fullFieldPath"

    fun searchKey() = resource + resourceIndex
}

fun Instruction.copyFrom(instruction: Instruction) {
    constantValue = instruction.constantValue
    resource = instruction.resource
    resourceIndex = instruction.resourceIndex
    fieldPath = instruction.fieldPath
    fullFieldPath = instruction.fullFieldPath
    field = instruction.field
    conversion = instruction.conversion
    fhirPathStructureMapFunctions = instruction.fhirPathStructureMapFunctions
}


fun String.addIdentation() : String {
    var currLevel = 0

    val lines = split("\n")

    val sb = StringBuilder()
    lines.forEach { line ->
        if (line.endsWith("{")) {
            sb.append(line.addIdentation(currLevel))
            sb.appendNewLine()
            currLevel++
        } else if (line.startsWith("}")) {
            currLevel--
            sb.append(line.addIdentation(currLevel))
            sb.appendNewLine()
        } else {
            sb.append(line.addIdentation(currLevel))
            sb.appendNewLine()
        }
    }

    return sb.toString()
}

fun String.addIdentation(times: Int) : String {
    var processedString = ""
    for (k in 1..times) {
        processedString += "\t"
    }

    processedString += this
    return processedString
}

fun writeStructureMapOutput( structureMap: String){
    File("generated-structure-map.txt").writeText(structureMap.addIdentation())
    val pcm = FilesystemPackageCacheManager(true, ToolsVersion.TOOLS_VERSION)
    val contextR5 = SimpleWorkerContext.fromPackage(pcm.loadPackage("hl7.fhir.r4.core", "4.0.1"))
    contextR5.setExpansionProfile(Parameters())
    contextR5.isCanRunWithoutTerminology = true
    val transformSupportServices = TransformSupportServices(contextR5)
    val scu = org.hl7.fhir.r4.utils.StructureMapUtilities(contextR5, transformSupportServices)
    val map = scu.parse(structureMap, "LocationRegistration")
    val iParser: IParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().setPrettyPrint(true)
    val mapString = iParser.encodeResourceToString(map)
    File("generated-json-map.json").writeText(mapString)
}
