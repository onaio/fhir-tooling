package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.smartregister.fhir.structuremaptool.determineFhirDataType
import kotlin.test.Test

class DetermineFhirDataTypeTest {

    @Test
    fun testDetermineFhirDataType() {
        // Test Null or Empty Input
        assertEquals("Invalid Input: Null or Empty String", determineFhirDataType(null))
        assertEquals("Invalid Input: Null or Empty String", determineFhirDataType(""))

        // Test Boolean
        assertEquals("Boolean", determineFhirDataType("true"))
        assertEquals("Boolean", determineFhirDataType("false"))

        // Test Integer
        assertEquals("Integer", determineFhirDataType("123"))
        assertEquals("Integer", determineFhirDataType("-456"))

        // Test Decimal
        assertEquals("Decimal", determineFhirDataType("123.456"))
        assertEquals("Decimal", determineFhirDataType("-0.789"))

        // Test Date
        assertEquals("Date", determineFhirDataType("2023-08-23"))

        // Test DateTime
        assertEquals("DateTime", determineFhirDataType("2023-08-23T14:30:00+01:00"))

        // Test Instant
        assertEquals("Instant", determineFhirDataType("2023-08-23T14:30:00.123Z"))

        // Test Quantity
        assertEquals("Quantity", determineFhirDataType("70 kg"))

        // Test Coding
        assertEquals("Coding", determineFhirDataType("12345|"))

        // Test Reference
        assertEquals("Reference", determineFhirDataType("Patient/123"))

        // Test Period
        assertEquals("Period", determineFhirDataType("2023-01-01/2023-12-31"))

        // Test Range
        assertEquals("Range", determineFhirDataType("10-20"))

        // Test Annotation
        assertEquals("Annotation", determineFhirDataType("Note: Patient is recovering well"))

        // Test Attachment

        // Test Base64Binary
        assertEquals("Base64Binary", determineFhirDataType("QmFzZTY0QmluYXJ5"))

        // Test ContactPoint
        assertEquals("ContactPoint", determineFhirDataType("+123456789"))

        // Test HumanName
        assertEquals("HumanName", determineFhirDataType("John Doe"))

        // Test Address
        assertEquals("Address", determineFhirDataType("123 Main Street"))

        // Test Duration
        assertEquals("Duration", determineFhirDataType("1 hour"))

        // Test Money
        assertEquals("Money", determineFhirDataType("100.00 USD"))

        // Test Ratio
        assertEquals("Ratio", determineFhirDataType("1:1000"))

        // Test Signature
        // Test Identifier
        assertEquals("Identifier", determineFhirDataType("AB123-45"))

        // Test Uri
        assertEquals("Uri", determineFhirDataType("https://example.com"))

        // Test Uuid
        assertEquals("Uuid", determineFhirDataType("123e4567-e89b-12d3-a456-426614174000"))

        // Test Narrative
        assertEquals(
            "Narrative",
            determineFhirDataType("<div xmlns=\"http://www.w3.org/1999/xhtml\">Patient narrative</div>")
        )

        // Test String as Default Case
        assertEquals("String", determineFhirDataType("Unmatched string"))
    }
}