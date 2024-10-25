package org.smartregister.fct.engine.util

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BaseDateTimeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Group
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Practitioner
import org.hl7.fhir.r4.model.PrimitiveType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.model.Timing
import java.util.Locale

private val fhirR4JsonParser = FhirContext.forR4Cached().newJsonParser().setPrettyPrint(true)

fun Base?.valueToString(datePattern: String = "dd-MMM-yyyy"): String {
    return when {
        this == null -> return ""
        this.isDateTime -> (this as BaseDateTimeType).value.makeItReadable(datePattern)
        this.isPrimitive -> (this as PrimitiveType<*>).asStringValue()
        this is Coding -> display ?: code
        this is CodeableConcept -> this.stringValue()
        this is Quantity -> this.value.toPlainString()
        this is Timing ->
            this.repeat.let {
                it.period
                    .toPlainString()
                    .plus(" ")
                    .plus(
                        it.periodUnit.display.replaceFirstChar { char ->
                            if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
                        },
                    )
                    .plus(" (s)")
            }

        this is HumanName ->
            this.given.firstOrNull().let {
                (if (it != null) "${it.valueToString()} " else "").plus(this.family)
            }

        this is Patient ->
            this.nameFirstRep.nameAsSingleString +
                    ", " +
                    this.gender.name.first() +
                    ", " +
                    this.birthDate.yearsPassed()

        this is Practitioner -> this.nameFirstRep.nameAsSingleString
        this is Group -> this.name
        else -> this.toString()
    }
}

fun CodeableConcept.stringValue(): String =
    this.text ?: this.codingFirstRep.display ?: this.codingFirstRep.code

fun Resource.encodeResourceToString(parser: IParser = fhirR4JsonParser): String =
    parser.encodeResourceToString(this.copy())

fun StructureMap.encodeResourceToString(parser: IParser = fhirR4JsonParser): String =
    parser
        .encodeResourceToString(this)
        .replace("'months'", "\\\\'months\\\\'")
        .replace("'days'", "\\\\'days\\\\'")
        .replace("'years'", "\\\\'years\\\\'")
        .replace("'weeks'", "\\\\'weeks\\\\'")

fun <T> String.decodeResourceFromString(parser: IParser = fhirR4JsonParser): T =
    parser.parseResource(this) as T

fun Resource.asReference() = Reference().apply { this.reference = "$resourceType/$logicalId" }

fun Resource.referenceValue(): String = "$resourceType/$logicalId"

fun String.resourceClassType(): Class<out Resource> =
    FhirContext.forR4Cached().getResourceDefinition(this).implementingClass as Class<out Resource>

/**
 * A function that extracts only the UUID part of a resource logicalId.
 *
 * Examples:
 * 1. "Group/0acda8c9-3fa3-40ae-abcd-7d1fba7098b4/_history/2" returns
 *
 * ```
 *    "0acda8c9-3fa3-40ae-abcd-7d1fba7098b4".
 * ```
 * 2. "Group/0acda8c9-3fa3-40ae-abcd-7d1fba7098b4" returns "0acda8c9-3fa3-40ae-abcd-7d1fba7098b4".
 */
fun String.extractLogicalIdUuid() = this.substringAfter("/").substringBefore("/")


val Resource.logicalId: String
    get() {
        return this.idElement?.idPart.orEmpty()
    }

val Resource.readableResourceName: String
    get() =
        resourceType
            .name
            .split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])".toRegex())
            .joinToString(" ") {
                it.replaceFirstChar(Char::titlecase)
            }


val listOfAllFhirResources = listOf(
    "CapabilityStatement",
    "StructureDefinition",
    "ImplementationGuide",
    "SearchParameter",
    "MessageDefinition",
    "OperationDefinition",
    "CompartmentDefinition",
    "StructureMap",
    "GraphDefinition",
    "ExampleScenario",
    "CodeSystem",
    "ValueSet",
    "ConceptMap",
    "NamingSystem",
    "TerminologyCapabilities",
    "Provenance",
    "AuditEvent",
    "Consent",
    "Composition",
    "DocumentManifest",
    "DocumentReference",
    "CatalogEntry",
    "Basic",
    "Binary",
    "Bundle",
    "Linkage",
    "MessageHeader",
    "OperationOutcome",
    "Parameters",
    "Subscription",
    "SubscriptionStatus",
    "SubscriptionTopic",
    "Patient",
    "Practitioner",
    "PractitionerRole",
    "RelatedPerson",
    "Person",
    "Group",
    "Organization",
    "OrganizationAffiliation",
    "HealthcareService",
    "Endpoint",
    "Location",
    "Substance",
    "BiologicallyDerivedProduct",
    "Device",
    "DeviceMetric",
    "NutritionProduct",
    "Task",
    "Appointment",
    "AppointmentResponse",
    "Schedule",
    "Slot",
    "VerificationResult",
    "Encounter",
    "EpisodeOfCare",
    "Flag",
    "List",
    "Library",
    "AllergyIntolerance",
    "AdverseEvent",
    "Condition",
    "Procedure",
    "FamilyMemberHistory",
    "ClinicalImpression",
    "DetectedIssue",
    "Observation",
    "Media",
    "DiagnosticReport",
    "Specimen",
    "BodyStructure",
    "ImagingStudy",
    "QuestionnaireResponse",
    "MolecularSequence",
    "MedicationRequest",
    "MedicationAdministration",
    "MedicationDispense",
    "MedicationStatement",
    "Medication",
    "MedicationKnowledge",
    "Immunization",
    "ImmunizationEvaluation",
    "ImmunizationRecommendation",
    "CarePlan",
    "CareTeam",
    "Goal",
    "ServiceRequest",
    "NutritionOrder",
    "VisionPrescription",
    "RiskAssessment",
    "RequestGroup",
    "Communication",
    "CommunicationRequest",
    "DeviceRequest",
    "DeviceUseStatement",
    "GuidanceResponse",
    "SupplyRequest",
    "SupplyDelivery",
    "Coverage",
    "CoverageEligibilityRequest",
    "CoverageEligibilityResponse",
    "EnrollmentRequest",
    "EnrollmentResponse",
    "Claim",
    "ClaimResponse",
    "Invoice",
    "PaymentNotice",
    "PaymentReconciliation",
    "Account",
    "ChargeItem",
    "ChargeItemDefinition",
    "Contract",
    "ExplanationOfBenefit",
    "InsurancePlan",
    "ResearchStudy",
    "ResearchSubject",
    "ActivityDefinition",
    "DeviceDefinition",
    "EventDefinition",
    "ObservationDefinition",
    "PlanDefinition",
    "Questionnaire",
    "SpecimenDefinition",
    "Citation",
    "Evidence",
    "EvidenceReport",
    "EvidenceVariable",
    "Measure",
    "MeasureReport",
    "TestScript",
    "TestReport",
    "MedicinalProductDefinition",
    "PackagedProductDefinition",
    "AdministrableProductDefinition",
    "ManufacturedItemDefinition",
    "Ingredient",
    "ClinicalUseDefinition",
    "RegulatedAuthorization",
    "SubstanceDefinition",
)