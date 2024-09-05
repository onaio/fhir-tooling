package org.smartregister.command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

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
}
