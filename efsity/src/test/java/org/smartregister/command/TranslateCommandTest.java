package org.smartregister.command;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
}
