/* (C)2023 */
package org.smartregister;

import org.smartregister.command.*;
import org.smartregister.fhircore_tooling.BuildConfig;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "fct",
    description = "FHIRCore tooling to make content authoring easier.",
    version = BuildConfig.RELEASE_VERSION,
    mixinStandardHelpOptions = true,
    subcommands = {
      ConvertCommand.class,
      StructureMapExtractResourcesCommand.class,
      ValidateCommand.class,
      CarePlanGeneratorCommand.class,
      ValidateFhirResourcesCommand.class,
      TranslateCommand.class,
      QuestionnaireResponseGeneratorCommand.class,
      ValidateFileStructureCommand.class
    })
public class Main implements Runnable {
  public static void main(String[] args) {

    int exitCode = new CommandLine(new Main()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public void run() {
    FctUtils.printToConsole(
            String.format("Running\u001b[36m EFSITY Version %s \u001b[0m", BuildConfig.RELEASE_VERSION));
  }
}
