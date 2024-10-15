package org.smartregister.fct.workflow.data.generator

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.util.TerserUtil
import javassist.NotFoundException
import org.hl7.fhir.r4.model.ActivityDefinition
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BaseDateTimeType
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Dosage
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.model.Task
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.utils.FHIRPathEngine
import org.hl7.fhir.r4.utils.StructureMapUtilities
import org.smartregister.fct.adb.domain.usecase.DeviceManager
import org.smartregister.fct.device_database.data.persistence.DeviceDBConfigPersistence
import org.smartregister.fct.device_database.domain.model.QueryRequest
import org.smartregister.fct.device_database.domain.model.QueryResponse
import org.smartregister.fct.engine.data.helper.TransformSupportServices
import org.smartregister.fct.engine.util.asReference
import org.smartregister.fct.engine.util.decodeResourceFromString
import org.smartregister.fct.engine.util.encodeResourceToString
import org.smartregister.fct.engine.util.logicalId
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.workflow.domain.model.WorkflowResponse
import org.smartregister.fct.workflow.util.addResourceParameter
import org.smartregister.fct.workflow.util.extractFhirpathDuration
import org.smartregister.fct.workflow.util.extractFhirpathPeriod
import java.util.Date

internal class LiteWorkflowGenerator(
    private val fhirPathEngine: FHIRPathEngine,
    private val transformSupportServices: TransformSupportServices,
) {

    private val resourceCache = mutableMapOf<String, Resource>()

    private val structureMapUtilities by lazy {
        StructureMapUtilities(
            transformSupportServices.simpleWorkerContext,
            transformSupportServices,
        )
    }

    suspend fun generate(
        planDefinition: PlanDefinition,
        subject: Resource,
        otherResource: List<String>,
    ): WorkflowResponse {

        return try {

            // clear the cache
            resourceCache.clear()

            val relatedEntityLocationTags =
                subject.meta.tag.filter {
                    it.system == "https://smartregister.org/related-entity-location-tag-id"
                }

            val carePlan = CarePlan().apply {
                this.title = planDefinition.title
                this.description = planDefinition.description
                this.instantiatesCanonical =
                    listOf(CanonicalType(planDefinition.asReference().reference))
                relatedEntityLocationTags.forEach(this.meta::addTag)
            }

            generateLiteCareplan(
                carePlan = carePlan,
                planDefinition = planDefinition,
                subject = subject,
                otherResource = otherResource,
                relatedEntityLocationTags = relatedEntityLocationTags
            )
        } catch (ex: Exception) {
            return WorkflowResponse(
                error = ex.message ?: "Query Error"
            )
        }

    }

    private suspend fun generateLiteCareplan(
        planDefinition: PlanDefinition,
        subject: Resource,
        carePlan: CarePlan,
        otherResource: List<String>,
        relatedEntityLocationTags: List<Coding>
    ): WorkflowResponse {


        val data = Bundle().apply {
            otherResource.forEach { resourceString ->
                addEntry().apply {
                    resource = resourceString.decodeResourceFromString()
                }
            }
        }

        planDefinition.action.forEach { action ->
            val input = Bundle().apply { entry.addAll(data.entry) }

            if (action.passesConditions(input, planDefinition, subject)) {
                val definition = action.activityDefinition(planDefinition)

                if (action.hasTransform()) {
                    val taskPeriods = action.taskPeriods(definition, carePlan)

                    taskPeriods.forEachIndexed { index, period ->
                        val source =
                            Parameters().apply {
                                addResourceParameter(CarePlan.SP_SUBJECT, subject)
                                addResourceParameter(PlanDefinition.SP_DEFINITION, definition)
                                addResourceParameter(PlanDefinition.SP_DEPENDS_ON, data)
                            }
                        source.setParameter(Task.SP_PERIOD, period)
                        source.setParameter(ActivityDefinition.SP_VERSION, IntegerType(index))

                        val id = IdType(action.transform).idPart

                        val structureMap = otherResource
                            .map { it.decodeResourceFromString<Resource>() }
                            .filterIsInstance<StructureMap>()
                            .firstOrNull { it.logicalId == id } ?: loadFromAppDatabase(id) as StructureMap

                        structureMapUtilities.transform(
                            transformSupportServices.simpleWorkerContext,
                            source,
                            structureMap,
                            carePlan,
                        )
                    }
                }

                if (definition.hasDynamicValue()) {
                    definition.dynamicValue.forEach { dynamicValue ->
                        if (definition.kind == ActivityDefinition.ActivityDefinitionKind.CAREPLAN) {
                            dynamicValue.expression.expression
                                .let {
                                    fhirPathEngine.evaluate(
                                        null,
                                        input,
                                        planDefinition,
                                        subject,
                                        it,
                                    )
                                }
                                ?.takeIf { it.isNotEmpty() }
                                ?.let { evaluatedValue ->
                                    FCTLogger.d("${dynamicValue.path}, evaluatedValue: $evaluatedValue")
                                    TerserUtil.setFieldByFhirPath(
                                        FhirContext.forR4Cached(),
                                        dynamicValue.path.removePrefix("${definition.kind.display}."),
                                        carePlan,
                                        evaluatedValue.first(),
                                    )
                                }
                        } else {
                            throw UnsupportedOperationException("${definition.kind} not supported")
                        }
                    }
                }
            }
        }

        //val carePlanTasks = carePlan.contained.filterIsInstance<Task>()
        carePlan.cleanPlanDefinitionCanonical()
        val dependents = extractDependents(carePlan, relatedEntityLocationTags)

        /*if (carePlanTasks.isNotEmpty()) {
            fhirResourceUtil.updateUpcomingTasksToDue(
                subject = subject.asReference(),
                taskResourcesToFilterBy = carePlanTasks,
            )
        }*/

        return WorkflowResponse(
            error = null,
            result = listOf(carePlan.encodeResourceToString()) + dependents.map { it.encodeResourceToString() }
        )
    }

    private suspend fun loadFromAppDatabase(logicalId: String): Resource {

        return resourceCache.getOrPut(logicalId) {
            val activeDevice =
                DeviceManager.getActiveDevice() ?: throw NullPointerException("No device available")
            DeviceManager.getActivePackage().value ?: throw NullPointerException("No package selected")

            val response = QueryResponse.build(
                activeDevice.runAppDBQuery(
                    QueryRequest(
                        database = DeviceDBConfigPersistence.RESOURCE_DB,
                        query = "SELECT * FROM ResourceEntity WHERE resourceId='$logicalId' LIMIT 1",
                        limit = 1
                    ).asJSONString()
                )
            )

            if (response.error != null) {
                throw RuntimeException(response.error)
            }

            if (response.data.isEmpty()) {
                throw NotFoundException("No resource found with this $logicalId id")
            }

            val serializedColumn = response.columns.first {
                it.name == "serializedResource"
            }

            response
                .data
                .first()
                .data[response.columns.indexOf(serializedColumn)]
                .data!!
                .decodeResourceFromString()
        }
    }

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

    private fun PlanDefinition.PlanDefinitionActionComponent.activityDefinition(
        planDefinition: PlanDefinition,
    ) =
        planDefinition.contained
            .filter { it.resourceType == ResourceType.ActivityDefinition }
            .first { it.logicalId == this.definitionCanonicalType.value } as ActivityDefinition

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
        val count =
            if (isLegacyPlanDefinition || !timing.repeat.hasCount()) 1 else timing.repeat.count

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
                        } else {
                            carePlan.period.end
                        }
                }
                .also { taskPeriods.add(it) }
        }

        return taskPeriods
    }

    private fun extractTaskPeriodsFromDosage(
        dosage: List<Dosage>,
        carePlan: CarePlan,
    ): List<Period> {
        val taskPeriods = mutableListOf<Period>()
        dosage
            .flatMap { extractTaskPeriodsFromTiming(it.timing, carePlan) }
            .also { taskPeriods.addAll(it) }

        return taskPeriods
    }

    private fun evaluateToDate(base: Base?, expression: String): BaseDateTimeType? =
        base?.let { fhirPathEngine.evaluate(it, expression).firstOrNull()?.dateTimeValue() }

    private fun CarePlan.cleanPlanDefinitionCanonical() {
        val canonicalValue = this.instantiatesCanonical.first().value
        if (canonicalValue.contains('/').not()) {
            this.instantiatesCanonical = listOf(CanonicalType("PlanDefinition/$canonicalValue"))
        }
    }

    private fun extractDependents(
        carePlan: CarePlan,
        relatedEntityLocationTags: List<Coding>
    ): List<Resource> {

        // Save embedded resources inside as independent entries, clear embedded and save carePlan
        val dependents = carePlan.contained.map { it }

        carePlan.contained.clear()

        dependents.forEach {
            relatedEntityLocationTags.forEach(it.meta::addTag)
        }

        return dependents
    }

}