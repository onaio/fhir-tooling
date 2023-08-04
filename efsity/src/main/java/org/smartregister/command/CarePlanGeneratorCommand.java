/* (C)2023 */
package org.smartregister.command;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Resource;
import org.smartregister.domain.FCTFile;
import org.smartregister.external.FHIRCarePlanGeneratorLite;
import org.smartregister.util.FCTUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "careplan")
public class CarePlanGeneratorCommand implements Runnable {
  @CommandLine.Option(
      names = {"-qr", "--questionnaire-response"},
      description = "questionnaire response file path",
      required = true)
  private String questionnaireResponseFilePath;

  @CommandLine.Option(
      names = {"-s", "--subject"},
      description = "subject resource",
      required = true)
  private String subjectFilePath;

  @CommandLine.Option(
      names = {"-pd", "--plan-definition"},
      description = "plan definition resource",
      required = true)
  private String planDefinitionFilePath;

  @CommandLine.Option(
      names = {"-sm", "--structure-map"},
      description = "structure map folder path",
      required = true)
  private String structureMapFolderPath;

  @CommandLine.Option(
      names = {"-o", "--output"},
      description = "output path, can be a file or a directory",
      defaultValue = ".")
  private String outputFilePath;

  @Override
  public void run() {

    if (StringUtils.isNotBlank(structureMapFolderPath)
        && !Files.isDirectory(Paths.get(structureMapFolderPath))) {
      throw new RuntimeException(
          String.format(
              "Structure map folder path must be a directory : %s", structureMapFolderPath));
    }

    try {
      generateCarePlanAndTasks();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void generateCarePlanAndTasks() throws IOException {

    long start = System.currentTimeMillis();
    IParser iParser = FhirContext.forR4().newJsonParser();

    FCTUtils.printInfo(
        String.format(
            "QuestionnaireResponse file path \u001b[35m%s\u001b[0m",
            questionnaireResponseFilePath));
    FCTUtils.printInfo(
        String.format("PlanDefinition file path \u001b[35m%s\u001b[0m", planDefinitionFilePath));
    FCTUtils.printInfo(String.format("Subject file path \u001b[35m%s\u001b[0m", subjectFilePath));
    FCTUtils.printInfo(
        String.format(
            "Structure Maps folder path \u001b[35m%s\u001b[0m", this.structureMapFolderPath));

    FCTFile questionnaireResponseFile = FCTUtils.readFile(questionnaireResponseFilePath);
    FCTFile planDefinitionFile = FCTUtils.readFile(planDefinitionFilePath);
    FCTFile subjectFile = FCTUtils.readFile(subjectFilePath);

    FHIRCarePlanGeneratorLite fhirCarePlanGeneratorLite =
        new FHIRCarePlanGeneratorLite(structureMapFolderPath);

    PlanDefinition planDefinition =
        FCTUtils.getFhirResource(PlanDefinition.class, planDefinitionFile.getContent());
    IBaseResource subject = iParser.parseResource(subjectFile.getContent());
    QuestionnaireResponse questionnaireResponse =
        FCTUtils.getFhirResource(
            QuestionnaireResponse.class, questionnaireResponseFile.getContent());

    Bundle questionnaireResponseDataBundle = new Bundle();
    Bundle responseResourceBundle = new Bundle();

    questionnaireResponseDataBundle.addEntry(
        new Bundle.BundleEntryComponent().setResource(questionnaireResponse));

    CarePlan carePlan =
        fhirCarePlanGeneratorLite.generateOrUpdateCarePlan(
            planDefinition, (Resource) subject, questionnaireResponseDataBundle);

    responseResourceBundle.addEntry(new Bundle.BundleEntryComponent().setResource(carePlan));

    for (Resource resource : carePlan.getContained()) {
      responseResourceBundle.addEntry(new Bundle.BundleEntryComponent().setResource(resource));
    }

    carePlan.getContained().clear();

    String outputFilePath =
        Files.isDirectory(Paths.get(this.outputFilePath))
            ? this.outputFilePath
                + File.separator
                + subjectFile.getNameWithoutExtension()
                + Constants.CAREPLAN_BUNDLE_FILENAME_SUFFIX
            : this.outputFilePath;

    String outputResourcesAsString = iParser.encodeResourceToString(responseResourceBundle);
    FCTUtils.writeJsonFile(outputFilePath, outputResourcesAsString);

    FCTUtils.printInfo(
        String.format(
            "Careplan and Tasks generated to path\u001b[36m %s \u001b[0m", outputFilePath));
    FCTUtils.printCompletedInDuration(start);
  }

  public class Constants {
    public static final String CAREPLAN_BUNDLE_FILENAME_SUFFIX = "-careplan-bundle.json";
  }
}
