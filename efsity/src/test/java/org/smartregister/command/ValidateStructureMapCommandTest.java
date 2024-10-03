package org.smartregister.command;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.smartregister.processor.StructureMapProcessor;

public class ValidateStructureMapCommandTest {

  private ValidateStructureMapCommand command;
  private Path tempDir;

  @BeforeEach
  public void setUp() throws IOException {
    command = new ValidateStructureMapCommand();
    tempDir = Files.createTempDirectory("test_project");
  }

  @AfterEach
  public void tearDown() throws IOException {
    Files.walk(tempDir)
        .sorted((a, b) -> b.compareTo(a))
        .forEach(
            p -> {
              try {
                Files.delete(p);
              } catch (IOException e) {
                e.printStackTrace();
              }
            });
  }

  @Test
  public void testIsProjectModeShouldReturnTrueForDirectory() throws IOException {
    assertTrue(command.isProjectMode(tempDir.toString()));
  }

  @Test
  public void testIsProjectModeShouldReturnFalseForFile() throws IOException {
    Path tempFile = Files.createTempFile(tempDir, "file", ".json");
    assertFalse(command.isProjectMode(tempFile.toString()));
  }

  @Test
  public void testMapIdentifierWithFilePath() throws IOException {
    String content = "{\"id\": \"questionnaire-id\"}";
    Path jsonFile = Files.createFile(tempDir.resolve("questionnaire.json"));
    Files.write(jsonFile, content.getBytes());

    Map<String, String> result = command.mapIdentifierWithFilePath(tempDir.toString());
    assertEquals(1, result.size());
    assertEquals(jsonFile.toString(), result.get("questionnaire-id"));
  }

  @Test
  public void testMapIdentifierWithFilePathWithNoJsonFiles() throws IOException {
    Map<String, String> result = command.mapIdentifierWithFilePath(tempDir.toString());
    assertTrue(result.isEmpty());
  }

  @Test
  public void testExtractIdFromJson() {
    String jsonContent = "{\"id\": \"test-id\"}";
    assertEquals("test-id", command.extractIdFromJson(jsonContent));
  }

  @Test
  public void testExtractIdFromJsonWithNoId() {
    String jsonContent = "{\"name\": \"test-name\"}";
    assertNull(command.extractIdFromJson(jsonContent));
  }

  @Test
  public void testValidateStructureMapForProject() throws IOException {
    // Simulating questionnaire and structure map files
    Path questionnaireFile = Files.createFile(tempDir.resolve("questionnaire.json"));
    Files.writeString(questionnaireFile, "{\"resourceType\": \"Questionnaire\", \"id\": \"q1\"}");

    Path structureMapFile = Files.createFile(tempDir.resolve("structureMap.json"));
    Files.writeString(structureMapFile, "{\"resourceType\": \"StructureMap\", \"id\": \"sm1\"}");

    // Simulating file mapping for testing
    Map<String, String> mockQuestionnaireMap = new HashMap<>();
    mockQuestionnaireMap.put("q1", questionnaireFile.toString());

    StructureMapProcessor structureMapProcessorMock = Mockito.mock(StructureMapProcessor.class);
    Mockito.when(structureMapProcessorMock.generateIdToFilepathMap())
        .thenReturn(Map.of("sm1", structureMapFile.toString()));

    // Call the method with mocks
    command.validateStructureMapForProject(tempDir.toString(), tempDir.toString(), false);
    // Check outputs (you can add assertions here to verify behavior based on how structure map
    // processing should work)
  }

  @Test
  public void testHasQuestionnaireReferenceToStructureMap() {
    JsonObject questionnaire = new JsonObject();
    JsonArray extensions = new JsonArray();
    JsonObject extension = new JsonObject();
    extension.addProperty(
        "url",
        "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap");
    JsonObject valueReference = new JsonObject();
    valueReference.addProperty("reference", "StructureMap/sm1");
    extension.add("valueReference", valueReference);
    extensions.add(extension);
    questionnaire.add("extension", extensions);

    assertTrue(command.hasQuestionnaireReferenceToStructureMap(questionnaire, "sm1"));
  }

  @Test
  public void testHasQuestionnaireReferenceToStructureMapNoMatch() {
    JsonObject questionnaire = new JsonObject();
    JsonArray extensions = new JsonArray();
    JsonObject extension = new JsonObject();
    extension.addProperty(
        "url",
        "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap");
    JsonObject valueReference = new JsonObject();
    valueReference.addProperty("reference", "StructureMap/sm2"); // different structureMap
    extension.add("valueReference", valueReference);
    extensions.add(extension);
    questionnaire.add("extension", extensions);

    assertFalse(command.hasQuestionnaireReferenceToStructureMap(questionnaire, "sm1"));
  }

  @Test
  public void testGetResourceFilesForDirectory() throws IOException {
    Path jsonFile = Files.createFile(tempDir.resolve("test-questionnaire.json"));
    Files.writeString(jsonFile, "{\"resourceType\": \"Questionnaire\"}");

    ArrayList<String> resourceFiles =
        ValidateStructureMapCommand.getResourceFiles(tempDir.toString());
    assertEquals(1, resourceFiles.size());
    assertTrue(resourceFiles.get(0).endsWith("test-questionnaire.json"));
  }

  @Test
  public void testGetResourceFilesForNonDirectoryFile() throws IOException {
    Path jsonFile = Files.createFile(tempDir.resolve("test-questionnaire.json"));
    Files.writeString(jsonFile, "{\"resourceType\": \"Questionnaire\"}");

    ArrayList<String> resourceFiles =
        ValidateStructureMapCommand.getResourceFiles(jsonFile.toString());
    assertEquals(1, resourceFiles.size());
    assertTrue(resourceFiles.get(0).endsWith("test-questionnaire.json"));
  }

  @Test
  public void testAddFhirResource() throws IOException {
    String jsonContent = "{\"resourceType\": \"Questionnaire\"}";
    File tempFile = Files.createTempFile(tempDir, "resource", ".json").toFile();
    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write(jsonContent);
    }

    ArrayList<String> resources = new ArrayList<>();
    ValidateStructureMapCommand.addFhirResource(tempFile.getAbsolutePath(), resources);

    assertEquals(1, resources.size());
    assertEquals(tempFile.getAbsolutePath(), resources.get(0));
  }

  @Test
  public void testAddFhirResourceWithNonQuestionnaireType() throws IOException {
    String jsonContent = "{\"resourceType\": \"Observation\"}";
    File tempFile = Files.createTempFile(tempDir, "resource", ".json").toFile();
    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write(jsonContent);
    }

    ArrayList<String> resources = new ArrayList<>();
    ValidateStructureMapCommand.addFhirResource(tempFile.getAbsolutePath(), resources);

    assertTrue(resources.isEmpty());
  }
}
