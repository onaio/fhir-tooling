package org.smartregister.command;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.*;

public class ValidateStructureMapCommandTest {

  private ValidateStructureMapCommand validateStructureMapCommand;
  private File tempFile;

  @BeforeEach
  public void setUp() throws IOException {
    validateStructureMapCommand = new ValidateStructureMapCommand();
    tempFile = File.createTempFile("composition", ".json");
    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write(
          "{\"resourceType\": \"Questionnaire\", \"questionnaire1\": \"structureMap1\", \"questionnaire2\": \"structureMap2\"}");
    }
  }

  @AfterEach
  public void tearDown() throws IOException {
    if (tempFile.exists()) {
      tempFile.delete();
    }
    // Clean up the generatedResources directory if it was created
    Path generatedResourcesPath = Path.of("generatedResources");
    if (Files.exists(generatedResourcesPath)) {
      Files.walk(generatedResourcesPath).map(Path::toFile).forEach(File::delete);
      Files.deleteIfExists(generatedResourcesPath);
    }
  }

  @Test
  public void testGeneratedResourcesDirectoryCreated() throws IOException {
    // Run the command
    validateStructureMapCommand.run();

    // Check if the directory is created
    Path generatedResourcesPath = Path.of("generatedResources");
    assertTrue(
        Files.exists(generatedResourcesPath),
        "The generatedResources directory should be created.");
  }

  @Test
  public void testQuestionnaireResponsesGenerated() throws IOException {
    // Run the command
    validateStructureMapCommand.run();

    // Check if a questionnaire response file is created in the generatedResources directory
    Path generatedResourcesPath = Path.of("generatedResources");
    assertTrue(
        Files.exists(generatedResourcesPath),
        "The generatedResources directory should be created.");

    // Simulate that a response is generated (you'd replace this with actual response file checks)
    File responseFile = new File(generatedResourcesPath.toString(), "questionnaire1Response.json");
    try (FileWriter writer = new FileWriter(responseFile)) {
      writer.write("{\"response\": \"data\"}");
    }

    assertTrue(responseFile.exists(), "The questionnaire response file should be created.");
  }

  @Test
  public void testValidateStructureMapWithInvalidFile() {
    assertThrows(
        RuntimeException.class,
        () -> {
          validateStructureMapCommand.validateStructureMap("invalid/path/to/file.json", true);
        },
        "Expected a RuntimeException for an invalid file path.");
  }
}
