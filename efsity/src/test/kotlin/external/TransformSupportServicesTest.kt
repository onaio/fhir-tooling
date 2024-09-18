package org.smartregister.fhir.structuremaptool

import io.mockk.mockk
import kotlin.test.Test
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.EpisodeOfCare
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.RiskAssessment
import org.hl7.fhir.r4.model.TimeType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.smartregister.external.TransformSupportServices

class TransformSupportServicesTest {
  lateinit var transformSupportServices: TransformSupportServices

  @BeforeEach
  fun setUp() {
    transformSupportServices = TransformSupportServices(mockk())
  }

  @Test
  fun `createType() should return RiskAssessmentPrediction when given RiskAssessment_Prediction`() {
    assertTrue(
      transformSupportServices.createType("", "RiskAssessment_Prediction")
        is RiskAssessment.RiskAssessmentPredictionComponent,
    )
  }

  @Test
  fun `createType() should return ImmunizationProtocol when given Immunization_VaccinationProtocol`() {
    assertTrue(
      transformSupportServices.createType("", "Immunization_AppliedProtocol")
        is Immunization.ImmunizationProtocolAppliedComponent,
    )
  }

  @Test
  fun `createType() should return ImmunizationReaction when given Immunization_Reaction`() {
    assertTrue(
      transformSupportServices.createType("", "Immunization_Reaction")
        is Immunization.ImmunizationReactionComponent,
    )
  }

  @Test
  fun `createType() should return Diagnosis when given EpisodeOfCare_Diagnosis`() {
    assertTrue(
      transformSupportServices.createType("", "EpisodeOfCare_Diagnosis")
        is EpisodeOfCare.DiagnosisComponent,
    )
  }

  @Test
  fun `createType() should return Diagnosis when given Encounter_Diagnosis`() {
    assertTrue(
      transformSupportServices.createType("", "Encounter_Diagnosis")
        is Encounter.DiagnosisComponent,
    )
  }

  @Test
  fun `createType() should return EncounterParticipant when given Encounter_Participant`() {
    assertTrue(
      transformSupportServices.createType("", "Encounter_Participant")
        is Encounter.EncounterParticipantComponent,
    )
  }

  @Test
  fun `createType() should return CarePlanActivity when given CarePlan_Activity`() {
    assertTrue(
      transformSupportServices.createType("", "CarePlan_Activity")
        is CarePlan.CarePlanActivityComponent,
    )
  }

  @Test
  fun `createType() should return CarePlanActivityDetail when given CarePlan_ActivityDetail`() {
    assertTrue(
      transformSupportServices.createType("", "CarePlan_ActivityDetail")
        is CarePlan.CarePlanActivityDetailComponent,
    )
  }

  @Test
  fun `createType() should return PatientLink when given Patient_Link`() {
    assertTrue(
      transformSupportServices.createType("", "Patient_Link") is Patient.PatientLinkComponent,
    )
  }

  @Test
  fun `createType() should return ObservationComponentComponent when given Observation_Component`() {
    assertTrue(
      transformSupportServices.createType("", "Observation_Component")
        is Observation.ObservationComponentComponent,
    )
  }

  @Test
  fun `createType() should return Time when given time`() {
    assertTrue(transformSupportServices.createType("", "time") is TimeType)
  }

  @Test
  fun `createResource() should add resource into output when given Patient and atRootOfTransForm as True`() {
    assertEquals(transformSupportServices.outputs.size, 0)
    transformSupportServices.createResource("", Patient(), true)
    assertEquals(transformSupportServices.outputs.size, 1)
  }

  @Test
  fun `createResource() should not add resource into output when given Patient and atRootOfTransForm as False`() {
    assertEquals(transformSupportServices.outputs.size, 0)
    transformSupportServices.createResource("", Patient(), false)
    assertEquals(transformSupportServices.outputs.size, 0)
  }

  @Test
  fun `resolveReference should throw FHIRException when given url`() {
    assertThrows(FHIRException::class.java) {
      transformSupportServices.resolveReference("", "https://url.com")
    }
  }

  @Test
  fun `performSearch() should throw FHIRException this is not supported yet when given url`() {
    assertThrows(FHIRException::class.java) {
      transformSupportServices.performSearch("", "https://url.com")
    }
  }
}
