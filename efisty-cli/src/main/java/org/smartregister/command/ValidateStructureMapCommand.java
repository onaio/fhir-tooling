package org.smartregister.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jimblackler.jsonschemafriend.GenerationException;
import net.jimblackler.jsonschemafriend.ValidationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.FctFile;
import org.smartregister.processor.FctValidationProcessor;
import org.smartregister.processor.QuestionnaireProcessor;
import org.smartregister.processor.StructureMapProcessor;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "validateStructureMap")
public class ValidateStructureMapCommand implements Runnable {
  static JsonParser jsonParser = new JsonParser();

  @CommandLine.Option(
      names = {"-i", "--input"},
      description = "path of the project or questionnaire folder",
      required = false)
  String inputPath;

  @CommandLine.Option(
      names = {"-q", "--questionnaire"},
      description = "path of the single questionnaire file",
      required = false)
  String questionnairePath;

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
    if (inputPath != null || questionnairePath != null) {

      long start = System.currentTimeMillis();
      try {
        if (isProjectMode(String.valueOf(inputPath != null && isProjectMode(inputPath)))) {
          validateStructureMapForProject(inputPath, structureMapFilePath, validate);
        } else {
          String questionnaireFilePath = questionnairePath != null ? questionnairePath : inputPath;
          validateStructureMap(questionnaireFilePath, structureMapFilePath, validate);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      FctUtils.printCompletedInDuration(start);
    }
  }

  boolean isProjectMode(String inputPath) {
    // Logic to determine if it's a project mode
    Path path = Paths.get(inputPath);
    return Files.isDirectory(path);
  }

  void validateStructureMap(String inputFilePath, String structureMapFilePath, boolean validate)
      throws IOException {
    FctUtils.printInfo("Starting structureMap validation");
    FctUtils.printInfo(
        String.format("Questionnaire file path \u001b[35m%s\u001b[0m", inputFilePath));
    FctUtils.printInfo(
        String.format("StructureMap file path \u001b[35m%s\u001b[0m", structureMapFilePath));

    ArrayList<String> questionnaires = getResourceFiles(inputFilePath);
    boolean allResourcesValid = true;

    // Fail if the list of questionnaires is empty
    if (questionnaires.isEmpty()) {
      FctUtils.printError("No questionnaires found. Validation cannot proceed.");
      throw new RuntimeException("Validation failed: No questionnaires found.");
    }
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

            int resourceCounter = 0;
            // Process each entry in the Bundle
            for (JsonElement entryElement : entries) {
              JsonObject entry = entryElement.getAsJsonObject();
              JsonObject resource = entry.getAsJsonObject("resource");

              if (resource != null) {
                String resourceType = resource.get("resourceType").getAsString();

                // Try to get the resource ID for a more unique file name
                String resourceId =
                    resource.has("id")
                        ? resource.get("id").getAsString()
                        : String.valueOf(resourceCounter++);
                // Create a unique file name for each resource using both the type and the resource
                // ID
                String resourceFileName = resourceType + "_" + resourceId + ".json";
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
                FctUtils.printWarning("No resource found in entry " + entryElement);
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
  }

  void validateStructureMapForProject(
      String questionnairesFolderPath, String structureMapsFolderPath, boolean validate)
      throws IOException {
    FctUtils.printInfo("Starting project mode validation");

    // Process questionnaires and structure maps
    Map<String, Set<String>> questionnaireToStructureMapId;

    // Check the project folder then find the questionnaire folder then pass it in the
    // Process questionnaires
    Map<String, Map<String, Set<String>>> questionnaireProcessorResults =
        new QuestionnaireProcessor(questionnairesFolderPath).process();
    questionnaireToStructureMapId =
        questionnaireProcessorResults.getOrDefault(
            FctValidationProcessor.Constants.structuremap, new HashMap<>());

    // Map identifiers to filenames for both questionnaires and structure maps
    Map<String, String> questionnaireFileMap = mapIdentifierWithFilePath(questionnairesFolderPath);

    // Use StructureMapProcessor for structure maps to get their ID-to-filename mapping
    StructureMapProcessor structureMapProcessor =
        new StructureMapProcessor(structureMapsFolderPath);
    Map<String, String> structureMapFileMap = structureMapProcessor.generateIdToFilepathMap();

    // Iterate over the map of questionnaires and corresponding structure maps
    for (Map.Entry<String, Set<String>> entry : questionnaireToStructureMapId.entrySet()) {
      String questionnaireId = entry.getKey();
      Set<String> structureMapIds = entry.getValue();

      FctUtils.printInfo("-----------------------------------------------");
      FctUtils.printInfo("Processing Questionnaire: " + questionnaireId);

      // Get the corresponding questionnaire filename
      String questionnaireFile = questionnaireFileMap.get(questionnaireId);
      if (questionnaireFile != null) {
        FctUtils.printInfo(
            "Questionnaire ID: " + questionnaireId + " -> File: " + questionnaireFile);

        // Handle multiple structure maps per questionnaire
        for (String structureMapId : structureMapIds) {
          FctUtils.printInfo("Processing StructureMap: " + structureMapId);

          // Get the corresponding structure map filename
          String structureMapFile = structureMapFileMap.get(structureMapId);
          if (structureMapFile != null) {
            FctUtils.printInfo(
                "StructureMap ID: " + structureMapId + " -> File: " + structureMapFile);

            // Call the existing validateStructureMap function for validation and resource
            // extraction
            try {
              validateStructureMap(questionnaireFile, structureMapFile, validate);
            } catch (IOException e) {
              FctUtils.printError("Error during structure map validation: " + e.getMessage());
              throw e; // Re-throw to maintain the behavior
            }

          } else {
            FctUtils.printWarning("No file found for structureMap: " + structureMapId);
          }
        }

      } else {
        FctUtils.printWarning("No file found for questionnaire: " + questionnaireId);
      }
    }
  }

  Map<String, String> mapIdentifierWithFilePath(String folderPath) throws IOException {
    Map<String, String> idToFileNameMap = new HashMap<>();

    File folder = new File(folderPath);
    File[] files = folder.listFiles();

    if (files != null) {
      for (File file : files) {
        if (file.isFile()
            && (file.getName().endsWith(".json") || file.getName().endsWith(".map"))) {
          String fileContent =
              new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
          String id = extractIdFromJson(fileContent);

          if (id != null && !id.isEmpty()) {
            idToFileNameMap.put(id, file.getAbsolutePath());
          }
        }
      }
    }
    return idToFileNameMap;
  }

  String extractIdFromJson(String jsonContent) {
    try {
      // Parse the JSON content to extract the ID (assuming the ID is under "id")
      JSONObject jsonObject = new JSONObject(jsonContent);
      return jsonObject.optString("id", null); // Adjust the key if necessary
    } catch (JSONException e) {
      FctUtils.printError("Failed to parse JSON content: " + e.getMessage());
      return null;
    }
  }

  boolean hasQuestionnaireReferenceToStructureMap(JsonObject questionnaire, String structureMapId) {
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

  static ArrayList<String> getResourceFiles(String pathToFolder) throws IOException {
    ArrayList<String> filesArray = new ArrayList<>();
    Path projectPath = Paths.get(pathToFolder);
    if (Files.isDirectory(projectPath)) {
      Files.walk(projectPath).forEach(path -> getFiles(filesArray, path.toFile()));
    } else if (Files.isRegularFile(projectPath)) {
      // Delegate to getFiles, as it already handles file checking logic
      getFiles(filesArray, projectPath.toFile());
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
