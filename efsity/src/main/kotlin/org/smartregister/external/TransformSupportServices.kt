/*
 * Copyright 2021-2023 Ona Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartregister.external;

import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.*
import org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentPredictionComponent
import org.hl7.fhir.r4.terminologies.ConceptMapEngine
import org.hl7.fhir.r4.utils.StructureMapUtilities.ITransformerServices
import org.smartregister.Main
import java.util.logging.Logger

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
