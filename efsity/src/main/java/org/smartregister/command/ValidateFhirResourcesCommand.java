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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.jimblackler.jsonschemafriend.*;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.json.JSONObject;
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

  @CommandLine.Option(
      names = {"-s", "--schema"},
      description = "configs schema"
  )
  static String configSchema;

  @Override
  public void run() {
    if (input != null) {
      try {
        validateFhirResources(input);
      } catch (IOException | ValidationException | GenerationException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void validateFhirResources(String inputFilePath) throws IOException, ValidationException, GenerationException {

    long start = System.currentTimeMillis();

    FctUtils.printInfo("Starting FHIR resource validation");
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
    int failCheck = 0;

    if (!Files.isDirectory(Paths.get(inputFilePath))) {
      FctUtils.printInfo(String.format("\u001b[35m%s\u001b[0m", inputFilePath));
      inputFile = FctUtils.readFile(inputFilePath);
      if(isConfigFile(inputFile)){
        failCheck = validateConfig(inputFile);
      } else {
        IBaseResource resource = iParser.parseResource(inputFile.getContent());
        failCheck = validateResource(validator, resource);
      }

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
            if(isConfigFile(inputFile)){
              failCheck = ( validateConfig(inputFile) == -1) ? -1 : failCheck;
            } else {
              IBaseResource resource = iParser.parseResource(inputFile.getContent());
              failCheck = ( validateResource(validator, resource) == -1) ? -1 : failCheck;
            }
          } catch (DataFormatException | NoSuchMethodError e) {
            FctUtils.printError(e.toString());
          }
        }
      }
    }
    FctUtils.printCompletedInDuration(start);

    if (failCheck < 0){
      throw new RuntimeException("Found Invalid file(s)");
    }
  }

  private static int validateResource(FhirValidator validator, IBaseResource resource) {

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
      return -1;
    } else {
      FctUtils.printInfo("File is valid!");
      return 0;
    }
  }

  static boolean isConfigFile(FctFile inputFile){
    JSONObject resource = new JSONObject(inputFile.getContent());
    return resource.has("configType");
  }

  static int validateConfig(FctFile configFile) throws GenerationException, ValidationException {
    boolean valid = true;
    SchemaStore schemaStore = new SchemaStore();
    Schema schema = schemaStore.loadSchema(new File(String.valueOf(Paths.get(configSchema))));

    Validator validator = new Validator();
    try {
      validator.validateJson(schema, configFile.getContent());
      FctUtils.printToConsole("Config file is valid!");
    } catch (ValidationException e) {
      valid = false;
      FctUtils.printError(e.toString());
    }

    if (valid){
      return 0;
    } else {
      return -1;
    }
  }
}
