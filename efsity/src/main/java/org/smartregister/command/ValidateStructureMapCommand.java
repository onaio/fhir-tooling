package org.smartregister.command;

import java.io.IOException;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "validateStructureMap")
public class ValidateStructureMapCommand implements Runnable {

  @CommandLine.Option(
      names = {"-i", "--input"},
      description = "path of the project folder",
      required = true)
  private String inputPath;

  @CommandLine.Option(
      names = {"-c", "--composition"},
      description = "path of the composition configuration file",
      required = true)
  String compositionFilePath;

  @Override
  public void run() {
    if (inputPath != null && compositionFilePath != null) {
      try {
        validateStructureMap(inputPath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void validateStructureMap(String inputFilePath) throws IOException {
    long start = System.currentTimeMillis();

    FctUtils.printInfo("Starting structureMap validation");
    FctUtils.printInfo(String.format("Input file path \u001b[35m%s\u001b[0m", inputFilePath));

    // Add functionality here

    FctUtils.printCompletedInDuration(start);
  }
}
