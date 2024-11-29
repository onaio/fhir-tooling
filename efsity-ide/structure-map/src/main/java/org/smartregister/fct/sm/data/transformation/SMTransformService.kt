package org.smartregister.fct.sm.data.transformation

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.utils.StructureMapUtilities
import org.smartregister.fct.engine.data.helper.TransformSupportServices
import org.smartregister.fct.engine.util.decodeResourceFromString
import org.smartregister.fct.logger.FCTLogger

class SMTransformService internal constructor(
    private val transformSupportServices: TransformSupportServices,
    private val simpleWorkerContext: SimpleWorkerContext,
) {

    private lateinit var structureMapUtilities: StructureMapUtilities
    private lateinit var jsonParser: IParser

    suspend fun init() = withContext(Dispatchers.IO) {
        structureMapUtilities = StructureMapUtilities(simpleWorkerContext, transformSupportServices)
        jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
    }

    fun transform(map: String, source: String? = null): Result<Bundle> {
        return try {
            val structureMap = structureMapUtilities.parse(map, "")

            val targetResource = Bundle().apply {
                addEntry().apply {
                    resource = structureMap
                }
            }

            if (source != null) {
                val clazz = source.decodeResourceFromString<Resource>().javaClass
                val baseElement = jsonParser.parseResource(clazz, source)
                structureMapUtilities.transform(
                    simpleWorkerContext,
                    baseElement,
                    structureMap,
                    targetResource
                )
            }

            Result.success(targetResource)
        } catch (ex: Throwable) {
            FCTLogger.e(ex)
            Result.failure(ex)
        }
    }
}