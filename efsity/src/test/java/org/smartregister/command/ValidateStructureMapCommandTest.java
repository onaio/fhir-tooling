package org.smartregister.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ValidateStructureMapCommandTest {

  private ValidateStructureMapCommand validateStructureMapCommand;
  private File tempFile;

  @BeforeEach
  public void setUp() throws IOException {
    validateStructureMapCommand = new ValidateStructureMapCommand();
    tempFile = File.createTempFile("composition", ".json");
    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write(
          "{\"questionnaire1\": \"structureMap1\", \"questionnaire2\": \"structureMap2\"}");
    }
  }

  @AfterEach
  public void tearDown() {
    if (tempFile.exists()) {
      tempFile.delete();
    }
  }

  @Test
  public void testParseCompositionFile() throws IOException {
    Map<String, String> result =
        validateStructureMapCommand.parseCompositionFile(tempFile.getAbsolutePath());

    assertEquals(2, result.size());
    assertEquals("structureMap1", result.get("questionnaire1"));
    assertEquals("structureMap2", result.get("questionnaire2"));
  }

  @Test
  public void testValidateStructureMap() throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    File validStructureMapFile = new File(tempDir, "structureMap1.map");
    validStructureMapFile.createNewFile();

    validateStructureMapCommand.inputPath = tempDir.getAbsolutePath();
    validateStructureMapCommand.compositionFilePath = tempFile.getAbsolutePath();

    validateStructureMapCommand.validateStructureMap(tempDir.getAbsolutePath());

    assertTrue(validStructureMapFile.exists());

    validStructureMapFile.delete();
  }
}
