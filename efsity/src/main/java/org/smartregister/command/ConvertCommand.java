/* (C)2023 */
package org.smartregister.command;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.smartregister.domain.FctFile;
import org.smartregister.external.CqlToLibraryConvertServices;
import org.smartregister.util.FctStructureMapUtilities;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "convert")
public class ConvertCommand implements Runnable {
  @CommandLine.Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "display a help message")
  private boolean helpRequested = false;

  @CommandLine.Option(
      names = {"-i", "--input"},
      description = "input file path e.g. structure_map.txt or .cql",
      required = true)
  private String input;

  @CommandLine.Option(
      names = {"-t", "--type"},
      description = "type of conversion sm | cql",
      required = true)
  private String conversionType;

  @CommandLine.Option(
      names = {"-o", "--output"},
      description =
          "(Optional) output path, can be file or directory, default is current directory",
      defaultValue = ".")
  private String output;

  @Override
  public void run() { // StructureMapToJson
    if (input != null) {
      try {

        convertToJson(input, output, conversionType);

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void convertToJson(String inputFilePath, String outputFilePath, String conversionType)
      throws IOException {

    long start = System.currentTimeMillis();

    String entities =
        Constants.SM.equals(conversionType)
            ? "structure.map to structure.json"
            : "cql.cql to cql.fhir.json";

    String outputFileSuffix = Constants.SM.equals(conversionType) ? ".json" : ".fhir.json";

    FctUtils.printInfo(String.format("Starting %s conversion...", entities));
    FctUtils.printInfo(String.format("Input file \u001b[35m%s\u001b[0m", inputFilePath));

    IParser iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser();
    FctFile inputFile = FctUtils.readFile(inputFilePath);
    IBaseResource resource;

    if (Constants.SM.equals(conversionType)) { // Structure Map

      resource = convertStructureMapToJson(inputFile);

    } else if (Constants.CQL.equals(conversionType)) {

      resource = convertDotCqlToJsonLibrary(inputFile);

    } else {

      throw new RuntimeException(
          String.format("Conversion type \u001b[31m%s\u001b[0m not supported", conversionType));
    }

    String structureMapJsonString = iParser.encodeResourceToString(resource);

    String outputPath =
        Files.isDirectory(Paths.get(outputFilePath))
            ? outputFilePath + File.separator + inputFile.getName() + outputFileSuffix
            : outputFilePath;

    FctUtils.writeJsonFile(outputPath, structureMapJsonString);

    FctUtils.printInfo(String.format("Output file\u001b[36m %s \u001b[0m", outputPath));
    FctUtils.printCompletedInDuration(start);
  }

  private IBaseResource convertDotCqlToJsonLibrary(FctFile inputFile) {
    CqlToLibraryConvertServices services = new CqlToLibraryConvertServices();
    return services.compileAndBuildCqlLibrary(inputFile);
  }

  public IBaseResource convertStructureMapToJson(FctFile inputFile) throws IOException {

    FctStructureMapUtilities structureMapUtilities = new FctStructureMapUtilities();
    return structureMapUtilities.parse(
        inputFile.getContent(), FctUtils.getStructureMapName(inputFile.getFirstLine()));
  }

  public class Constants {
    public static final String SM = "sm"; // Structure Map
    public static final String CQL = "cql";
  }
}
