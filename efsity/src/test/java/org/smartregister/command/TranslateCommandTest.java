package org.smartregister.command;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TranslateCommandTest {

  private TranslateCommand translateCommand;

  @BeforeEach
  public void setUp() {
    translateCommand = new TranslateCommand();
  }

  @Test
  public void testRunInvalidMode() {
    translateCommand.mode = "invalid_mode";
    assertThrows(RuntimeException.class, () -> translateCommand.run());
  }

  @Test
  public void testRunExtractWithInvalidInputPath() {
    translateCommand.mode = "extract";
    translateCommand.resourceFile = "invalid_path.json";
    assertThrows(RuntimeException.class, () -> translateCommand.run());
  }

  @Test
  public void testRunExtract() throws IOException {
    // Assuming you have a valid JSON file for testing
    Path rawQuestionnairePath = Paths.get("src/test/resources/raw_questionnaire.json");
    Path tempRawQuestionnaire = Files.createTempFile("temp_raw_questionnaire", ".json");
    Files.copy(rawQuestionnairePath, tempRawQuestionnaire, StandardCopyOption.REPLACE_EXISTING);

    Path defaultPropertiesPath = Paths.get("src/test/resources/strings_default.properties");
    Path tempDefaultPropertiesPath = Files.createTempFile("temp_strings_default", ".properties");

    translateCommand.mode = "extract";
    translateCommand.resourceFile = tempRawQuestionnaire.toString();
    translateCommand.translationFile = tempDefaultPropertiesPath.toString();
    translateCommand.extractionType = "fhirContent";

    assertDoesNotThrow(() -> translateCommand.run());

    Properties existingProperties = new Properties();
    InputStream defaultPropertiesInput = new FileInputStream(defaultPropertiesPath.toFile());
    existingProperties.load(defaultPropertiesInput);

    Properties newProperties = new Properties();
    InputStream tempDefaultPropertiesInput =
        new FileInputStream(tempDefaultPropertiesPath.toFile());
    newProperties.load(tempDefaultPropertiesInput);

    // Compare the contents of the two files
    assertEquals(existingProperties, newProperties, "File contents are similar.");
    // Clean up temporary resources
    tempRawQuestionnaire.toFile().delete();
    tempDefaultPropertiesPath.toFile().delete();
  }

  @Test
  public void testRunMerge() throws IOException {
    Path rawQuestionnairePath = Paths.get("src/test/resources/raw_questionnaire.json");
    Path tempRawQuestionnaire = Files.createTempFile("temp_raw_questionnaire", ".json");
    Files.copy(rawQuestionnairePath, tempRawQuestionnaire, StandardCopyOption.REPLACE_EXISTING);

    Path mergedQuestionnairePath = Paths.get("src/test/resources/merged_questionnaire.json");

    Path frPropertiesPath = Paths.get("src/test/resources/strings_fr.properties");

    translateCommand.mode = "merge";
    translateCommand.resourceFile = tempRawQuestionnaire.toString();
    translateCommand.translationFile = frPropertiesPath.toString();
    translateCommand.locale = "fr";

    assertDoesNotThrow(() -> translateCommand.run());

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rawQuestionnaire =
        objectMapper.readTree(
            Files.newBufferedReader(tempRawQuestionnaire, StandardCharsets.UTF_8));
    JsonNode mergedQuestionnaire =
        objectMapper.readTree(
            Files.newBufferedReader(mergedQuestionnairePath, StandardCharsets.UTF_8));

    // Compare the contents of the two nodes
    assertEquals(rawQuestionnaire, mergedQuestionnaire, "File merged as expected");
    tempRawQuestionnaire.toFile().delete();
  }
}
