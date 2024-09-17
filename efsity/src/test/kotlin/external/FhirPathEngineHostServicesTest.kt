package external

import org.hl7.fhir.r4.model.StringType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.smartregister.external.FhirPathEngineHostServices

class FhirPathEngineHostServicesTest {

  @Test
  fun `test Resolve Constant with Valid Key returns Value`() {
    val appContext = mapOf("test" to mutableListOf(StringType("Test Value")))
    val result = FhirPathEngineHostServices.resolveConstant(appContext, "test", false)

    assertNotNull(result)
    assertTrue(result is MutableList<*>)
    assertEquals("Test Value", (result!![0] as StringType).value)
  }

  @Test
  fun `test Resolve Constant with Invalid Key returns Null`() {
    val appContext = mapOf("test" to mutableListOf(StringType("Test Value")))
    val result = FhirPathEngineHostServices.resolveConstant(appContext, "invalidKey", false)

    assertNull(result)
  }

  @Test
  fun `test Resolve Constant with NonMapAppContext returns Null`() {
    val appContext = "invalidAppContext"
    val result = FhirPathEngineHostServices.resolveConstant(appContext, "test", false)

    assertNull(result)
  }

  @Test
  fun `test Resolve Constant Type throws Unsupported Operation Exception`() {
    assertThrows(UnsupportedOperationException::class.java) {
      FhirPathEngineHostServices.resolveConstantType(null, "test")
    }
  }

  @Test
  fun `test Log throws Unsupported Operation Exception`() {
    assertThrows(UnsupportedOperationException::class.java) {
      FhirPathEngineHostServices.log("Test log message", mutableListOf())
    }
  }

  @Test
  fun `test Resolve Function throws Unsupported Operation Exception`() {
    assertThrows(UnsupportedOperationException::class.java) {
      FhirPathEngineHostServices.resolveFunction("testFunction")
    }
  }

  @Test
  fun `test Check Function throws Unsupported Operation Exception`() {
    assertThrows(UnsupportedOperationException::class.java) {
      FhirPathEngineHostServices.checkFunction(null, "testFunction", mutableListOf())
    }
  }

  @Test
  fun `test ExecuteFunction throws Unsupported Operation Exception`() {
    assertThrows(UnsupportedOperationException::class.java) {
      FhirPathEngineHostServices.executeFunction(
        null,
        mutableListOf(),
        "testFunction",
        mutableListOf()
      )
    }
  }

  @Test
  fun `test Resolve Reference throws Unsupported Operation Exception`() {
    assertThrows(UnsupportedOperationException::class.java) {
      FhirPathEngineHostServices.resolveReference(null, "http://example.com", null)
    }
  }

  @Test
  fun `test Conforms To Profile throws Unsupported Operation Exception`() {
    assertThrows(UnsupportedOperationException::class.java) {
      FhirPathEngineHostServices.conformsToProfile(null, null, "http://example.com")
    }
  }

  @Test
  fun `test Resolve ValueSet throws Unsupported Operation Exception`() {
    assertThrows(UnsupportedOperationException::class.java) {
      FhirPathEngineHostServices.resolveValueSet(null, "http://example.com")
    }
  }
}
