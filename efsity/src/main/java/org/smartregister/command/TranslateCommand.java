package org.smartregister.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "translate")
public class TranslateCommand implements Runnable {
  @CommandLine.Option(
    names = {"-m", "--mode"},
    description =
      "Options are either `extract` to generate the translation file from a questionnaire or " +
        "`merge` to import a translated file and populate the original questionnaire",
    required = true)
  private String mode;

  @CommandLine.Option(
    names = {"-rf", "--resourceFile"},
    description = "resource file path",
    required = true)
  private String resourceFile;

  @CommandLine.Option(
    names = {"-tf", "--translationFile"},
    description = "translation file path",
    required = false)
  private String translationFile;

  @CommandLine.Option(
    names = {"-l", "--locale"},
    description = "translation locale",
    required = false)
  private String locale;

  private final String[] modes = {"merge", "extract"};

  @Override
  public void run() {
    if (!Arrays.asList(modes).contains(mode)) {
      throw new RuntimeException("Modes should either be `extract` or `merge`");
    }
    try {
      if (Objects.equals(mode, "extract")) {
        long start = System.currentTimeMillis();

        FctUtils.printInfo("Starting text extraction");
        FctUtils.printInfo(String.format("Input file \u001b[35m%s\u001b[0m", resourceFile));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(resourceFile));

        // Create a properties object to store the results
        Properties properties = new Properties();

        // Start the recursive extraction
        extractTranslationText(rootNode, "", properties);

        // Write the properties to a file
        if (translationFile == null) {

          String fileName = getFileName(resourceFile);
          String defaultLanguage = getLanguageOrDefault(rootNode);
          translationFile = fileName + "_" + defaultLanguage + ".properties";
        }
        FctUtils.printInfo(String.format("Translation file \u001b[35m%s\u001b[0m", translationFile));
        properties.store(new FileOutputStream(translationFile), null);

        FctUtils.printInfo(String.format("Output file\u001b[36m %s \u001b[0m", translationFile));
        FctUtils.printCompletedInDuration(start);
      } else if (Objects.equals(mode, "merge")) {
        if (locale == null) {
          throw new RuntimeException("Translation Locale should be provided during merge.");
        }
        if (translationFile == null) {
          throw new RuntimeException("Translation file should be provided during merge.");
        }
        long start = System.currentTimeMillis();

        FctUtils.printInfo("Starting text merging");
        FctUtils.printInfo(String.format("Input file \u001b[35m%s\u001b[0m", resourceFile));
        FctUtils.printInfo(String.format("Translation file \u001b[35m%s\u001b[0m", translationFile));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonContent = objectMapper.readTree(new FileReader(resourceFile));
        List<String> properties = readPropertiesFile(translationFile);
        populateJson(properties, jsonContent, locale);

        // Serializing json
        String jsonObject = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonContent);

        // Writing to ni.json
        FileWriter fileWriter = new FileWriter("ni.json");
        fileWriter.write(jsonObject);
        fileWriter.close();

        FctUtils.printInfo(String.format("Output file\u001b[36m %s \u001b[0m", "updated_qs.json"));
        FctUtils.printCompletedInDuration(start);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void extractTranslationText(JsonNode node, String path, Properties properties)
    throws IOException {
    if (node.isObject()) {
      node.fields().forEachRemaining(entry -> {
        String key = entry.getKey();
        JsonNode value = entry.getValue();

        String newPath = path.isEmpty() ? key : path + "." + key;

        if (Objects.equals(key, "text")) {
          properties.setProperty(newPath, value.asText());
        }

        try {
          extractTranslationText(value, newPath, properties);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } else if (node.isArray()) {
      for (int i = 0; i < node.size(); i++) {
        extractTranslationText(node.get(i), path + "." + i, properties);
      }
    }
  }

  private static String getLanguageOrDefault(JsonNode jsonNode) {
    if (jsonNode.has("language")) {
      return jsonNode.get("language").asText();
    } else {
      return "default";
    }
  }

  private static List<String> readPropertiesFile(String filePath) throws IOException {
    List<String> properties = new ArrayList<>();
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    String line;
    while ((line = reader.readLine()) != null) {
      properties.add(line);
    }
    reader.close();
    return properties;
  }

  private static String getFileName(String filePath) {
    File file = new File(filePath);
    String fileName = file.getName();

    int lastIndex = fileName.lastIndexOf(".");
    if (lastIndex != -1) {
      return fileName.substring(0, lastIndex);
    } else {
      return fileName;
    }
  }

  private static void populateJson(List<String> properties, JsonNode jsonContent, String locale) {
    String url = "localhost:8000";

    for (String prop : properties) {
      if (prop.contains("=")) {
        String[] propParts = prop.split("=");
        String jsonPath = propParts[0];
        String value = propParts[1];
        String[] jsonPathList = jsonPath.split("\\.");
        replaceJson(jsonContent, jsonPathList, locale, value, url);
      }
    }
  }

  private static void replaceJson(JsonNode jsonContent, String[] jsonPathList, String language, String value, String url) {
    String currentPath = jsonPathList[0];
    ObjectMapper objectMapper = new ObjectMapper();

    if (!currentPath.equals("text")) {
      try {
        int index = Integer.parseInt(currentPath);
        jsonContent = jsonContent.get(index);
      } catch (NumberFormatException ignored) {
        jsonContent = jsonContent.get(currentPath);
      }

      String[] newPathList = new String[jsonPathList.length - 1];
      System.arraycopy(jsonPathList, 1, newPathList, 0, jsonPathList.length - 1);
      replaceJson(jsonContent, newPathList, language, value, url);
    } else {
      if (jsonContent.has("_text")) {
        JsonNode textNode = jsonContent.get("_text");
        if (!textNode.has("extension")) {
          // Create a new extension array
          ((com.fasterxml.jackson.databind.node.ObjectNode) textNode).putArray("extension");
        }

        boolean extensionExists = false;
        for (JsonNode extension : textNode.get("extension")) {
          for (JsonNode langExtension : extension.get("extension")) {
            if (langExtension.get("url").asText().equals("lang") &&
              langExtension.get("valueCode").asText().equals(language)) {
              extensionExists = true;
              break;
            }
          }
        }

        if (!extensionExists) {
          ((com.fasterxml.jackson.databind.node.ArrayNode) textNode.get("extension")).addPOJO(
            objectMapper.createObjectNode()
              .put("url", url)
              .putPOJO("extension", objectMapper.createArrayNode()
                .addPOJO(
                  objectMapper.createObjectNode()
                    .put("url", "lang")
                    .put("valueCode", language)
                )
                .addPOJO(
                  objectMapper.createObjectNode()
                    .put("url", "content")
                    .put("valueString", value)
                ))
          );
        }
      } else {
        ((com.fasterxml.jackson.databind.node.ObjectNode) jsonContent).set("_text",
          objectMapper.createObjectNode()
            .putPOJO("extension", objectMapper.createArrayNode()
              .addPOJO(objectMapper.createObjectNode()
                .put("url", url)
                .putPOJO("extension", objectMapper.createArrayNode()
                  .addPOJO(
                    objectMapper.createObjectNode()
                      .put("url", "lang")
                      .put("valueCode", language)
                  )
                  .addPOJO(
                    objectMapper.createObjectNode()
                      .put("url", "content")
                      .put("valueString", value)
                  ))
              ))
        );
      }
    }
  }
}
