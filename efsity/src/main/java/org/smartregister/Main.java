/* (C)2023 */
package org.smartregister;

import org.smartregister.command.CarePlanGeneratorCommand;
import org.smartregister.command.ConvertCommand;
import org.smartregister.command.StructureMapExtractResourcesCommand;
import org.smartregister.command.ValidateCommand;
import org.smartregister.command.ValidateFhirResourcesCommand;
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
      ValidateFhirResourcesCommand.class
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
