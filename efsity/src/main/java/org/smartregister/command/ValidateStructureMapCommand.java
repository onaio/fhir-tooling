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
      names = {"-c", "--composition"},
      description = "path of the composition configuration file",
      required = true)
  String compositionFilePath;

  @Override
  public void run() {
    if (inputPath != null && compositionFilePath != null) {
      try {
        validateStructureMap(inputPath, compositionFilePath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void validateStructureMap(String inputFilePath, String compositionFilePath)
      throws IOException {
    long start = System.currentTimeMillis();

    ArrayList<String> questionnaires = getResourceFiles(inputFilePath);
    for (String questionnairePath : questionnaires) {
      FctUtils.printInfo(questionnairePath);
      FctUtils.printInfo("Questionnaires available");
      try {
        ValidateFhirResourcesCommand.validateFhirResources(questionnairePath);
      } catch (ValidationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (GenerationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    FctUtils.printCompletedInDuration(start);
  }

  static ArrayList<String> getResourceFiles(String pathToFolder) throws IOException {
    ArrayList<String> filesArray = new ArrayList<>();
    Path projectPath = Paths.get(pathToFolder);
    if (Files.isDirectory(projectPath) && !pathToFolder.startsWith("x_")) {
      Files.walk(projectPath).forEach(path -> getFiles(filesArray, path.toFile()));
    } else if (Files.isRegularFile(projectPath)) {

      if (!projectPath.getFileName().toString().startsWith("x_")
          && projectPath.getFileName().toString().endsWith(".json")) {
        addFhirResource(pathToFolder, filesArray);
      } else {
        FctUtils.printWarning("Dropping " + projectPath.getFileName());
      }
    }
    return filesArray;
  }

  static void getFiles(ArrayList<String> filesArray, File file) {
    if (file.isFile()) {
      if (!file.getName().startsWith("x_") && file.getName().endsWith(".json")) {
        FctUtils.printWarning("Adding " + file.getAbsolutePath() + " with name: " + file.getName());
        addFhirResource(file.getAbsolutePath(), filesArray);
        FctUtils.printInfo("Questionnaires available 4");

      } else {
        FctUtils.printWarning(
            "Dropping " + file.getAbsolutePath() + " with name: " + file.getName());
      }
    }
  }

  private static void addFhirResource(String filePath, List<String> filesArray) {
    FctUtils.printInfo("Questionnaires available 1");

    try {
      FctUtils.printInfo("Questionnaires available 2");

      JsonElement jsonElement = jsonParser.parse(new FileReader(filePath));
      JsonElement resourceType = jsonElement.getAsJsonObject().get("resourceType");
      if (resourceType != null && resourceType.toString().contains("Questionnaire")) {
        FctUtils.printInfo(resourceType.toString());
        FctUtils.printInfo("Questionnaires available 3");
        filesArray.add(filePath);
      }

    } catch (Exception e) {
      FctUtils.printError(e.getMessage());
    }
  }
}
