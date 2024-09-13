package org.smartregister.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.jimblackler.jsonschemafriend.GenerationException;
import net.jimblackler.jsonschemafriend.ValidationException;
import org.smartregister.domain.FctFile;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "validateStructureMap")
public class ValidateStructureMapCommand implements Runnable {
  static JsonParser jsonParser = new JsonParser();

  @CommandLine.Option(
      names = {"-i", "--input"},
      description = "path of the project folder",
      required = true)
  String inputPath;

  @CommandLine.Option(
      names = {"-v", "--validate"},
      description = "validate the fhir resources ",
      defaultValue = "false")
  boolean validate;

  @CommandLine.Option(
      names = {"-sm", "--structure-map"},
      description = "structure map file path",
      required = false)
  String structureMapFilePath;

  @Override
  public void run() {
    if (inputPath != null) {
      try {
        validateStructureMap(inputPath, validate, structureMapFilePath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  void validateStructureMap(String inputFilePath, boolean validate, String structureMapFilePath)
      throws IOException {
    long start = System.currentTimeMillis();

    FctUtils.printInfo("Starting structureMap validation");
    FctUtils.printInfo(String.format("Input file path \u001b[35m%s\u001b[0m", inputFilePath));

    ArrayList<String> questionnaires = getResourceFiles(inputFilePath);
    boolean allResourcesValid = true;

    for (String questionnairePath : questionnaires) {
      FctUtils.printInfo("Processing: " + questionnairePath);

      // Validate Questionnaire
      if (validate) {
        try {
          ValidateFhirResourcesCommand.validateFhirResources(questionnairePath);
        } catch (ValidationException | GenerationException | IOException e) {
          throw new RuntimeException(e);
        }
      }

      // Define the directory name for generated resources
      String directoryName = "generatedResources";
      Path generatedResourcesPath = Paths.get(directoryName);
      if (!Files.exists(generatedResourcesPath)) {
        Files.createDirectory(generatedResourcesPath);
        FctUtils.printInfo(
            "Created directory: " + generatedResourcesPath.toAbsolutePath().toString());
      }

      // Generate QuestionnaireResponse
      QuestionnaireResponseGeneratorCommand.generateResponse(
          questionnairePath,
          "populate",
          generatedResourcesPath.toString(),
          "",
          "http://localhost:8080/fhir",
          "",
          "",
          "");

      // Extract Resources using the StructureMap and the generated QuestionnaireResponse
      String generatedQuestionnaireResponsePath =
          generatedResourcesPath.toString()
              + File.separator
              + Paths.get(questionnairePath)
                  .getFileName()
                  .toString()
                  .replace(".json", "-response.json");

      // Create the subdirectory called extractedResources
      Path extractedResourcesPath = generatedResourcesPath.resolve("extractedResources");
      if (!Files.exists(extractedResourcesPath)) {
        Files.createDirectory(extractedResourcesPath);
        FctUtils.printInfo(
            "Created extracted resources folder: "
                + extractedResourcesPath.toAbsolutePath().toString());
      }

      StructureMapExtractResourcesCommand.extractResource(
          generatedQuestionnaireResponsePath,
          structureMapFilePath,
          extractedResourcesPath.toString());

      // Read the Bundle file using FctUtils.readFile and extract resources
      try (DirectoryStream<Path> extractedFiles =
          Files.newDirectoryStream(extractedResourcesPath, "*.json")) {
        for (Path bundleFile : extractedFiles) {
          FctUtils.printInfo("Reading Bundle file: " + bundleFile.toString());

          // Read the bundle file
          FctFile bundleContent = FctUtils.readFile(bundleFile.toString());

          JsonObject bundleJson =
              JsonParser.parseString(bundleContent.getContent()).getAsJsonObject();

          // Check if it's a Bundle
          if (bundleJson.has("resourceType")
              && "Bundle".equals(bundleJson.get("resourceType").getAsString())) {
            JsonArray entries = bundleJson.getAsJsonArray("entry");
            FctUtils.printInfo("Processing " + entries.size() + " entries from the Bundle.");

            // Process each entry in the Bundle
            for (int i = 0; i < entries.size(); i++) {
              JsonObject entry = entries.get(i).getAsJsonObject();
              JsonObject resource = entry.getAsJsonObject("resource");

              if (resource != null) {
                String resourceType = resource.get("resourceType").getAsString();

                // Create a unique file name for each resource
                String resourceFileName = resourceType + ".json";
                Path resourceFilePath =
                    Paths.get(extractedResourcesPath.toString(), resourceFileName);

                // Write the resource to a file using FctUtils.writeJsonFile
                FctUtils.writeJsonFile(resourceFilePath.toString(), resource.toString());

                // Validate the extracted resource
                try {
                  ValidateFhirResourcesCommand.validateFhirResources(resourceFilePath.toString());
                } catch (ValidationException | GenerationException | IOException e) {
                  FctUtils.printError(
                      "Validation failed for resource: " + resourceFilePath.toString());
                  allResourcesValid = false;
                }
              } else {
                FctUtils.printWarning("No resource found in entry " + i);
              }
            }
          } else {
            FctUtils.printError("File is not a Bundle: " + bundleContent.getName());
          }
        }
      } catch (IOException e) {
        throw new RuntimeException("Failed to read extracted resources directory.", e);
      }
    }

    if (allResourcesValid) {
      FctUtils.printInfo("All extracted resources are valid.");
    } else {
      FctUtils.printError("Some extracted resources are invalid.");
      throw new RuntimeException("Validation failed for some extracted resources.");
    }

    FctUtils.printCompletedInDuration(start);
  }

  static ArrayList<String> getResourceFiles(String pathToFolder) throws IOException {
    ArrayList<String> filesArray = new ArrayList<>();
    Path projectPath = Paths.get(pathToFolder);
    if (Files.isDirectory(projectPath)) {
      Files.walk(projectPath).forEach(path -> getFiles(filesArray, path.toFile()));
    } else if (Files.isRegularFile(projectPath)) {

      if (projectPath.getFileName().toString().endsWith(".json")) {
        addFhirResource(pathToFolder, filesArray);
      }
    }
    return filesArray;
  }

  static void getFiles(ArrayList<String> filesArray, File file) {
    if (file.isFile()) {
      if (file.getName().endsWith(".json")) {
        addFhirResource(file.getAbsolutePath(), filesArray);
      }
    }
  }

  static void addFhirResource(String filePath, List<String> filesArray) {
    try {
      JsonElement jsonElement = jsonParser.parse(new FileReader(filePath));
      JsonElement resourceType = jsonElement.getAsJsonObject().get("resourceType");
      if (resourceType != null && resourceType.toString().contains("Questionnaire")) {
        FctUtils.printInfo("Adding " + filePath);
        filesArray.add(filePath);
      }

    } catch (Exception e) {
      FctUtils.printError(e.getMessage());
    }
  }
}
