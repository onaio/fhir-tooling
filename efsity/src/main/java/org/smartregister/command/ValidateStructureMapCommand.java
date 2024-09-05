package org.smartregister.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.jimblackler.jsonschemafriend.GenerationException;
import net.jimblackler.jsonschemafriend.ValidationException;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "validateStructureMap")
public class ValidateStructureMapCommand implements Runnable {
  static JsonParser jsonParser = new JsonParser();

  @CommandLine.Option(
      names = {"-i", "--input"},
      description = "path of the project folder",
      required = true)
  private String inputPath;

  @CommandLine.Option(
      names = {"-v", "--validate"},
      description = "validate the fhir resources ",
      defaultValue = "false")
  private boolean validate;

  @Override
  public void run() {
    if (inputPath != null) {
      try {
        validateStructureMap(inputPath, validate);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void validateStructureMap(String inputFilePath, boolean validate) throws IOException {
    long start = System.currentTimeMillis();
    FctUtils.printInfo("Starting structureMap validation");
    FctUtils.printInfo(String.format("Input file path \u001b[35m%s\u001b[0m", inputFilePath));

    ArrayList<String> questionnaires = getResourceFiles(inputFilePath);
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

      // TODO This currently creates all the questionnaireResponses in the current path (.)
      //  Create a folder called generatedResources if none exists in the current path
      //  and save the generated resources there

      // Generate QuestionnaireResponse
      QuestionnaireResponseGeneratorCommand.generateResponse(
          questionnairePath, "populate", ".", "", "http://localhost:8080/fhir", "", "", "");
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

  private static void addFhirResource(String filePath, List<String> filesArray) {
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
