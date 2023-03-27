/* (C)2023 */
package org.smartregister.command;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.hl7.fhir.r4.context.SimpleWorkerContext;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.StructureMap;
import org.hl7.fhir.r4.utils.StructureMapUtilities;
import org.hl7.fhir.utilities.npm.FilesystemPackageCacheManager;
import org.hl7.fhir.utilities.npm.ToolsVersion;
import org.smartregister.domain.FCTFile;
import org.smartregister.external.TransformSupportServices;
import org.smartregister.util.FCTUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "extract")
public class StructureMapExtractResourcesCommand implements Runnable {
  @CommandLine.Option(
      names = {"-qr", "--questionnaire-response"},
      description = "questionnaire response file path",
      required = true)
  private String qr;

  @CommandLine.Option(
      names = {"-sm", "--structure-map"},
      description = "structure map file path",
      required = true)
  private String sm;

  @CommandLine.Option(
      names = {"-o", "--output"},
      description = "output path, can be a file or a directory",
      defaultValue = ".")
  private String output;

  @Override
  public void run() {

    try {
      extractResource(qr, sm);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void extractResource(String qrFilePath, String structureMapFilePath) throws IOException {

    long start = System.currentTimeMillis();

    FCTUtils.printInfo(String.format("Questionnaire Response file path \u001b[35m%s\u001b[0m", qr));
    FCTUtils.printInfo(String.format("Structure Map file path \u001b[35m%s\u001b[0m", sm));

    FCTFile questionnaireResponse = FCTUtils.readFile(qrFilePath);
    FCTFile structureMapFile = FCTUtils.readFile(structureMapFilePath);

    FilesystemPackageCacheManager pcm =
        new FilesystemPackageCacheManager(true, ToolsVersion.TOOLS_VERSION);

    // Package name manually checked from
    // https://simplifier.net/packages?Text=hl7.fhir.core&fhirVersion=All+FHIR+Versions
    SimpleWorkerContext contextR4 =
        SimpleWorkerContext.fromPackage(
            pcm.loadPackage(
                FCTUtils.Constants.HL7_FHIR_PACKAGE, FCTUtils.Constants.HL7_FHIR_PACKAGE_VERSION));
    contextR4.setExpansionProfile(new Parameters());
    contextR4.setCanRunWithoutTerminology(true);

    TransformSupportServices transformSupportServices = new TransformSupportServices(contextR4);

    StructureMapUtilities structureMapUtilities =
        new StructureMapUtilities(contextR4, transformSupportServices);

    StructureMap sMap =
        structureMapUtilities.parse(
            structureMapFile.getContent(),
            FCTUtils.getStructureMapName(structureMapFile.getFirstLine()));

    IParser iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser();
    // Print SM?
    // String structureMapString = iParser.encodeResourceToString(sMap);
    // System.out.println(structureMapString);

    Bundle targetResource = new Bundle();

    Base baseElement =
        iParser.parseResource(QuestionnaireResponse.class, questionnaireResponse.getContent());

    structureMapUtilities.transform(contextR4, baseElement, sMap, targetResource);

    String outputFilePath =
        Files.isDirectory(Paths.get(output))
            ? output
                + File.separator
                + questionnaireResponse
                    .getName()
                    .substring(0, questionnaireResponse.getName().lastIndexOf('.'))
                + "-extraction-bundle.json"
            : output;

    String outputResources = iParser.encodeResourceToString(targetResource);
    FCTUtils.writeJsonFile(outputFilePath, outputResources);

    FCTUtils.printInfo(String.format("Extracted to path\u001b[36m %s \u001b[0m", outputFilePath));
    FCTUtils.printCompletedInDuration(start);
  }
}
