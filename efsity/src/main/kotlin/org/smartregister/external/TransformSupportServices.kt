/* (C)2021-2023 */
package org.smartregister.external

import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.AdverseEvent
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.EpisodeOfCare
import org.hl7.fhir.r4.model.Group
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.ResourceFactory
import org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentPredictionComponent
import org.hl7.fhir.r4.model.Task
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.terminologies.ConceptMapEngine
import org.hl7.fhir.r4.utils.StructureMapUtilities.ITransformerServices

/**
 * Copied from
 * https://github.com/opensrp/fhircore/blob/main/android/engine/src/main/java/org/smartregister/fhircore/engine/util/helper/TransformSupportServices.kt
 */
class TransformSupportServices constructor(private val simpleWorkerContext: SimpleWorkerContext) :
  ITransformerServices {

  private val outputs: MutableList<Base> = mutableListOf()

  override fun log(message: String) {
    // logger.info(message)
  }

  @Throws(FHIRException::class)
  override fun createType(appInfo: Any, name: String): Base {
    return when (name) {
      "RiskAssessment_Prediction" -> RiskAssessmentPredictionComponent()
      "Immunization_VaccinationProtocol" -> Immunization.ImmunizationProtocolAppliedComponent()
      "Immunization_Reaction" -> Immunization.ImmunizationReactionComponent()
      "EpisodeOfCare_Diagnosis" -> EpisodeOfCare.DiagnosisComponent()
      "Encounter_Diagnosis" -> Encounter.DiagnosisComponent()
      "Encounter_Participant" -> Encounter.EncounterParticipantComponent()
      "Encounter_Location" -> Encounter.EncounterLocationComponent()
      "CarePlan_Activity" -> CarePlan.CarePlanActivityComponent()
      "CarePlan_ActivityDetail" -> CarePlan.CarePlanActivityDetailComponent()
      "Patient_Link" -> Patient.PatientLinkComponent()
      "Timing_Repeat" -> Timing.TimingRepeatComponent()
      "PlanDefinition_Action" -> PlanDefinition.PlanDefinitionActionComponent()
      "Group_Characteristic" -> Group.GroupCharacteristicComponent()
      "Observation_Component" -> Observation.ObservationComponentComponent()
      "Task_Input" -> Task.ParameterComponent()
      "Task_Output" -> Task.TaskOutputComponent()
      "Task_Restriction" -> Task.TaskRestrictionComponent()
      "AdverseEvent_SuspectEntity" -> AdverseEvent.AdverseEventSuspectEntityComponent()
      else -> ResourceFactory.createResourceOrType(name)
    }
  }

  override fun createResource(appInfo: Any, res: Base, atRootofTransform: Boolean): Base {
    if (atRootofTransform) outputs.add(res)
    return res
  }

  @Throws(FHIRException::class)
  override fun translate(appInfo: Any, source: Coding, conceptMapUrl: String): Coding {
    val cme = ConceptMapEngine(simpleWorkerContext)
    return cme.translate(source, conceptMapUrl)
  }

  @Throws(FHIRException::class)
  override fun resolveReference(appContext: Any, url: String): Base {
    throw FHIRException("resolveReference is not supported yet")
  }

  @Throws(FHIRException::class)
  override fun performSearch(appContext: Any, url: String): List<Base> {
    throw FHIRException("performSearch is not supported yet")
  }
}
