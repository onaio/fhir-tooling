package org.smartregister.fct.rules.util

import androidx.compose.ui.text.buildAnnotatedString
import org.smartregister.fct.rules.domain.model.MethodInfo

internal object RulesEngineMethods {

    val methodList = listOf(

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("extractValue(")
                append("base: ")
                appendBold("Base?")
                append(", expression: ")
                appendBold("String): String")
            },
            description = "Function to extract value based on the provided FHIR path [expression] on the given [base].\n" +
                          "@return the value of the first item in the returned list of [Base] as String, empty otherwise."
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("extractData(")
                append("base: ")
                appendBold("Base")
                append(", expressions: ")
                appendBold("Map<String, String>): Map<String, List<Base>>")
            },
            description = ""
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("extractData(")
                append("base: ")
                appendBold("Base")
                append(", expression: ")
                appendBold("String): List<Base>")
            },
            description = "Function to extract value for the [base] using the on the provided FHIR path [expression].\n" +
                          "@return a list of [Base]."
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("translate(")
                append("value: ")
                appendBold("String): String")
            },
            description = "This function creates a property key from the string [value] and uses the key to retrieve the correct translation from the string.properties file."
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("retrieveRelatedResources(")
                append("resource: ")
                appendBold("Resource")
                append(", relatedResourceKey: ")
                appendBold("String")
                append(", referenceFhirPathExpression: ")
                appendBold("String?")
                append(", relatedResourcesMap: ")
                appendBold("Map<String, List<Resource>>? = null): List<Resource>")
            },
            description = "This method retrieves a list of relatedResources for a given resource from the facts map It" +
                    "fetches a list of facts of the given [relatedResourceKey] then iterates through this list in" +
                    "order to return a list of all resources whose subject reference matches the logical Id of the" +
                    "[resource]\n" +
                    "\n" +
                    "@param resource The parent resource for which the related resources will be retrieved\n" +
                    "@param relatedResourceKey The key representing the relatedResources in the map\n" +
                    "@param referenceFhirPathExpression A fhir path expression used to retrieve the subject\n" +
                    "reference Id from the related resources"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("retrieveParentResource(")
                append("childResource: ")
                appendBold("Resource")
                append(", parentResourceType: ")
                appendBold("String")
                append(", fhirPathExpression: ")
                appendBold("String): Resource?")
            },
            description = "This method retrieve a parentResource for a given relatedResource from the facts map It " +
                    "fetches a list of facts of the given [parentResourceType] then iterates through this list in " +
                    "order to return a resource whose logical id matches the subject reference retrieved via " +
                    "fhirPath from the [childResource]\n" +
                    "- The logical Id of the parentResource [parentResourceType]\n" +
                    "- The ResourceType the parentResources belong to [fhirPathExpression]\n" +
                    "- A fhir path expression used to retrieve the logical Id from the parent resources"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("evaluateToBoolean(")
                append("resources: ")
                appendBold("List<Resource>?")
                append(", conditionalFhirPathExpression: ")
                appendBold("String")
                append(", matchAll: ")
                appendBold("Boolean = false): Boolean")
            },
            description = "This function returns a true or false value if any ( [matchAll]= false) or all ( [matchAll]=" +
                    " true) of the [resources] satisfy the [conditionalFhirPathExpression] provided\n" +
                    "\n" +
                    " [resources] List of resources the expressions are run against [conditionalFhirPathExpression]" +
                    " An expression to run against the provided resources [matchAll] When true the function checks" +
                    " whether all of the resources fulfill the expression provided\n" +
                    "\n" +
                    "When false the function checks whether any of the resources fulfills the expression provided"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("mapResourcesToLabeledCSV(")
                append("resources: ")
                appendBold("List<Resource>?")
                append(", fhirPathExpression: ")
                appendBold("String")
                append(", label: ")
                appendBold("String")
                append(", matchAllExtraConditions: ")
                appendBold("Boolean? = false")
                append(", vararg extraConditions: ")
                appendBold("Any? = emptyArray()): String")
            },
            description = "This function transform the provided [resources] into a list of [label] given that the " +
                    "[fhirPathExpression] for each of the resources is evaluated to true.\n" +
                    "\n" +
                    "Example: To retrieve a list of household member icons, find Patients aged 5yrs and below then " +
                    "return Comma Separated Values of 'CHILD' (to be serialized into [ServiceMemberIcon]) for " +
                    "each."
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("mapResourceToLabeledCSV(")
                append("resource: ")
                appendBold("Resource")
                append(", fhirPathExpression: ")
                appendBold("String")
                append(", label: ")
                appendBold("String): String")
            },
            description = "Transforms a [resource] into [label] if the [fhirPathExpression] is evaluated to true.\n" +
                    "\n" +
                    "Example: To retrieve the icon for household member who is a child, evaluate their age to be" +
                    "less than 5years, if 'true' return 'CHILD' (to be serialized to [ServiceMemberIcon])"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("extractAge(")
                append("resource: ")
                appendBold("Resource): String")
            },
            description = "Extracts a Patient/RelatedPerson's age"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("extractGender(")
                append("resource: ")
                append("Resource): String")
            },
            description = "Extracts and returns a translated string for the gender in the resource"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("extractDOB(")
                append("resource: ")
                appendBold("Resource")
                append(", dateFormat: ")
                appendBold("String): String")
            },
            description = "This function extracts a Patient/RelatedPerson's DOB from the FHIR resource"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("prettifyDate(")
                append("inputDate: ")
                appendBold("Date): String")
            },
            description = "This function takes [inputDate] and returns a difference (for examples 7 hours, 2 day, 5 " +
                    "months, 3 years etc)"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("daysPassed(")
                append("inputDate: ")
                appendBold("String")
                append(", pattern: ")
                appendBold("String = SDF_DD_MMM_YYYY): String")
            },
            description = "This function takes [inputDate] and returns a difference (for examples 15, 30 etc) between " +
                    "inputDate and the currentDate"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("prettifyDate(")
                append("inputDateString: ")
                appendBold("String): String")
            },
            description = "This function takes [inputDateString] like 2022-7-1 and returns a difference (for examples 7 " +
                    "hours ago, 2 days ago, 5 months ago, 3 years ago etc) [inputDateString] can give given as " +
                    "2022-02 or 2022"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("extractPractitionerInfoFromSharedPrefs(")
                append("practitionerKey: ")
                appendBold("String): String?")
            },
            description = "This function fetches assignment data separately that is; PractitionerId, " +
                    "PractitionerCareTeam, PractitionerOrganization and PractitionerLocation, using rules on the " +
                    "configs."
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("formatDate(")
                append("inputDate: ")
                appendBold("String")
                append(", inputDateFormat: ")
                appendBold("String")
                append(", expectedFormat: ")
                appendBold("String = SDF_E_MMM_DD_YYYY): String?")
            },
            description = "This function is responsible for formatting a date for whatever expectedFormat we need. It " +
                    "takes an [inputDate] string along with the [inputDateFormat] so it can convert it to the Date " +
                    "and then it gives output in expected Format, [expectedFormat] is by default (Example: Mon, " +
                    "Nov 5 2021)"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("formatDate(")
                append("date: ")
                appendBold("Date")
                append(", expectedFormat: ")
                appendBold("String = SDF_E_MMM_DD_YYYY): String")
            },
            description = "This function is responsible for formatting a date for whatever expectedFormat we need. It " +
                    "takes an input a [date] as input and then it gives output in expected Format, " +
                    "[expectedFormat] is by default (Example: Mon, Nov 5 2021)"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("generateRandomSixDigitInt(): Int")
            },
            description = "This function generates a random 6-digit integer between a hard-coded range. It may generate " +
                    "duplicate outputs on subsequent function calls.\n" +
                    "\n" +
                    "@return An Integer."
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("filterResources(")
                append("resources: ")
                appendBold("List<Resource>?")
                append(", conditionalFhirPathExpression: ")
                appendBold("String, ): List<Resource>")
            },
            description = "This function filters resources provided the condition extracted from the " +
                    "[conditionalFhirPathExpression] is met"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("filterResources(")
                append("resources: ")
                appendBold("List<Resource>?")
                append(", fhirPathExpression: ")
                appendBold("String")
                append(", dataType: ")
                appendBold("String")
                append(", value: ")
                appendBold("Any")
                append(", vararg compareToResult: ")
                appendBold("Any): List<Resource>?")
            },
            description = "Filters [Resource] s by comparing the given [value] against the value obtained after " +
                    "extracting data on each [Resource] using FHIRPath with the provided [fhirPathExpression]. The " +
                    "value is cast to the [DataType] to facilitate comparison using the [compareTo] function which " +
                    "returns zero if this object is equal to the specified other object, a negative number if it's " +
                    "less than other, or a positive number if it's greater than other.\n" +
                    "\n" +
                    "Please NOTE the order of comparison. The value extracted from FHIRPath is compared against " +
                    "the provided [value]"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("filterResourcesByJsonPath(")
                append("resources: ")
                appendBold("List<Resource>?")
                append(", jsonPathExpression: ")
                appendBold("String")
                append(", dataType: ")
                appendBold("String")
                append(", value: ")
                appendBold("Any")
                append(", vararg compareToResult: ")
                appendBold("Any): List<Resource>?")
            },
            description = ""
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("joinToString(")
                append("sourceString: ")
                appendBold("MutableList<String?>")
                append(", regex: ")
                appendBold("String = DEFAULT_REGEX")
                append(", separator: ")
                appendBold("String = DEFAULT_STRING_SEPARATOR): String")
            },
            description = "This function combines all string indexes to a list separated by the separator and regex " +
                    "defined by the content author"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("limitTo(")
                append("source: ")
                appendBold("List<Any>?")
                append(", limit: ")
                appendBold("Int?): List<Any>")
            },
            description = "This function returns a list of resources with a limit of [limit] resources"
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("mapResourcesToExtractedValues(")
                append("resources: ")
                appendBold("List<Resource>?")
                append(", fhirPathExpression: ")
                appendBold("String): List<Any>")
            },
            description = ""
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("computeTotalCount(")
                append("relatedResourceCounts: ")
                appendBold("List<RelatedResourceCount>?): Long")
            },
            description = ""
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("retrieveCount(")
                append("parentResourceId: ")
                appendBold("String")
                append(", relatedResourceCounts: ")
                appendBold("List<RelatedResourceCount>?): Long")
            },
            description = ""
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("sortResources(")
                append("resources: ")
                appendBold("List<Resource>?")
                append(", fhirPathExpression: ")
                appendBold("String")
                append(", dataType: ")
                appendBold("String")
                append(", order: ")
                appendBold("String = Order.ASCENDING.name): List<Resource>?")
            },
            description = "This function sorts [resources] by comparing the values extracted by FHIRPath using the " +
                    "[fhirPathExpression]. The [dataType] is required for ordering of the items. You can " +
                    "optionally specify the [Order] of sorting."
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("generateTaskServiceStatus(")
                append("task: ")
                appendBold("Task?): String")
            },
            description = ""
        ),

        MethodInfo(
            name = buildAnnotatedString {
                appendColor("fun ")
                appendBold("taskServiceStatusExist(")
                append("tasks: ")
                appendBold("List<Task>")
                append(", vararg serviceStatus: ")
                appendBold("String): Boolean")
            },
            description = ""
        ),
    )
}