package org.smartregister.command;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.smartregister.util.FctUtils;

public class TranslateCommandTest {
  private Path tempCleanConfigsFolder;
  private TranslateCommand translateCommand;

  @BeforeEach
  public void setUp() throws IOException {

    tempCleanConfigsFolder = Files.createTempDirectory("temp_clean_configs_folder");
    Path cleanConfigsFolder = Paths.get("src/test/resources/clean_configs_folder");
    FctUtils.copyDirectoryContent(cleanConfigsFolder, tempCleanConfigsFolder);

    translateCommand = new TranslateCommand();
  }

  @AfterEach
  public void tearDown() {
    FctUtils.deleteDirectoryRecursively(tempCleanConfigsFolder);
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

    Properties existingProperties =
        FctUtils.readPropertiesFile(defaultPropertiesPath.toFile().getAbsolutePath());

    Properties newProperties =
        FctUtils.readPropertiesFile(tempDefaultPropertiesPath.toFile().getAbsolutePath());

    // Compare the contents of the two files
    assertEquals(existingProperties, newProperties, "File contents are similar.");
    // Clean up temporary resources
    tempRawQuestionnaire.toFile().delete();
    tempDefaultPropertiesPath.toFile().delete();
  }

  @Test
  public void testRunExtractConfigWithCleanConfigsFolderRunsSuccessfully() throws IOException {

    Path cleanConfigsFolder = tempCleanConfigsFolder;
    Path backupFolder = Files.createTempDirectory("temp_back_up_dir");
    FctUtils.copyDirectoryContent(cleanConfigsFolder, backupFolder);
    TranslateCommand translateCommandSpy = Mockito.spy(translateCommand);
    translateCommandSpy.mode = "extract";
    translateCommandSpy.extractionType = "configs";
    translateCommandSpy.resourceFile = cleanConfigsFolder.toString();

    Path frPropertiesPathOriginal = Paths.get("src/test/resources/strings_fr.properties");
    Path frPropertiesPathTest =
        tempCleanConfigsFolder.resolve(frPropertiesPathOriginal.getFileName());
    Files.copy(frPropertiesPathOriginal, frPropertiesPathTest, StandardCopyOption.REPLACE_EXISTING);
    translateCommandSpy.translationFile = frPropertiesPathTest.toString();
    translateCommandSpy.run();
    Mockito.verify(translateCommandSpy, Mockito.atLeast(2))
        .copyDirectoryContent(Mockito.any(), Mockito.any());
    Mockito.verify(translateCommandSpy, Mockito.atLeast(1))
        .deleteDirectoryRecursively(Mockito.any());
  }

  @Test
  public void testRunExtractConfigWithDirtyConfigsFolderDeletesTempFileOnFailure()
      throws RuntimeException {
    Path dirtyConfigsFolder = Paths.get("src/test/resources/dirty_configs_folder");
    TranslateCommand translateCommandSpy = Mockito.spy(translateCommand);
    translateCommandSpy.mode = "extract";
    translateCommandSpy.extractionType = "configs";
    translateCommandSpy.resourceFile = dirtyConfigsFolder.toString();

    Path frPropertiesPath = Paths.get("src/test/resources/strings_fr.properties");
    translateCommandSpy.translationFile = frPropertiesPath.toString();

    assertThrows(RuntimeException.class, translateCommandSpy::run);

    Mockito.verify(translateCommandSpy, Mockito.atLeast(1))
        .copyDirectoryContent(Mockito.any(), Mockito.any());
    Mockito.verify(translateCommandSpy, Mockito.atLeast(1))
        .deleteDirectoryRecursively(Mockito.any());
  }

  @Test
  public void testRunMerge() throws IOException {
    Path rawQuestionnairePath = Paths.get("src/test/resources/raw_questionnaire.json");
    Path tempRawQuestionnaire = Files.createTempFile("temp_raw_questionnaire", ".json");
    Files.copy(rawQuestionnairePath, tempRawQuestionnaire, StandardCopyOption.REPLACE_EXISTING);

    Path expectedMergedQuestionnairePath =
        Paths.get("src/test/resources/merged_questionnaire.json");

    Path frPropertiesPath = Paths.get("src/test/resources/strings_fr.properties");

    translateCommand.mode = "merge";
    translateCommand.resourceFile = tempRawQuestionnaire.toString();
    translateCommand.translationFile = frPropertiesPath.toString();
    translateCommand.locale = "fr";

    assertDoesNotThrow(() -> translateCommand.run());

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode processedRawQuestionnaire =
        objectMapper.readTree(
            Files.newBufferedReader(tempRawQuestionnaire, StandardCharsets.UTF_8));
    JsonNode mergedQuestionnaire =
        objectMapper.readTree(
            Files.newBufferedReader(expectedMergedQuestionnairePath, StandardCharsets.UTF_8));

    // Compare the contents of the two nodes
    assertEquals(mergedQuestionnaire, processedRawQuestionnaire, "File merged as expected");
    tempRawQuestionnaire.toFile().delete();
  }
}
