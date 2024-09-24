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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
      names = {"-c", "--composition"},
      description = "path of the composition configuration file",
      required = false)
  String compositionFilePath;

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
        if (isProjectMode(inputPath)) {
          validateStructureMapForProject(inputPath, compositionFilePath, validate);
        } else {
          validateStructureMap(inputPath, validate, structureMapFilePath);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  boolean isProjectMode(String inputPath) {
    // Logic to determine if it's a project mode
    Path path = Paths.get(inputPath);
    return Files.isDirectory(path);
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

  void validateStructureMapForProject(
      String projectPath, String compositionFilePath, boolean validate) throws IOException {
    FctUtils.printInfo("Starting project mode validation with composition");

    // Step 1: Parse composition to get Questionnaires and StructureMaps sections
    JsonObject composition = parseCompositionFile(compositionFilePath);
    JsonArray questionnaires = getQuestionnairesFromComposition(composition);
    JsonArray structureMaps = getStructureMapsFromComposition(composition);

    if (questionnaires != null && structureMaps != null) {
      // Step 2: Get the questionnaire to SM ID map
      Map<String, String> questionnaireToStructureMapIdMap =
          getQuestionnaireToStructureMapIdMap(questionnaires, structureMaps);

      // Step 3: Loop through Questionnaires and check for corresponding StructureMap
      for (JsonElement questionnaireElement : questionnaires) {
        JsonObject questionnaire = questionnaireElement.getAsJsonObject();
        String questionnaireId = questionnaire.get("title").getAsString();

        // Get the corresponding SM ID from the map
        String structureMapId = questionnaireToStructureMapIdMap.get(questionnaireId);

        if (structureMapId != null) {
          FctUtils.printInfo("Found structure map: " + structureMapId);

          // Define paths for Questionnaire and StructureMap
          String questionnairePath = projectPath + "questionnaire/" + questionnaireId + ".json";
          String structureMapPath = projectPath + "structure_map/" + structureMapId + ".json";

          // Step 4: Validate StructureMap using the existing validateStructureMap function
          ArrayList<String> questionnairesList = new ArrayList<>();
          questionnairesList.add(questionnairePath);
          validateStructureMap(questionnairePath, validate, structureMapPath);
        } else {
          FctUtils.printWarning(
              "No matching structure map found for questionnaire: " + questionnaireId);
        }
      }
    } else {
      FctUtils.printError("Composition file is missing Questionnaires or StructureMaps sections.");
    }
  }

   Map<String, String> getQuestionnaireToStructureMapIdMap(
      JsonArray questionnaires, JsonArray structureMaps) {
    Map<String, String> questionnaireToStructureMapIdMap = new HashMap<>();

    for (JsonElement questionnaireElement : questionnaires) {
      JsonObject questionnaire = questionnaireElement.getAsJsonObject();
      String questionnaireId = questionnaire.get("title").getAsString();

      for (JsonElement structureMapElement : structureMaps) {
        JsonObject structureMap = structureMapElement.getAsJsonObject();
        String structureMapId = structureMap.get("title").getAsString();

        // Check if the questionnaire has a reference to the structure map
        if (hasQuestionnaireReferenceToStructureMap(questionnaire, structureMapId)) {
          questionnaireToStructureMapIdMap.put(questionnaireId, structureMapId);
          break;
        }
      }
    }

    return questionnaireToStructureMapIdMap;
  }

  private boolean hasQuestionnaireReferenceToStructureMap(
      JsonObject questionnaire, String structureMapId) {
    // Check if the questionnaire has an extension with a reference to the structure map
    if (questionnaire.has("extension")) {
      JsonArray extensions = questionnaire.getAsJsonArray("extension");
      for (JsonElement extensionElement : extensions) {
        JsonObject extension = extensionElement.getAsJsonObject();
        if (extension.has("url")
            && extension
                .get("url")
                .getAsString()
                .equals(
                    "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap")) {
          if (extension.has("valueReference")
              && extension
                  .getAsJsonObject("valueReference")
                  .get("reference")
                  .getAsString()
                  .equals("StructureMap/" + structureMapId)) {
            return true;
          }
        }
      }
    }

    return false;
  }

  JsonObject parseCompositionFile(String compositionFilePath) throws IOException {
    FctUtils.printInfo("Parsing composition file: " + compositionFilePath);
    FctFile compositionFile = FctUtils.readFile(compositionFilePath);
    return JsonParser.parseString(compositionFile.getContent()).getAsJsonObject();
  }

  JsonArray getQuestionnairesFromComposition(JsonObject composition) {
    if (composition.has("section")) {
      JsonArray sections = composition.getAsJsonArray("section");
      for (JsonElement section : sections) {
        JsonObject sectionObj = section.getAsJsonObject();
        if (sectionObj.has("title")
            && "Questionnaires".equals(sectionObj.get("title").getAsString())) {
          return sectionObj.getAsJsonArray("section"); // Get the sections under Questionnaires
        }
      }
    }
    return null;
  }

  JsonArray getStructureMapsFromComposition(JsonObject composition) {
    if (composition.has("section")) {
      JsonArray sections = composition.getAsJsonArray("section");
      for (JsonElement section : sections) {
        JsonObject sectionObj = section.getAsJsonObject();
        if (sectionObj.has("title")
            && "StructureMaps".equals(sectionObj.get("title").getAsString())) {
          return sectionObj.getAsJsonArray("section"); // Get the sections under StructureMaps
        }
      }
    }
    return null;
  }

  String findMatchingStructureMap(String questionnaireTitle, JsonArray structureMaps) {
    for (JsonElement structureMapElement : structureMaps) {
      JsonObject structureMap = structureMapElement.getAsJsonObject();

      // Check if the "title" field exists and is not null
      if (structureMap.has("title") && !structureMap.get("title").isJsonNull()) {
        String structureMapTitle = structureMap.get("title").getAsString();

        // Logic to match questionnaire title with structure map title
        if (structureMapTitle.equals(questionnaireTitle)) {
          return structureMapTitle; // Return the matched structure map title
        }
      }
    }
    return null;
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
