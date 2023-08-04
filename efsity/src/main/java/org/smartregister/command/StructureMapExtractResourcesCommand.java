/* (C)2023 */
package org.smartregister.command;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.StructureMap;
import org.smartregister.domain.FCTFile;
import org.smartregister.util.FCTStructureMapUtilities;
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

    FCTStructureMapUtilities structureMapUtilities = new FCTStructureMapUtilities();
    StructureMap structureMap = structureMapUtilities.getStructureMap(structureMapFilePath);

    IParser iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser();

    Bundle targetResource = new Bundle();

    Base baseElement =
        iParser.parseResource(QuestionnaireResponse.class, questionnaireResponse.getContent());

    structureMapUtilities.transform(
        structureMapUtilities.getSimpleWorkerContext(), baseElement, structureMap, targetResource);

    String outputFilePath =
        Files.isDirectory(Paths.get(output))
            ? output
                + File.separator
                + questionnaireResponse.getNameWithoutExtension()
                + "-extraction-bundle.json"
            : output;

    String outputResourcesAsString = iParser.encodeResourceToString(targetResource);
    FCTUtils.writeJsonFile(outputFilePath, outputResourcesAsString);

    FCTUtils.printInfo(String.format("Extracted to path\u001b[36m %s \u001b[0m", outputFilePath));
    FCTUtils.printCompletedInDuration(start);
  }
}
