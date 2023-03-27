/* (C)2023 */
package org.smartregister;

import org.smartregister.command.ConvertCommand;
import org.smartregister.command.StructureMapExtractResourcesCommand;
import org.smartregister.command.ValidateCommand;
import org.smartregister.util.FCTUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "fct",
    description = "FHIRCore tooling to make content authoring easier.",
    version = "1.0.0",
    mixinStandardHelpOptions = true,
    subcommands = {
      ConvertCommand.class,
      StructureMapExtractResourcesCommand.class,
      ValidateCommand.class
    })
public class Main implements Runnable {
  public static final String VERSION = "1.0.0";

  @CommandLine.Option(
      names = {"-v"},
      description = "version")
  private boolean getVersion;

  public static void main(String[] args) {
    FCTUtils.printToConsole(
        String.format("Running\u001b[36m EFSITY Version %s \u001b[0m", VERSION));
    FCTUtils.printNewLine();

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
