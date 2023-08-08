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
import org.smartregister.domain.FctFile;
import org.smartregister.external.FhirCarePlanGeneratorLite;
import org.smartregister.util.FctUtils;
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
      description =
          "(Optional) output path, can be a file or a directory, default is current directory",
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

    FctUtils.printInfo(
        String.format(
            "QuestionnaireResponse file path \u001b[35m%s\u001b[0m",
            questionnaireResponseFilePath));
    FctUtils.printInfo(
        String.format("PlanDefinition file path \u001b[35m%s\u001b[0m", planDefinitionFilePath));
    FctUtils.printInfo(String.format("Subject file path \u001b[35m%s\u001b[0m", subjectFilePath));
    FctUtils.printInfo(
        String.format(
            "Structure Maps folder path \u001b[35m%s\u001b[0m", this.structureMapFolderPath));

    FctFile questionnaireResponseFile = FctUtils.readFile(questionnaireResponseFilePath);
    FctFile planDefinitionFile = FctUtils.readFile(planDefinitionFilePath);
    FctFile subjectFile = FctUtils.readFile(subjectFilePath);

    FhirCarePlanGeneratorLite fhirCarePlanGeneratorLite =
        new FhirCarePlanGeneratorLite(structureMapFolderPath);

    PlanDefinition planDefinition =
        FctUtils.getFhirResource(PlanDefinition.class, planDefinitionFile.getContent());
    IBaseResource subject = iParser.parseResource(subjectFile.getContent());
    QuestionnaireResponse questionnaireResponse =
        FctUtils.getFhirResource(
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
    FctUtils.writeJsonFile(outputFilePath, outputResourcesAsString);

    FctUtils.printInfo(
        String.format(
            "Result Bundle generated and saved to path\u001b[36m %s \u001b[0m", outputFilePath));
    FctUtils.printCompletedInDuration(start);
  }

  public class Constants {
    public static final String CAREPLAN_BUNDLE_FILENAME_SUFFIX = "-careplan-bundle.json";
  }
}
