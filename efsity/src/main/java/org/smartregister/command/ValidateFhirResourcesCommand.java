/* (C)2023 */
package org.smartregister.command;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.smartregister.domain.FctFile;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "validateFhir")
public class ValidateFhirResourcesCommand implements Runnable {
  @CommandLine.Option(
      names = {"-i", "--input"},
      description = "input file path",
      required = true)
  private String input;

  @Override
  public void run() {
    if (input != null) {
      try {
        validateFhirResources(input);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      ;
    }
  }

  private void validateFhirResources(String inputFilePath) throws IOException {

    long start = System.currentTimeMillis();

    FctUtils.printInfo(String.format("Starting FHIR resource validation"));
    FctUtils.printInfo(String.format("Input file path \u001b[35m%s\u001b[0m", inputFilePath));

    FhirContext fhirContext = FhirContext.forR4();

    ValidationSupportChain validationSupportChain =
        new ValidationSupportChain(
            new DefaultProfileValidationSupport(fhirContext),
            new InMemoryTerminologyServerValidationSupport(fhirContext),
            new CommonCodeSystemsTerminologyService(fhirContext));

    FhirValidator validator = fhirContext.newValidator();
    FhirInstanceValidator instanceValidator = new FhirInstanceValidator(validationSupportChain);
    validator.registerValidatorModule(instanceValidator);

    IParser iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser();
    FctFile inputFile;

    if (!Files.isDirectory(Paths.get(inputFilePath))) {
      FctUtils.printInfo(String.format("\u001b[35m%s\u001b[0m", inputFilePath));
      inputFile = FctUtils.readFile(inputFilePath);
      IBaseResource resource = iParser.parseResource(inputFile.getContent());
      validateResource(validator, resource);

    } else {
      Map<String, Map<String, String>> folderTofilesIndexMap =
          FctUtils.indexConfigurationFiles(inputFilePath, "json");
      for (Map.Entry<String, Map<String, String>> entry : folderTofilesIndexMap.entrySet()) {
        Map<String, String> fileIndexMap = folderTofilesIndexMap.get(entry.getKey());

        for (Map.Entry<String,String> nestedEntry : fileIndexMap.entrySet()) {
          if (nestedEntry.getKey().startsWith(".")) continue;
          FctUtils.printInfo(String.format("\u001b[35m%s\u001b[0m", nestedEntry.getValue()));
          inputFile = FctUtils.readFile(nestedEntry.getValue());

          try {
            IBaseResource resource = iParser.parseResource(inputFile.getContent());
            validateResource(validator, resource);
          } catch (DataFormatException e) {
            FctUtils.printError(e.toString());
          } catch (NoSuchMethodError e) {
            FctUtils.printError(e.toString());
          }
        }
      }
    }

    FctUtils.printCompletedInDuration(start);
  }

  private void validateResource(FhirValidator validator, IBaseResource resource) {

    ValidationResult result = validator.validateWithResult(resource);

    if (!result.isSuccessful()) {
      for (SingleValidationMessage next : result.getMessages()) {
        if (next.getSeverity() == ResultSeverityEnum.ERROR) {
          FctUtils.printError(
              String.format(
                  " \u001b[36m%s\u001b[0m - %s", next.getLocationString(), next.getMessage()));
        } else if (next.getSeverity() == ResultSeverityEnum.INFORMATION) {
          FctUtils.printInfo(
              String.format(
                  " \u001b[36m%s\u001b[0m - %s", next.getLocationString(), next.getMessage()));
        } else if (next.getSeverity() == ResultSeverityEnum.WARNING) {
          FctUtils.printWarning(
              String.format(
                  " \u001b[36m%s\u001b[0m - %s", next.getLocationString(), next.getMessage()));
        }
      }
    } else {
      FctUtils.printInfo(String.format("File is valid!"));
    }
  }
}
