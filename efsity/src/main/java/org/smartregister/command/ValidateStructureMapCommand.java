package org.smartregister.command;

import java.io.IOException;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "validateStructureMap")
public class ValidateStructureMapCommand implements Runnable {

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
        validateStructureMap(inputPath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void validateStructureMap(String inputFilePath) throws IOException {
    long start = System.currentTimeMillis();

    FctUtils.printInfo("Starting structureMap validation");
    FctUtils.printInfo(String.format("Input file path \u001b[35m%s\u001b[0m", inputFilePath));

    Map<String, String> questionnaireToStructureMap = parseCompositionFile(compositionFilePath);

    for (Map.Entry<String, String> entry : questionnaireToStructureMap.entrySet()) {
      String questionnaire = entry.getKey();
      String structureMap = entry.getValue();

      boolean isValid = validateStructureMapFile(structureMap, inputFilePath);

      if (isValid) {
        FctUtils.printInfo(String.format("Valid StructureMap found for Questionnaire: %s -> %s", questionnaire, structureMap));
        processStructureMap(structureMap);
      } else {
        FctUtils.printWarning(String.format("Invalid or missing StructureMap for Questionnaire: %s -> %s", questionnaire, structureMap));
      }
    }

    FctUtils.printCompletedInDuration(start);
  }

  private Map<String, String> parseCompositionFile(String compositionFilePath) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(new File(compositionFilePath));

    Map<String, String> questionnaireToStructureMap = new HashMap<>();
    Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      String questionnaire = field.getKey();
      String structureMap = field.getValue().asText();
      questionnaireToStructureMap.put(questionnaire, structureMap);
    }
    return questionnaireToStructureMap;
  }

  private boolean validateStructureMapFile(String structureMap, String inputFilePath) {
    File structureMapFile = new File(inputFilePath, structureMap + ".map");
    return structureMapFile.exists();
  }

  private void processStructureMap(String structureMap) {
    FctUtils.printInfo(String.format("Processing StructureMap: %s", structureMap));
    // Add your processing logic here
  }
}
