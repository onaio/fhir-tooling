/* (C)2023 */
package org.smartregister.external

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.util.TerserUtil
import java.util.Date
import org.apache.commons.lang3.StringUtils
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.ActivityDefinition
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BaseDateTimeType
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Dosage
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.utils.FHIRPathEngine
import org.smartregister.processor.StructureMapProcessor
import org.smartregister.util.FctStructureMapUtilities
import org.smartregister.util.FctUtils

/**
 * This class (loosely) borrows from
 * https://github.com/opensrp/fhircore/blob/main/android/engine/src/main/java/org/smartregister/fhircore/engine/task/FhirCarePlanGenerator.kt
 * Note: It cherry-picks parts of the class that are required to generate the Careplan resource and
 * extract the raw Tasks. Its application on efsity is to confirm the correct types of resources are
 * generated
 */
class FhirCarePlanGeneratorLite {

  private var structureMapProcessor: StructureMapProcessor
  private var structureMapDictionary: Map<String, String>

  constructor(structureMapFolderPath: String) {
    structureMapProcessor = StructureMapProcessor(structureMapFolderPath)
    structureMapDictionary = structureMapProcessor.generateIdToFilepathMap()
  }

  fun generateOrUpdateCarePlan(
    planDefinition: PlanDefinition,
    subject: Resource,
    data: Bundle = Bundle()
  ): CarePlan {
    val output =
      CarePlan().apply {
        // TODO delete this section once all PlanDefinitions are using new
        // recommended approach
        this.title = planDefinition.title
        this.description = planDefinition.description
        this.instantiatesCanonical = listOf(CanonicalType(planDefinition.asReference().reference))
      }

    FctUtils.printInfo(
      String.format(
        "Processing Plan Definition with name \u001B[36m%s\u001B[0m and title \u001b[35m%s\u001b[0m : \u001B[35m%s\u001B[0m",
        planDefinition.name,
        planDefinition.title,
        planDefinition.description
      )
    )

    if (planDefinition == null || planDefinition.action == null || planDefinition.action.size < 1) {
      FctUtils.printWarning("No Actions defined found for the Plan definition")
    } else
      FctUtils.printInfo(
        String.format(
          "Total Plan definition Actions found: \u001B[36m%s\u001B[0m",
          planDefinition.action.size
        )
      )

    var tasksGenerated = 0
    planDefinition.action.forEach { action ->
      val input = Bundle().apply { entry.addAll(data.entry) }
      if (action.passesConditions(input, planDefinition, subject)) {
        val definition = action.activityDefinition(planDefinition)

        if (action.hasTransform()) {
          val taskPeriods = action.taskPeriods(definition, output)

          taskPeriods.forEachIndexed { index, period ->
            val source =
              Parameters().apply {
                addResourceParameter(CarePlan.SP_SUBJECT, subject)
                addResourceParameter(PlanDefinition.SP_DEFINITION, definition)
                // TODO find some other way (activity definition based) to pass additional data
                addResourceParameter(PlanDefinition.SP_DEPENDS_ON, data)
              }
            source.setParameter(Task.SP_PERIOD, period)
            source.setParameter(ActivityDefinition.SP_VERSION, IntegerType(index))

            val structureMapUtilities = FctStructureMapUtilities()
            val structureMapId = IdType(action.transform).idPart
            val structureMapFilePath = getStructureMapById(structureMapId)

            if (StringUtils.isNotBlank(structureMapFilePath)) {

              FctUtils.printInfo(
                String.format(
                  "Extracting with structure map id \u001B[36m%s\u001B[0m - \u001b[35m%s\u001b[0m",
                  structureMapId,
                  structureMapFilePath
                )
              )

              val structureMap = structureMapUtilities.getStructureMap(structureMapFilePath)
              structureMapUtilities.transform(
                structureMapUtilities.simpleWorkerContext,
                source,
                structureMap,
                output,
              )
            } else {

              FctUtils.printError(
                String.format(
                  "Structure Map with id \u001B[36m%s\u001B[0m missing for the provided Plan definition",
                  structureMapId
                )
              )
            }
          }

          tasksGenerated++
        }
        if (definition.hasDynamicValue()) {
          definition.dynamicValue.forEach { dynamicValue ->
            if (definition.kind == ActivityDefinition.ActivityDefinitionKind.CAREPLAN) {
              dynamicValue.expression.expression
                .let { fhirPathEngine.evaluate(null, input, planDefinition, subject, it) }
                ?.takeIf { it.isNotEmpty() }
                ?.let { evaluatedValue ->
                  // TODO handle cases where we explicitly need to set previous value as null, when
                  // passing null to Terser, it gives error NPE
                  // logger.info ("${dynamicValue.path}, evaluatedValue: $evaluatedValue")
                  TerserUtil.setFieldByFhirPath(
                    FhirContext.forR4Cached(),
                    dynamicValue.path.removePrefix("${definition.kind.display}."),
                    output,
                    evaluatedValue.first(),
                  )
                }
            } else {
              throw UnsupportedOperationException("${definition.kind} not supported")
            }
          }
        }
      } else
        FctUtils.printWarning(
          String.format(
            "Condition failed for Plan Definition Action \u001B[36m%s\u001B[0m",
            action.definition.primitiveValue()
          )
        )
    }

    if (tasksGenerated == 0) {
      FctUtils.printWarning("No Plan definition Action condition passed. 0 Tasks generated!")
    } else FctUtils.printInfo(String.format("%s Tasks generated!", tasksGenerated))
    return output
  }

  private fun getStructureMapById(id: String): String? {
    return structureMapDictionary[id]
  }

  private fun extractTaskPeriodsFromDosage(dosage: List<Dosage>, carePlan: CarePlan): List<Period> {
    val taskPeriods = mutableListOf<Period>()
    dosage
      .flatMap { extractTaskPeriodsFromTiming(it.timing, carePlan) }
      .also { taskPeriods.addAll(it) }

    return taskPeriods
  }

  private fun evaluateToDate(base: Base?, expression: String): BaseDateTimeType? =
    base?.let { fhirPathEngine.evaluate(it, expression).firstOrNull()?.dateTimeValue() }

  private fun extractTaskPeriodsFromTiming(timing: Timing, carePlan: CarePlan): List<Period> {
    val taskPeriods = mutableListOf<Period>()
    // TODO handle properties used by older PlanDefintions. If any PlanDefinition is using
    // countMax, frequency, durationUnit of hour, consider as non compliant and assume
    // all handling would be done with a structure map. Once all PlanDefinitions are using
    // recommended approach and using plan definitions properly change line below to use
    // timing.repeat.count only
    val isLegacyPlanDefinition =
      (timing.repeat.hasFrequency() ||
        timing.repeat.hasCountMax() ||
        timing.repeat.durationUnit?.equals(Timing.UnitsOfTime.H) == true)
    val count = if (isLegacyPlanDefinition || !timing.repeat.hasCount()) 1 else timing.repeat.count

    val periodExpression = timing.extractFhirpathPeriod()
    val durationExpression = timing.extractFhirpathDuration()

    // Offset date for current task period; CarePlan start if all tasks generated at once
    // otherwise today means that tasks are generated on demand
    var offsetDate: BaseDateTimeType =
      DateTimeType(if (timing.repeat.hasCount()) carePlan.period.start else Date())

    for (i in 1..count) {
      if (periodExpression.isNotBlank() && offsetDate.hasValue()) {
        evaluateToDate(offsetDate, "\$this + $periodExpression")?.let { offsetDate = it }
      }

      Period()
        .apply {
          start = offsetDate.value
          end =
            if (durationExpression.isNotBlank() && offsetDate.hasValue()) {
              evaluateToDate(offsetDate, "\$this + $durationExpression")?.value
            } else carePlan.period.end
        }
        .also { taskPeriods.add(it) }
    }

    return taskPeriods
  }

  // Extensions
  private fun PlanDefinition.PlanDefinitionActionComponent.taskPeriods(
    definition: ActivityDefinition,
    carePlan: CarePlan,
  ): List<Period> {
    return when {
      definition.hasDosage() -> extractTaskPeriodsFromDosage(definition.dosage, carePlan)
      definition.hasTiming() && !definition.hasTimingTiming() ->
        throw IllegalArgumentException(
          "Timing component should only be Timing. Can not handle ${timing.fhirType()}",
        )
      else -> extractTaskPeriodsFromTiming(definition.timingTiming, carePlan)
    }
  }
  private fun Parameters.addResourceParameter(name: String, resource: Resource) =
    this.addParameter(
      Parameters.ParametersParameterComponent().apply {
        this.name = name
        this.resource = resource
      },
    )
  private fun PlanDefinition.PlanDefinitionActionComponent.passesConditions(
    focus: Resource?,
    root: Resource?,
    base: Base,
  ) =
    this.condition.all {
      require(it.kind == PlanDefinition.ActionConditionKind.APPLICABILITY) {
        "PlanDefinition.action.kind=${it.kind} not supported"
      }

      require(it.expression.language == Expression.ExpressionLanguage.TEXT_FHIRPATH.toCode()) {
        "PlanDefinition.expression.language=${it.expression.language} not supported"
      }

      fhirPathEngine.evaluateToBoolean(focus, root, base, it.expression.expression)
    }

  private val fhirPathEngine: FHIRPathEngine =
    with(FhirContext.forCached(FhirVersionEnum.R4)) {
      FHIRPathEngine(HapiWorkerContext(this, this.validationSupport)).apply {
        hostServices = FhirPathEngineHostServices
      }
    }

  private fun Resource.asReference() =
    Reference().apply { this.reference = "$resourceType/$logicalId" }

  private val Resource.logicalId: String
    get() {
      return this.idElement?.idPart.orEmpty()
    }

  private fun PlanDefinition.PlanDefinitionActionComponent.activityDefinition(
    planDefinition: PlanDefinition,
  ) =
    planDefinition.contained
      .filter { it.resourceType == ResourceType.ActivityDefinition }
      .first { it.logicalId == this.definitionCanonicalType.value } as ActivityDefinition

  private fun Timing.extractFhirpathPeriod() =
    this.repeat.let { if (it.hasPeriod()) "${it.period} '${it.periodUnit.display}'" else "" }

  private fun Timing.extractFhirpathDuration() =
    this.repeat.let { if (it.hasDuration()) "${it.duration} '${it.durationUnit.display}'" else "" }
}
