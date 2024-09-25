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

public class ValidateStructureMapCommandTest {

  private ValidateStructureMapCommand validateStructureMapCommand;
  private File tempFile;
  private Path tempDirectory;

  @BeforeEach
  public void setUp() throws IOException {
    validateStructureMapCommand = new ValidateStructureMapCommand();
    tempDirectory = Files.createTempDirectory("test");
    tempFile = Files.createFile(tempDirectory.resolve("composition.json")).toFile();

    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write(
          "{\n"
              + "  \"resourceType\": \"Composition\",\n"
              + "  \"section\": [\n"
              + "    {\n"
              + "      \"title\": \"Questionnaires\",\n"
              + "      \"section\": [{ \"title\": \"Questionnaire1\" }]\n"
              + "    },\n"
              + "    {\n"
              + "      \"title\": \"StructureMaps\",\n"
              + "      \"section\": [{ \"title\": \"StructureMap1\" }]\n"
              + "    }\n"
              + "  ]\n"
              + "}");
    }
  }

  @AfterEach
  public void tearDown() throws IOException {
    if (tempFile.exists()) {
      tempFile.delete();
    }
    if (Files.exists(tempDirectory)) {
      Files.walk(tempDirectory)
          .sorted((path1, path2) -> path2.compareTo(path1))
          .map(Path::toFile)
          .forEach(File::delete);
    }
  }

  @Test
  public void testIsProjectModeReturnsFalseForFile() {
    assertFalse(validateStructureMapCommand.isProjectMode(tempFile.toString()));
  }

  @Test
  public void testParseCompositionFile() throws IOException {
    JsonObject composition = validateStructureMapCommand.parseCompositionFile(tempFile.getPath());
    assertNotNull(composition);
    assertEquals("Composition", composition.get("resourceType").getAsString());
  }

  @Test
  public void testGetQuestionnairesFromComposition() throws IOException {
    JsonObject composition = validateStructureMapCommand.parseCompositionFile(tempFile.getPath());
    JsonArray questionnaires =
        validateStructureMapCommand.getQuestionnairesFromComposition(composition);
    assertNotNull(questionnaires);
    assertEquals(1, questionnaires.size());
    assertEquals(
        "Questionnaire1", questionnaires.get(0).getAsJsonObject().get("title").getAsString());
  }

  @Test
  public void testGetStructureMapsFromComposition() throws IOException {
    JsonObject composition = validateStructureMapCommand.parseCompositionFile(tempFile.getPath());
    JsonArray structureMaps =
        validateStructureMapCommand.getStructureMapsFromComposition(composition);
    assertNotNull(structureMaps);
    assertEquals(1, structureMaps.size());
    assertEquals(
        "StructureMap1", structureMaps.get(0).getAsJsonObject().get("title").getAsString());
  }

  @Test
  public void testGetQuestionnaireToStructureMapIdMap() {
    // Create a Questionnaire with an extension linking it to a StructureMap
    JsonArray questionnaires = new JsonArray();
    JsonObject questionnaire = new JsonObject();
    questionnaire.addProperty("title", "Questionnaire1");

    // Add the extension with a reference to the StructureMap
    JsonArray extensions = new JsonArray();
    JsonObject extension = new JsonObject();
    extension.addProperty(
        "url",
        "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap");

    JsonObject valueReference = new JsonObject();
    valueReference.addProperty("reference", "StructureMap/StructureMap1");

    extension.add("valueReference", valueReference);
    extensions.add(extension);

    questionnaire.add("extension", extensions);
    questionnaires.add(questionnaire);

    // Create a StructureMap
    JsonArray structureMaps = new JsonArray();
    JsonObject structureMap = new JsonObject();
    structureMap.addProperty("title", "StructureMap1");
    structureMaps.add(structureMap);

    // Expected map
    Map<String, String> expectedMap = new HashMap<>();
    expectedMap.put("Questionnaire1", "StructureMap1");

    // Test actual output from the method
    Map<String, String> actualMap =
        validateStructureMapCommand.getQuestionnaireToStructureMapIdMap(
            questionnaires, structureMaps);

    // Assert equality of expected and actual results
    assertEquals(expectedMap, actualMap);
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
    valueReference.addProperty("reference", "StructureMap/StructureMap1");
    extension.add("valueReference", valueReference);
    extensions.add(extension);
    questionnaire.add("extension", extensions);

    assertTrue(
        validateStructureMapCommand.hasQuestionnaireReferenceToStructureMap(
            questionnaire, "StructureMap1"));
  }

  @Test
  public void testAddFhirResourceAddsQuestionnaireResource() throws IOException {
    ArrayList<String> filesArray = new ArrayList<>();
    File questionnaireFile = Files.createFile(tempDirectory.resolve("questionnaire.json")).toFile();

    try (FileWriter writer = new FileWriter(questionnaireFile)) {
      writer.write("{\"resourceType\": \"Questionnaire\"}");
    }

    ValidateStructureMapCommand.addFhirResource(questionnaireFile.getAbsolutePath(), filesArray);
    assertEquals(1, filesArray.size());
    assertTrue(filesArray.get(0).contains("questionnaire.json"));
  }
}
