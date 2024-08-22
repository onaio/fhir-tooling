package org.smartregister.fhir.structuremaptool

import org.hl7.fhir.r4.model.StringType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class FHIRPathEngineHostServicesTest {

    @Test
    fun testResolveConstant() {
        val appContext = mapOf("test" to StringType("Test Value"))
        val result = FHIRPathEngineHostServices.resolveConstant(appContext, "test", false)

        assertNotNull(result)
        assertEquals("Test Value", (result as StringType).value)
    }

    @Test
    fun testResolveConstant_cache() {
        // Set up the application context with a constant value
        val appContext = mapOf("test" to StringType("Test Value"))

        // First call: Resolves and caches the constant value
        FHIRPathEngineHostServices.resolveConstant(appContext, "test", false)

        // Second call: Should retrieve the value from the cache
        val result = FHIRPathEngineHostServices.resolveConstant(appContext, "test", false)

        // Verify that the result is not null and matches the expected value
        assertNotNull(result, "The resolved constant should not be null")
        assertEquals("Test Value", (result as StringType).value, "The resolved constant should match the expected value")
    }


    @Test
    fun testLog() {
        val logResult = FHIRPathEngineHostServices.log("Test log message", mutableListOf())
        assertTrue(logResult)
    }

    @Test
    fun testResolveFunction_unsupported() {
        val exception = assertThrows(UnsupportedOperationException::class.java) {
            FHIRPathEngineHostServices.resolveFunction("testFunction")
        }
        assertEquals("Function testFunction is not yet implemented.", exception.message)
    }

    @Test
    fun testResolveValueSet_unsupported() {
        val exception = assertThrows(UnsupportedOperationException::class.java) {
            FHIRPathEngineHostServices.resolveValueSet(null, "http://example.com")
        }
        assertEquals("resolveValueSet is not yet implemented.", exception.message)
    }

    @Test
    fun testResolveReference_unsupported() {
        val exception = assertThrows(UnsupportedOperationException::class.java) {
            FHIRPathEngineHostServices.resolveReference(null, "http://example.com")
        }
        assertEquals("resolveReference is not yet implemented.", exception.message)
    }

    @Test
    fun testCheckFunctionThrowsUnsupportedOperationException() {
        val exception = assertThrows(UnsupportedOperationException::class.java) {
            FHIRPathEngineHostServices.checkFunction(null, "testFunction", mutableListOf())
        }
        assertEquals("checkFunction is not yet implemented.", exception.message)
    }

    @Test
    fun testExecuteFunctionThrowsUnsupportedException() {
        val exception = assertThrows(UnsupportedOperationException::class.java) {
            FHIRPathEngineHostServices.executeFunction(null, mutableListOf(), "testFunction", mutableListOf())
        }
        assertEquals("executeFunction is not yet implemented.", exception.message)
    }
}