package org.smartregister.command;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ValidateStructureMapCommandTest {

  private ValidateStructureMapCommand validateStructureMapCommand;
  private File tempFile;
  private Path tempDirectory;
  private Path generatedResourcesPath;

  @BeforeEach
  public void setUp() throws IOException {
    validateStructureMapCommand = new ValidateStructureMapCommand();
    tempDirectory = Files.createTempDirectory("test");
    tempFile = Files.createFile(tempDirectory.resolve("composition.json")).toFile();

    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write(
          "{\"questionnaire1\": \"structureMap1\", \"questionnaire2\": \"structureMap2\"}");
    }

    generatedResourcesPath = tempDirectory.resolve("generatedResources");
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
  void testGetResourceFiles() throws IOException {
    // Create a valid JSON file
    File jsonFile = new File(tempDirectory.toFile(), "validQuestionnaire.json");
    try (FileWriter writer = new FileWriter(jsonFile)) {
      writer.write("{\"resourceType\": \"Questionnaire\"}");
    }

    ArrayList<String> filesArray =
        ValidateStructureMapCommand.getResourceFiles(tempDirectory.toString());
    assertTrue(filesArray.contains(jsonFile.getAbsolutePath()));

    // Test with an invalid file
    File nonJsonFile = new File(tempDirectory.toFile(), "invalidFile.txt");
    try (FileWriter writer = new FileWriter(nonJsonFile)) {
      writer.write("Not a JSON file");
    }

    filesArray = ValidateStructureMapCommand.getResourceFiles(tempDirectory.toString());
    assertFalse(filesArray.contains(nonJsonFile.getAbsolutePath()));
  }

  @Test
  void testAddFhirResource() throws IOException {
    File jsonFile = new File(tempDirectory.toFile(), "questionnaire.json");
    try (FileWriter writer = new FileWriter(jsonFile)) {
      writer.write("{\"resourceType\": \"Questionnaire\"}");
    }

    ArrayList<String> filesArray = new ArrayList<>();
    ValidateStructureMapCommand.addFhirResource(jsonFile.getAbsolutePath(), filesArray);

    assertTrue(filesArray.contains(jsonFile.getAbsolutePath()));

    // Test with invalid JSON
    File invalidJsonFile = new File(tempDirectory.toFile(), "invalid.json");
    try (FileWriter writer = new FileWriter(invalidJsonFile)) {
      writer.write("{\"resourceType\": \"Other\"}");
    }

    ValidateStructureMapCommand.addFhirResource(invalidJsonFile.getAbsolutePath(), filesArray);
    assertFalse(filesArray.contains(invalidJsonFile.getAbsolutePath()));
  }
}
