package org.smartregister.command;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import net.jimblackler.jsonschemafriend.GenerationException;
import net.jimblackler.jsonschemafriend.ValidationException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.smartregister.domain.FctFile;
import org.smartregister.util.FctUtils;

public class PublishFhirResourcesCommandTest {

  @InjectMocks private PublishFhirResourcesCommand publishFhirResourcesCommand;

  @TempDir static Path tempDirectory;

  @BeforeEach
  void setUp() {
    publishFhirResourcesCommand = new PublishFhirResourcesCommand();
  }

  @Test
  void testGetResourceFiles() throws IOException {
    // add folders and file to form sample project structure
    Path testFolder = Files.createDirectory(tempDirectory.resolve("testFileFolder"));
    Path projectFolder = Files.createDirectory(testFolder.resolve("testProject"));

    // create 3 folders in projectFolder
    Path questionnaireFolder = Files.createDirectory(projectFolder.resolve("questionnaires"));
    Path plansFolder = Files.createDirectory(projectFolder.resolve("plan_definitions"));
    Path structureMapsFolder = Files.createDirectory(projectFolder.resolve("structureMaps"));

    // create a file in each of the folders above
    Path questionnaireFile =
        Files.createFile(questionnaireFolder.resolve("patient_registration.json"));
    Files.write(
        questionnaireFile, "{\"resourceType\":\"Questionnaire\"}".getBytes(StandardCharsets.UTF_8));
    Path planFile = Files.createFile(plansFolder.resolve("anc_visit.json"));
    Files.write(planFile, "{\"resourceType\":\"PlanDefinition\"}".getBytes(StandardCharsets.UTF_8));
    Path structureMapFile =
        Files.createFile(structureMapsFolder.resolve("pregnancy_screening.json"));
    Files.write(
        structureMapFile, "{\"resourceType\":\"StructureMap\"}".getBytes(StandardCharsets.UTF_8));

    // get files in the folder
    ArrayList<String> resourceFiles =
        publishFhirResourcesCommand.getResourceFiles(projectFolder.toString());

    assertEquals(3, resourceFiles.size());
    assertTrue(resourceFiles.contains(questionnaireFile.toString()));
    assertTrue(resourceFiles.contains(planFile.toString()));
    assertTrue(resourceFiles.contains(structureMapFile.toString()));
  }

  @Test
  void testBuildResourceObject() throws IOException {
    Path testFolder = Files.createDirectory(tempDirectory.resolve("testObjectFolder"));
    Path resourceFile = Files.createFile(testFolder.resolve("group.json"));

    String sampleResource =
        "{\n"
            + "  \"resourceType\": \"Group\",\n"
            + "  \"id\": \"548060c9-8e9b-4b0d-88e7-925e9348fdae\",\n"
            + "  \"identifier\": [\n"
            + "    {\n"
            + "      \"use\": \"official\",\n"
            + "      \"value\": \"548060c9-8e9b-4b0d-88e7-925e9348fdae\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"active\": false,\n"
            + "  \"name\": \"Test Group\"\n"
            + "}";
    FileWriter writer = new FileWriter(String.valueOf(resourceFile));
    writer.write(sampleResource);
    writer.flush();
    writer.close();

    FctFile testFile = FctUtils.readFile(resourceFile.toString());
    JSONObject resourceObject =
        publishFhirResourcesCommand.buildResourceObject(testFile.getContent());

    // assert that object has request
    assertEquals(
        "{\"method\":\"PUT\",\"url\":\"Group/548060c9-8e9b-4b0d-88e7-925e9348fdae\"}",
        resourceObject.get("request").toString());

    // assert object has meta with version tag
    JSONObject resource = (JSONObject) resourceObject.get("resource");
    assertTrue(
        resource
            .get("meta")
            .toString()
            .contains(
                "{\"tag\":[{\"system\":\"https://smartregister.org/fct-release-version\",\"code\":\""));
  }

  @Test
  void testPublishResourcesValidationFalse()
      throws IOException, ValidationException, GenerationException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
    try (MockedStatic<ValidateFhirResourcesCommand> validateMock =
        Mockito.mockStatic(ValidateFhirResourcesCommand.class)) {
      validateMock
          .when(
              () ->
                  ValidateFhirResourcesCommand.validateFhirResources(
                      "src/test/resources/raw_questionnaire.json"))
          .thenAnswer(Answers.RETURNS_DEFAULTS);
      PublishFhirResourcesCommand mockPublishFhirResourcesCommand =
          mock(PublishFhirResourcesCommand.class);
      mockPublishFhirResourcesCommand.validateResource = "false";
      mockPublishFhirResourcesCommand.accessToken = "testAccessToken";
      mockPublishFhirResourcesCommand.projectFolder = "src/test/resources/raw_questionnaire.json";
      doNothing()
          .when(mockPublishFhirResourcesCommand)
          .postRequest(Mockito.anyString(), Mockito.anyString());
      doCallRealMethod().when(mockPublishFhirResourcesCommand).buildResources();
      mockPublishFhirResourcesCommand.buildResources();
    }
    System.setOut(System.out);
    String printedOutput = outputStream.toString().trim();
    assertTrue(printedOutput.contains("Without Validation"));
  }

  @Test
  void testPublishResourcesValidationTrue()
      throws IOException, ValidationException, GenerationException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
    try (MockedStatic<ValidateFhirResourcesCommand> validateMock =
        Mockito.mockStatic(ValidateFhirResourcesCommand.class)) {
      validateMock
          .when(
              () ->
                  ValidateFhirResourcesCommand.validateFhirResources(
                      "src/test/resources/raw_questionnaire.json"))
          .thenAnswer(Answers.RETURNS_DEFAULTS);
      PublishFhirResourcesCommand mockPublishFhirResourcesCommand =
          mock(PublishFhirResourcesCommand.class);
      mockPublishFhirResourcesCommand.validateResource = "true";
      mockPublishFhirResourcesCommand.accessToken = "testAccessToken";
      mockPublishFhirResourcesCommand.projectFolder = "src/test/resources/raw_questionnaire.json";
      doNothing()
          .when(mockPublishFhirResourcesCommand)
          .postRequest(Mockito.anyString(), Mockito.anyString());
      doCallRealMethod().when(mockPublishFhirResourcesCommand).buildResources();
      mockPublishFhirResourcesCommand.buildResources();
    }
    System.setOut(System.out);
    String printedOutput = outputStream.toString().trim();
    assertTrue(printedOutput.contains("Validating file"));
  }

  @Test
  void testGetFileName() {
    String profileFile = "householdProfile";
    assertEquals(
        publishFhirResourcesCommand.getFileName(profileFile), "household_profile_config.json");

    String registerFile = "sickChildRegister";
    assertEquals(
        publishFhirResourcesCommand.getFileName(registerFile), "sick_child_register_config.json");

    String translationFile = "strings_fr";
    assertEquals(
        publishFhirResourcesCommand.getFileName(translationFile), "strings_fr_config.properties");

    String basicFile = "navigation";
    assertEquals(publishFhirResourcesCommand.getFileName(basicFile), "navigation_config.json");
  }

  @Test
  void testGetDetails() {
    String obj =
        "{"
            + "\n       \"title\": \"PNC register configuration\","
            + "\n       \"focus\": {"
            + "\n            \"reference\": \"Binary/9aa6bbb6-df76-42a4-bdbe-72dc197378ca\","
            + "\n            \"identifier\": {"
            + "\n               \"value\": \"pncRegister\""
            + "\n        }}}";
    JSONObject testObject = new JSONObject(obj);

    HashMap<String, String> result = new HashMap<>();
    result.put("reference", "Binary/9aa6bbb6-df76-42a4-bdbe-72dc197378ca");
    result.put("name", "pnc_register_config.json");
    assertEquals(publishFhirResourcesCommand.getDetails(testObject), result);
  }

  @Test
  void testGetBinaryContent() throws IOException {
    String filename = "sample.json";
    Path testFolder = Files.createDirectory(tempDirectory.resolve("testBinaryFolder"));
    Path resourceFile = Files.createFile(testFolder.resolve(filename));
    String sampleResource =
        "{\"appId\":\"testApp\",\"configType\":\"application\",\"theme\":\"DEFAULT\",\"appTitle\":\"TestApp\"}";
    FileWriter writer = new FileWriter(String.valueOf(resourceFile));
    writer.write(sampleResource);
    writer.flush();
    writer.close();

    String actualResult =
        publishFhirResourcesCommand.getBinaryContent(filename, String.valueOf(testFolder));
    String expectedResult =
        "eyJhcHBJZCI6InRlc3RBcHAiLCJjb25maWdUeXBlIjoiYXBwbGljYXRpb24iLCJ0aGVtZSI6IkRFRkFVTFQiLCJhcHBUaXRsZSI6IlRlc3RBcHAifQo=";
    assertEquals(expectedResult, actualResult);
  }

  @Test
  void testPublishBinaries() throws IOException {
    Path testFolder = Files.createDirectory(tempDirectory.resolve("testPublishBinaryFolder"));
    Path compositionFile = Files.createFile(testFolder.resolve("composition_config.json"));
    Path applicationFile = Files.createFile(testFolder.resolve("application_config.json"));
    Path profilesFolder = Files.createDirectory(testFolder.resolve("profiles"));
    Path householdProfile =
        Files.createFile(profilesFolder.resolve("household_profile_config.json"));
    Path registersFolder = Files.createDirectory(testFolder.resolve("registers"));
    Path ancRegister = Files.createFile(registersFolder.resolve("anc_register_config.json"));

    String compositionString =
        "{\"resourceType\":\"Composition\",\"id\":\"bf131f26-5c94-4bd2-9c88-bfc673dcd27d\",\"section\":[{\"title\":\"Application configuration\",\"focus\":{\"reference\":\"Binary/98cc3379-454c-4820-bec3-06c1e0aee45e\",\"identifier\":{\"value\":\"application\"}}},{\"title\":\"Register configurations\",\"section\":[{\"title\":\"ANC register configuration\",\"focus\":{\"reference\":\"Binary/5321c50d-fd84-45ba-a1a9-7424c8f9e1fc\",\"identifier\":{\"value\":\"ancRegister\"}}}]},{\"title\":\"Profile configurations\",\"section\":[{\"title\":\"Household profile configuration\",\"focus\":{\"reference\":\"Binary/64c10d9b-ac39-4b85-8d00-f82e8f9f2211\",\"identifier\":{\"value\":\"householdProfile\"}}}]}]}";
    String applicationString =
        "{\"appId\":\"test\",\"configType\":\"application\",\"theme\":\"DEFAULT\",\"appTitle\":\"TestApp\",\"useDarkTheme\":false}";
    String householdProfileString =
        "{\"appId\":\"test\",\"configType\":\"profile\",\"id\":\"householdProfile\",\"fhirResource\":{\"baseResource\":{\"resource\":\"Group\"}}}";
    String ancRegisterString =
        "{\"appId\":\"test\",\"configType\":\"register\",\"id\":\"ancRegister\",\"fhirResource\":{\"baseResource\":{\"resource\":\"Patient\"}}}";

    FileWriter writer = new FileWriter(String.valueOf(compositionFile));
    writer.write(compositionString);
    writer.flush();
    writer = new FileWriter(String.valueOf(applicationFile));
    writer.write(applicationString);
    writer.flush();
    writer = new FileWriter(String.valueOf(householdProfile));
    writer.write(householdProfileString);
    writer.flush();
    writer = new FileWriter(String.valueOf(ancRegister));
    writer.write(ancRegisterString);
    writer.flush();
    writer.close();

    ArrayList<JSONObject> resources =
        publishFhirResourcesCommand.buildBinaries(
            String.valueOf(compositionFile), String.valueOf(testFolder));
    assertEquals(3, resources.size());
    assertEquals(
        resources.get(0).getJSONObject("request").getString("url"),
        "Binary/98cc3379-454c-4820-bec3-06c1e0aee45e");
    assertTrue(
        resources
            .get(1)
            .getJSONObject("resource")
            .getString("data")
            .startsWith("eyJhcHBJZCI6InRlc3Qi"));
    assertEquals(
        publishFhirResourcesCommand.getFCTReleaseVersion(),
        resources
            .get(2)
            .getJSONObject("resource")
            .getJSONObject("meta")
            .getJSONArray("tag")
            .getJSONObject(0)
            .getString("code"));
  }
}
