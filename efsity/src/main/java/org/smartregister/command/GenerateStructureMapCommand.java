package org.smartregister.command;

import java.io.IOException;
import org.smartregister.structuremaptool.GenerateStructureMapServiceKt;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "generateStructureMap")
public class GenerateStructureMapCommand implements Runnable {
  @CommandLine.Option(
      names = {"-q", "--questionnaire"},
      description = "Questionnaire",
      required = true)
  private String questionnairePath;

  @CommandLine.Option(
      names = {"-c", "--configPath"},
      description = "StructureMap generation configuration in an excel sheet",
      required = true)
  private String configPath;

  @CommandLine.Option(
      names = {"-qr", "--questionnaireResponsePath"},
      description = "Questionnaire response",
      required = true)
  private String questionnaireResponsePath;

  @Override
  public void run() {
    if (configPath != null) {
      try {
        generateStructureMap(configPath, questionnairePath, questionnaireResponsePath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void generateStructureMap(
      String configPath, String questionnairePath, String questionnaireResponsePath)
      throws IOException {
    long start = System.currentTimeMillis();
    FctUtils.printInfo("Starting StructureMap generation");
    GenerateStructureMapServiceKt.main(configPath, questionnairePath, questionnaireResponsePath);

    FctUtils.printCompletedInDuration(start);
  }
}
