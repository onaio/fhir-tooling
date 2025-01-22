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
  private TranslateCommand translateCommand;
  // Create temp application directory and add fhir_content and questionnaire dirs
  private Path tempAppFolderPath;
  private Path fhirContentFolderPath;
  private Path rawQuestionnairePath;
  private Path tempRawQuestionnaire;
  private Path tempAppConfigsFolder;

  @BeforeEach
  public void setUp() throws IOException {

    // Create temp application directory and add configs, fhir_content, questionnaire dirs and
    // content
    tempAppFolderPath = createTempDirectory("tempApp");
    tempAppConfigsFolder = tempAppFolderPath.resolve("configs");
    Files.createDirectory(tempAppConfigsFolder);
    FctUtils.copyDirectoryContent(
        Paths.get("src/test/resources/clean_configs_folder"), tempAppConfigsFolder);
    fhirContentFolderPath = createSubDirectory(tempAppFolderPath, "fhir_content");
    Path questionnairesFolderPath = createSubDirectory(fhirContentFolderPath, "questionnaires");
    rawQuestionnairePath = Paths.get("src/test/resources/raw_questionnaire.json");
    tempRawQuestionnaire = questionnairesFolderPath.resolve("temp_raw_questionnaire.json");
    Files.createFile(tempRawQuestionnaire);
    Files.copy(rawQuestionnairePath, tempRawQuestionnaire, StandardCopyOption.REPLACE_EXISTING);

    translateCommand = new TranslateCommand();
  }

  @AfterEach
  public void tearDown() throws IOException {
    deleteDirectory(tempAppConfigsFolder);
    deleteDirectory(tempAppFolderPath);
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
    Path tempRawQuestionnaire =
        createTempFileWithContents("temp_raw_questionnaire", ".json", rawQuestionnairePath);
    Path tempDefaultPropertiesPath = createTempFile("temp_strings_default", ".properties");

    translateCommand.mode = "extract";
    translateCommand.resourceFile = tempRawQuestionnaire.toString();
    translateCommand.translationFile = tempDefaultPropertiesPath.toString();
    translateCommand.extractionType = "fhirContent";

    assertDoesNotThrow(() -> translateCommand.run());

    compareProperties(
        "src/test/resources/strings_default.properties", tempDefaultPropertiesPath.toString());

    deleteFile(tempRawQuestionnaire);
    deleteFile(tempDefaultPropertiesPath);
  }

  // this tests a case of type fct translate -m extract -rf
  // ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content
  @Test
  public void testRunExtractFhirContent_Given_FhirContentFolderPath() throws IOException {

    Path tempTranslationsPath = fhirContentFolderPath.resolve("translation");
    Path tempDefaultPropertiesPath = tempTranslationsPath.resolve("strings_default.properties");

    translateCommand.mode = "extract";
    translateCommand.resourceFile = fhirContentFolderPath.toString();

    assertDoesNotThrow(() -> translateCommand.run());

    compareProperties(
        "src/test/resources/strings_default.properties", tempDefaultPropertiesPath.toString());

    deleteFile(tempRawQuestionnaire);
    deleteFile(tempDefaultPropertiesPath);
  }

  // this tests a case of type fct translate -m extract -rf
  // ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content -et fhirContent
  @Test
  public void testRunExtractFhirContent_Given_FhirContentFolderNameWithExtractionType()
      throws IOException {

    Path translationsPath = fhirContentFolderPath.resolve("translation");
    Path tempDefaultPropertiesPath = translationsPath.resolve("strings_default.properties");

    translateCommand.mode = "extract";
    translateCommand.resourceFile = fhirContentFolderPath.toString();
    translateCommand.extractionType = "fhirContent";

    assertDoesNotThrow(() -> translateCommand.run());

    compareProperties(
        "src/test/resources/strings_default.properties", tempDefaultPropertiesPath.toString());

    deleteFile(tempRawQuestionnaire);
    deleteFile(tempDefaultPropertiesPath);
  }

  // this tests a case of type fct translate -m extract -rf
  // ~/Workspace/fhir-resources/<project>/<environment>/<app>/configs
  @Test
  public void testRunExtractConfig_Given_ConfigsFolder() throws IOException {

    TranslateCommand translateCommandSpy = Mockito.spy(translateCommand);
    translateCommandSpy.mode = "extract";
    translateCommandSpy.resourceFile = tempAppConfigsFolder.toString();

    Path propertiesPathOriginal =
        Paths.get("src/test/resources/clean_configs_folder/strings_configs.properties");
    Path propertiesPathTest = createTempFile("strings_default", "properties");
    translateCommandSpy.translationFile = propertiesPathTest.toString();
    translateCommandSpy.run();
    compareProperties(propertiesPathOriginal.toString(), propertiesPathOriginal.toString());

    Mockito.verify(translateCommandSpy, Mockito.atLeast(2))
        .copyDirectoryContent(Mockito.any(), Mockito.any());
    Mockito.verify(translateCommandSpy, Mockito.atLeast(1))
        .deleteDirectoryRecursively(Mockito.any());
  }

  // this tests a case of type fct translate -m extract -rf
  // ~/Workspace/fhir-resources/<project>/<environment>/<app>/configs -et configs
  @Test
  public void testRunExtractConfig_Given_ConfigsFolderPath_FileExtractionType() throws IOException {

    TranslateCommand translateCommandSpy = Mockito.spy(translateCommand);
    translateCommandSpy.mode = "extract";
    translateCommandSpy.extractionType = "configs";
    translateCommandSpy.resourceFile = tempAppConfigsFolder.toString();

    Path frPropertiesPathOriginal = Paths.get("src/test/resources/strings_fr.properties");
    Path frPropertiesPathTest =
        tempAppConfigsFolder.resolve(frPropertiesPathOriginal.getFileName());
    Files.copy(frPropertiesPathOriginal, frPropertiesPathTest, StandardCopyOption.REPLACE_EXISTING);
    translateCommandSpy.translationFile = frPropertiesPathTest.toString();
    translateCommandSpy.run();
    Mockito.verify(translateCommandSpy, Mockito.atLeast(2))
        .copyDirectoryContent(Mockito.any(), Mockito.any());
    Mockito.verify(translateCommandSpy, Mockito.atLeast(1))
        .deleteDirectoryRecursively(Mockito.any());
  }

  @Test
  public void testRunExtractConfig_Given_DirtyConfigsFolderDeletesTempFileOnFailure()
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

  // this function tests a case of type fct translate -m extract -rf
  // ~/Workspace/fhir-resources/<project>/<environment>/<app>
  @Test
  public void testRunExtractAll_Given_ProjectPath() throws IOException {

    Path tempTranslationsPath = tempAppFolderPath.resolve("translation");
    Path tempDefaultPropertiesPath = tempTranslationsPath.resolve("strings_default.properties");

    translateCommand.mode = "extract";
    translateCommand.resourceFile = tempAppFolderPath.toString();

    assertDoesNotThrow(() -> translateCommand.run());

    compareProperties(
        "src/test/resources/strings_all.properties", tempDefaultPropertiesPath.toString());

    deleteFile(tempRawQuestionnaire);
    deleteFile(tempDefaultPropertiesPath);
  }

  // this function tests a case of type fct translate -m extract -rf
  // ~/Workspace/fhir-resources/<project>/<environment>/<app> -et all
  @Test
  public void testRunExtractAll_Given_ProjectPathAndExtractionType() throws IOException {

    Path tempTranslationsPath = tempAppFolderPath.resolve("translation");
    Path tempDefaultPropertiesPath = tempTranslationsPath.resolve("strings_default.properties");

    translateCommand.mode = "extract";
    translateCommand.resourceFile = tempAppFolderPath.toString();
    translateCommand.extractionType = "all";

    assertDoesNotThrow(() -> translateCommand.run());

    compareProperties(
        "src/test/resources/strings_all.properties", tempDefaultPropertiesPath.toString());

    deleteFile(tempRawQuestionnaire);
    deleteFile(tempDefaultPropertiesPath);
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

    compareJsonFiles(tempRawQuestionnaire, expectedMergedQuestionnairePath);
    deleteFile(tempRawQuestionnaire);
  }

  // Helper Methods
  private Path createTempDirectory(String name) throws IOException {
    return Files.createTempDirectory(name);
  }

  private Path createTempDirectoryWithContents(String tempDirName, String sourceDir)
      throws IOException {
    Path tempDir = createTempDirectory(tempDirName);
    FctUtils.copyDirectoryContent(Paths.get(sourceDir), tempDir);
    return tempDir;
  }

  private Path createSubDirectory(Path parent, String name) throws IOException {
    Path subDir = parent.resolve(name);
    Files.createDirectories(subDir);
    return subDir;
  }

  private Path createTempFile(String prefix, String suffix) throws IOException {
    return Files.createTempFile(prefix, suffix);
  }

  private Path createTempFileWithContents(String prefix, String suffix, Path sourceFile)
      throws IOException {
    Path tempFile = createTempFile(prefix, suffix);
    Files.copy(sourceFile, tempFile, StandardCopyOption.REPLACE_EXISTING);
    return tempFile;
  }

  private void deleteDirectory(Path dir) throws IOException {
    FctUtils.deleteDirectoryRecursively(dir);
  }

  private void deleteFile(Path file) {
    try {
      Files.deleteIfExists(file);
    } catch (IOException e) {
      System.err.println("Failed to delete file: " + file + " due to " + e.getMessage());
    }
  }

  private void compareProperties(String expectedPath, String actualPath) throws IOException {
    Properties expected = FctUtils.readPropertiesFile(expectedPath);
    Properties actual = FctUtils.readPropertiesFile(actualPath);
    assertEquals(expected, actual, "Properties files do not match.");
  }

  private void compareJsonFiles(Path actualPath, Path expectedPath) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualJson =
        objectMapper.readTree(Files.newBufferedReader(actualPath, StandardCharsets.UTF_8));
    JsonNode expectedJson =
        objectMapper.readTree(Files.newBufferedReader(expectedPath, StandardCharsets.UTF_8));
    assertEquals(expectedJson, actualJson, "JSON files do not match.");
  }
}
