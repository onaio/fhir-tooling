/* (C)2023 */
package org.smartregister;

import org.smartregister.command.CarePlanGeneratorCommand;
import org.smartregister.command.ConvertCommand;
import org.smartregister.command.StructureMapExtractResourcesCommand;
import org.smartregister.command.ValidateCommand;
import org.smartregister.command.ValidateFhirResourcesCommand;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "fct",
    description = "FHIRCore tooling to make content authoring easier.",
    version = "2.3.1",
    mixinStandardHelpOptions = true,
    subcommands = {
      ConvertCommand.class,
      StructureMapExtractResourcesCommand.class,
      ValidateCommand.class,
      CarePlanGeneratorCommand.class,
      ValidateFhirResourcesCommand.class
    })
public class Main implements Runnable {
  public static final String VERSION = "2.3.1";

  @CommandLine.Option(
      names = {"-v"},
      description = "version")
  private boolean getVersion;

  public static void main(String[] args) {
    FctUtils.printToConsole(
        String.format("Running\u001b[36m EFSITY Version %s \u001b[0m", VERSION));
    FctUtils.printNewLine();

    int exitCode = new CommandLine(new Main()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public void run() {
    if (getVersion) {
      System.out.println(VERSION);
    }
  }
}
