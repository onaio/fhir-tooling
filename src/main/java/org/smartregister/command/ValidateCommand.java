/* (C)2023 */
package org.smartregister.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;
import org.smartregister.util.FCTValidationEngine;
import picocli.CommandLine;

@CommandLine.Command(name = "validate")
public class ValidateCommand implements Runnable {
  private static final Logger logger = Logger.getLogger(ValidateCommand.class.getCanonicalName());

  @CommandLine.Option(
      names = {"-i", "--input"},
      description = "directory path of the project",
      required = true)
  private String inputFolder;

  @CommandLine.Option(
      names = {"-c", "--composition"},
      description = "path of the composition configuration file",
      required = true)
  private String compositionFilePath;

  @Override
  public void run() {

    if (!Files.isDirectory(Paths.get(inputFolder))) {
      throw new RuntimeException("path needs to be a directory");
    }

    try {

      FCTValidationEngine FCTValidationEngine = new FCTValidationEngine();
      FCTValidationEngine.process(compositionFilePath, inputFolder);

    } catch (IOException e) {
      logger.severe(e.getMessage());
    }
  }
}
