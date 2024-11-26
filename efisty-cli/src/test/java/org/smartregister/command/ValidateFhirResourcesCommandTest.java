package org.smartregister.command;

import static org.junit.jupiter.api.Assertions.*;

import ca.uhn.fhir.parser.DataFormatException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import net.jimblackler.jsonschemafriend.GenerationException;
import net.jimblackler.jsonschemafriend.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smartregister.domain.FctFile;
import org.smartregister.util.FctUtils;

public class ValidateFhirResourcesCommandTest {

  private ValidateFhirResourcesCommand validateFhirResourcesCommand;

  @BeforeEach
  void setUp() {
    validateFhirResourcesCommand = new ValidateFhirResourcesCommand();
  }

  @Test
  void testValidateResource() throws IOException, ValidationException, GenerationException {
    // valid resource
    String input = "src/test/resources/raw_questionnaire.json";
    assertDoesNotThrow(() -> validateFhirResourcesCommand.validateFhirResources(input));
    // invalid resource
    String invalidInput = "src/test/resources/fhirConfigsJsonSchema.json";
    assertThrows(
        DataFormatException.class,
        () -> validateFhirResourcesCommand.validateFhirResources(invalidInput));
  }

  @Test
  void testValidateConfig() throws IOException, ValidationException, GenerationException {
    // valid config
    String input = "src/test/resources/profile_config.json";
    FctFile inputFile = FctUtils.readFile(input);
    validateFhirResourcesCommand.configSchema = "src/test/resources/fhirConfigsJsonSchema.json";
    assertDoesNotThrow(() -> validateFhirResourcesCommand.validateConfig(inputFile));

    // invalid config
    String invalidInput = "src/test/resources/raw_questionnaire.json";
    FctFile invalidInputFile = FctUtils.readFile(invalidInput);
    ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStreamCaptor));
    validateFhirResourcesCommand.validateConfig(invalidInputFile);
    System.setOut(System.out);
    assertTrue(outputStreamCaptor.toString().contains("Validation error"));
  }
}
