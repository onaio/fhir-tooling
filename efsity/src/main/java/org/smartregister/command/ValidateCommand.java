/* (C)2023 */
package org.smartregister.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;
import org.smartregister.processor.FCTValidationProcessor;
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

  @CommandLine.Option(
      names = {"-sm", "--structure-maps"},
      description =
          "(Optional) directory path to the location of structure map .txt or .map files. Must be a directory, default is current directory. Must be used with the -q flag",
      required = false)
  private String structureMapsFolderPath;

  @CommandLine.Option(
      names = {"-q", "--questionnaires"},
      description =
          "(Optional) directory path to the location of questionnaires .json files. Must be a directory. Must be used with the -sm flag",
      required = false)
  private String questionnairesFolderPath;

  @Override
  public void run() {

    if (!Files.isDirectory(Paths.get(inputFolder))) {
      throw new RuntimeException("-i, --input configs input path needs to be a directory");
    }

    if (structureMapsFolderPath != null && questionnairesFolderPath == null) {

      throw new RuntimeException(
          "You have supplied a -sm, --structure-maps flag without a corresponding -q, --questionnaires flag");

    } else if (structureMapsFolderPath == null && questionnairesFolderPath != null) {
      throw new RuntimeException(
          "You have supplied a -q, --questionnaires flag without a corresponding -sm, --structure-maps flag");
    }

    if (structureMapsFolderPath != null) {

      if (!Files.isDirectory(Paths.get(structureMapsFolderPath))) {
        throw new RuntimeException("-sm, --structure-maps path needs to be a directory");
      }

      if (!Files.isDirectory(Paths.get(questionnairesFolderPath))) {
        throw new RuntimeException("-q, --questionnaires path needs to be a directory");
      }
    }

    try {

      FCTValidationProcessor FCTValidationProcessor = new FCTValidationProcessor();
      FCTValidationProcessor.process(
          compositionFilePath, structureMapsFolderPath, questionnairesFolderPath, inputFolder);

    } catch (IOException e) {
      logger.severe(e.getMessage());
    }
  }
}
