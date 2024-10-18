package org.smartregister.structuremaptool

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.util.*
import org.apache.commons.io.FileUtils
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.utilities.npm.FilesystemPackageCacheManager
import org.hl7.fhir.utilities.npm.ToolsVersion
import org.smartregister.external.TransformSupportServices
import org.smartregister.structuremaptool.extensions.addIndentation
import org.smartregister.structuremaptool.extensions.clean

fun main(
  xlsConfigFilePath: String,
  questionnaireFilePath: String,
  questionnaireResponsePath: String
) {
  val contextR4 = FhirContext.forR4()
  val fhirJsonParser = contextR4.newJsonParser()
  val questionnaire: Questionnaire =
    fhirJsonParser.parseResource(
      Questionnaire::class.java,
      FileUtils.readFileToString(File(questionnaireFilePath), Charset.defaultCharset())
    )
  val questionnaireResponse: QuestionnaireResponse =
    fhirJsonParser.parseResource(
      QuestionnaireResponse::class.java,
      FileUtils.readFileToString(File(questionnaireResponsePath), Charset.defaultCharset())
    )

  // reads the xls
  val xlsConfigFile = FileInputStream(xlsConfigFilePath)
  val xlWorkbook = WorkbookFactory.create(xlsConfigFile)

  // TODO: Check that all the Resource(s) ub the Resource column are the correct name and type eg.
  // RiskFlag in the previous XLSX was not valid
  // TODO: Check that all the path's and other entries in the excel sheet are valid
  // TODO: Add instructions for adding embedded classes like
  // `RiskAssessment$RiskAssessmentPredictionComponent` to the TransformSupportServices

  // read the settings sheet
  val settingsWorkbook = xlWorkbook.getSheet("Settings")
  var questionnaireId: String? = null

  for (i in 0..settingsWorkbook.lastRowNum) {
    val cell = settingsWorkbook.getRow(i).getCell(0)
    if (cell.stringCellValue == "questionnaire-id") {
      questionnaireId = settingsWorkbook.getRow(i).getCell(1).stringCellValue
    }
  }

  /*
   TODO: Fix Groups calling sequence so that Groups that depend on other resources to be generated need to be called first
   We can also throw an exception if to figure out cyclic dependency. Good candidate for Floyd's tortoise and/or topological sorting 😁. Cool!!!!
  */

  val questionnaireResponseItemIds = questionnaireResponse.item.map { it.id }
  if (questionnaireId != null && questionnaireResponseItemIds.isNotEmpty()) {

    val sb = StringBuilder()
    val structureMapHeader =
      """
            map "http://hl7.org/fhir/StructureMap/$questionnaireId" = '${questionnaireId.clean()}'
            
            uses "http://hl7.org/fhir/StructureDefinition/QuestionnaireReponse" as source
            uses "http://hl7.org/fhir/StructureDefinition/Bundle" as target
        """
        .trimIndent()

    val structureMapBody =
      """
            group ${questionnaireId.clean()}(source src : QuestionnaireResponse, target bundle: Bundle) {
            src -> bundle.id = uuid() "rule_c";
            src -> bundle.type = 'collection' "rule_b";
            src -> bundle.entry as entry then """
        .trimIndent()

    val resourceConversionInstructions = hashMapOf<String, MutableList<Instruction>>()

    // Group the rules according to the resource
    val fieldMappingsSheet = xlWorkbook.getSheet("Field Mappings")
    fieldMappingsSheet.forEachIndexed { index, row ->
      if (index == 0) return@forEachIndexed
      if (row.isEmpty()) {
        return@forEachIndexed
      }

      val instruction = row.getInstruction()
      val xlsId = instruction.responseFieldId
      val comparedResponseAndXlsId = questionnaireResponseItemIds.contains(xlsId)
      if (instruction.resource.isNotEmpty() && comparedResponseAndXlsId) {
        resourceConversionInstructions
          .computeIfAbsent(instruction.searchKey(), { key -> mutableListOf() })
          .add(instruction)
      }
    }

    // val resource =  ?: Class.forName("org.hl7.fhir.r4.model.$resourceName").newInstance() as
    // Resource

    // Perform the extraction for the row
    /*generateStructureMapLine(structureMapBody, row, resource, extractionResources)

    extractionResources[resourceName + resourceIndex] = resource*/

    sb.append(structureMapHeader)
    sb.appendNewLine(2)
    sb.append(structureMapBody)

    // Fix the question path
    val questionsPath = getQuestionsPath(questionnaire)

    // TODO: Generate the links to the group names here
    var index = 0
    var len = resourceConversionInstructions.size
    var resourceName = ""
    resourceConversionInstructions.forEach { entry ->
      resourceName =
        entry.key.replaceFirstChar {
          if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
      if (index++ != 0) sb.append(",")
      if (resourceName.isNotEmpty()) sb.append("Extract$resourceName(src, bundle)")
    }
    sb.append(""" "rule_a";""".trimMargin())
    sb.appendNewLine()
    sb.append("}")

    // Add the embedded instructions
    val groupNames = mutableListOf<String>()
    sb.appendNewLine(3)

    resourceConversionInstructions.forEach {
      Group(it, sb, questionsPath).generateGroup(questionnaireResponse)
    }

    val structureMapString = sb.toString()
    try {
      val simpleWorkerContext =
        SimpleWorkerContext().apply {
          setExpansionProfile(Parameters())
          isCanRunWithoutTerminology = true
        }
      val transformSupportServices = TransformSupportServices(simpleWorkerContext)
      val scu =
        org.hl7.fhir.r4.utils.StructureMapUtilities(simpleWorkerContext, transformSupportServices)
      val structureMap = scu.parse(structureMapString, questionnaireId.clean())
      // DataFormatException | FHIRLexerException

      try {
        val bundle = Bundle()
        scu.transform(contextR4, questionnaireResponse, structureMap, bundle)
        val jsonParser = FhirContext.forR4().newJsonParser()

        println(jsonParser.encodeResourceToString(bundle))
      } catch (e: Exception) {
        e.printStackTrace()
      }
    } catch (ex: Exception) {
      println("The generated StructureMap has a formatting error")
      ex.printStackTrace()
    }

    var finalStructureMap = sb.toString()
    finalStructureMap = finalStructureMap.addIndentation()
    println(finalStructureMap)
    writeStructureMapOutput(sb.toString().addIndentation())
  }
}

fun Row.isEmpty(): Boolean {
  return getCell(0) == null && getCell(1) == null && getCell(2) == null
}

fun Row.getCellAsString(cellnum: Int): String? {
  val cell = getCell(cellnum) ?: return null
  return when (cell.cellTypeEnum) {
    CellType.STRING -> cell.stringCellValue
    CellType.BLANK -> null
    CellType.BOOLEAN -> cell.booleanCellValue.toString()
    CellType.NUMERIC -> cell.numericCellValue.toString()
    else -> null
  }
}

fun Row.getInstruction(): Instruction {
  return Instruction().apply {
    responseFieldId = getCell(0)?.stringCellValue
    constantValue = getCellAsString(1)
    resource = getCell(2).stringCellValue
    resourceIndex = getCell(3)?.numericCellValue?.toInt() ?: 0
    fieldPath = getCell(4)?.stringCellValue ?: ""
    fullFieldPath = fieldPath
    field = getCell(5)?.stringCellValue
    conversion = getCell(6)?.stringCellValue
    fhirPathStructureMapFunctions = getCell(7)?.stringCellValue
  }
}

class Instruction {
  var responseFieldId: String? = null
  var constantValue: String? = null
  var resource: String = ""
  var resourceIndex: Int = 0
  var fieldPath: String = ""
  var field: String? = null
  var conversion: String? = null
  var fhirPathStructureMapFunctions: String? = null

  // TODO: Clean the following properties
  var fullFieldPath = ""

  fun fullPropertyPath(): String = "$resource.$fullFieldPath"

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

fun writeStructureMapOutput(structureMap: String) {
  File("generated-structure-map.txt").writeText(structureMap.addIndentation())
  val pcm = FilesystemPackageCacheManager(true, ToolsVersion.TOOLS_VERSION)
  val contextR5 = SimpleWorkerContext.fromPackage(pcm.loadPackage("hl7.fhir.r4.core", "4.0.1"))
  contextR5.setExpansionProfile(Parameters())
  contextR5.isCanRunWithoutTerminology = true
  val transformSupportServices = TransformSupportServices(contextR5)
  val scu = org.hl7.fhir.r4.utils.StructureMapUtilities(contextR5, transformSupportServices)
  val map = scu.parse(structureMap, "LocationRegistration")
  val iParser: IParser =
    FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().setPrettyPrint(true)
  val mapString = iParser.encodeResourceToString(map)
  File("generated-json-map.json").writeText(mapString)
}
