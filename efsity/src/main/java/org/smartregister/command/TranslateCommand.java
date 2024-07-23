package org.smartregister.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.smartregister.util.FCTConstants;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "translate")
public class TranslateCommand implements Runnable {
  @CommandLine.Option(
      names = {"-m", "--mode"},
      description =
          "Options are either `extract` to generate the translation file from a questionnaire or "
              + "`merge` to import a translated file and populate the original questionnaire",
      required = true)
  String mode;

  @CommandLine.Option(
      names = {"-rf", "--resourceFile"},
      description = "resource file path",
      required = true)
  String resourceFile;

  @CommandLine.Option(
      names = {"-tf", "--translationFile"},
      description = "translation file path",
      required = false)
  String translationFile;

  @CommandLine.Option(
      names = {"-l", "--locale"},
      description = "translation locale",
      required = false)
  String locale;

  @CommandLine.Option(
      names = {"-et", "--extractionType"},
      description = "extraction type",
      required = false)
  String extractionType;

  private final String[] modes = {"merge", "extract"};
  private final String[] extractionTypes = {"all", "configs", "fhirContent"};

  private static String url = "http://hl7.org/fhir/StructureDefinition/translation";

  Path tempsConfig = null;
  Path tempFilePath = null;

  @Override
  public void run() {
    if (!Arrays.asList(modes).contains(mode)) {
      throw new RuntimeException("Modes should either be `extract` or `merge`");
    }
    if (extractionType != null && !Arrays.asList(extractionTypes).contains(extractionType)) {
      throw new RuntimeException(
          "extractionTypes should either be `all`, `configs`, `fhir_content`");
    }

    if (Objects.equals(mode, "extract")) {
      long start = System.currentTimeMillis();

      Path inputFilePath = Paths.get(resourceFile);
      FctUtils.printInfo(String.format("Input file \u001b[35m%s\u001b[0m", resourceFile));

      try {
        if (Objects.equals(translationFile, null)) {
          Path translationsDirectoryPath = getTranslationDirectoryPath(inputFilePath);
          String defaultTranslationFile = translationsDirectoryPath + "/strings_default.properties";
          if (!Files.exists(translationsDirectoryPath)) {
            Files.createDirectories(translationsDirectoryPath);
            Files.createFile(Paths.get(defaultTranslationFile));
          }
          translationFile = defaultTranslationFile;
        }
        tempsConfig = Files.createTempDirectory("configs");

        // Check if the input path is a directory or a JSON file
        if (Files.isDirectory(inputFilePath)) {
          if ("configs".equals(extractionType) || inputFilePath.endsWith("configs")) {
            // handle case where extractionType has not been given and inputFilePath ends with
            // configs
            if (Objects.equals(extractionType, null)) extractionType = "configs";
            Set<String> targetFields = FCTConstants.configTranslatables;
            copyDirectoryContent(inputFilePath, tempsConfig);
            extractContent(translationFile, inputFilePath, targetFields, extractionType);
          } else if (Objects.equals(extractionType, "fhirContent")
              || inputFilePath.endsWith("fhir_content")) {
            extractionType = "fhirContent";
            Set<String> targetFields = FCTConstants.questionnaireTranslatables;

            if (!inputFilePath.endsWith("questionnaires")) {
              inputFilePath = inputFilePath.resolve("questionnaires");
            }
            extractContent(translationFile, inputFilePath, targetFields, extractionType);
          } else if (extractionType == null || Objects.equals(extractionType, "all")) {
            Path configsPath = inputFilePath.resolve("configs");
            Path fhirContentPath = inputFilePath.resolve("fhir_content");
            Path questionnairePath = fhirContentPath.resolve("questionnaires");

            if (Files.exists(configsPath) && Files.isDirectory(configsPath)) {
              extractionType = "configs";
              copyDirectoryContent(configsPath, tempsConfig);
              Set<String> targetFields = FCTConstants.configTranslatables;
              extractContent(translationFile, configsPath, targetFields, extractionType);
            } else {
              FctUtils.printWarning("`configs` directory not found in directory");
            }
            if (Files.exists(fhirContentPath)
                && Files.isDirectory(fhirContentPath)
                && Files.exists(questionnairePath)
                && Files.isDirectory(questionnairePath)) {
              extractionType = "fhirContent";
              Set<String> targetFields = FCTConstants.questionnaireTranslatables;

              extractContent(translationFile, questionnairePath, targetFields, extractionType);
            } else {
              FctUtils.printWarning(
                  "`fhir_content` or `fhir_content/questionnaires` directory not found in directory");
            }
          }
        } else if (Files.isRegularFile(inputFilePath) && resourceFile.endsWith(".json")) {
          if (translationFile == null) {
            throw new RuntimeException(
                "Provide translation file when extracting from a specific file");
          }
          if (extractionType == null) {
            throw new RuntimeException(
                "Provide extractionType when extracting from a specific file");
          }
          Set<String> targetFields;
          if (extractionType.equals("configs")) {
            targetFields = FCTConstants.configTranslatables;
          } else {
            targetFields = FCTConstants.questionnaireTranslatables;
          }
          extractContent(translationFile, inputFilePath, targetFields, extractionType);
        } else {
          throw new RuntimeException(
              "Invalid input path. Please provide a directory or a JSON file.");
        }
        FctUtils.printCompletedInDuration(start);
      } catch (IOException | NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    } else if (Objects.equals(mode, "merge")) {
      if (translationFile == null) {
        throw new RuntimeException("For merge, translationFile and locale must be provided.");
      }

      if (locale == null) {
        // Assuming the translation file name follows the format "strings_{locale}.properties"
        String[] parts = translationFile.split("_");
        if (parts.length == 2 && parts[0].equals("strings") && parts[1].endsWith(".properties")) {
          locale = parts[1].substring(0, parts[1].length() - ".properties".length());
        } else {
          throw new RuntimeException(
              "Failed to determine the locale from the translation file name.");
        }
      }

      FctUtils.printInfo("Starting merge");
      FctUtils.printInfo(String.format("Input file \u001b[35m%s\u001b[0m", resourceFile));
      FctUtils.printInfo(String.format("Translation file \u001b[35m%s\u001b[0m", translationFile));

      try {
        Path inputFilePath = Paths.get(resourceFile);
        Set<String> targetFields = FCTConstants.questionnaireTranslatables;

        if (Files.isRegularFile(inputFilePath) && inputFilePath.toString().endsWith(".json")) {
          mergeContent(inputFilePath, translationFile, locale, targetFields);
        } else if (Files.isDirectory(inputFilePath)) {
          if (!inputFilePath.endsWith("questionnaires")) {
            inputFilePath = inputFilePath.resolve("questionnaires");
          }
          Files.walk(inputFilePath)
              .filter(Files::isRegularFile)
              .filter(file -> file.toString().endsWith(".json"))
              .forEach(
                  file -> {
                    try {
                      mergeContent(file, translationFile, locale, targetFields);

                    } catch (IOException | NoSuchAlgorithmException e) {
                      throw new RuntimeException(e);
                    }
                  });
        } else {
          throw new RuntimeException("Provide a valid `resourceFile` directory or file.");
        }
      } catch (IOException | NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @NotNull private static Path getTranslationDirectoryPath(Path inputFilePath) {
    Path translationsDirectoryPath;
    if (inputFilePath.endsWith("configs")
        || inputFilePath.endsWith("fhir_content")
        || inputFilePath.toString().endsWith(".json")) {
      if (inputFilePath.toString().endsWith(".json")) {
        translationsDirectoryPath = inputFilePath.getParent().getParent().resolve("translation");
      } else translationsDirectoryPath = inputFilePath.getParent().resolve("translation");
    } else translationsDirectoryPath = inputFilePath.resolve("translation");
    return translationsDirectoryPath;
  }

  private static void mergeContent(
      Path inputFilePath, String translationFile, String locale, Set<String> targetFields)
      throws IOException, NoSuchAlgorithmException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode =
        objectMapper.readTree(Files.newBufferedReader(inputFilePath, StandardCharsets.UTF_8));

    // Load the translation properties
    Properties translationProperties = FctUtils.readPropertiesFile(translationFile);

    // Traverse and update the JSON structure
    JsonNode updatedJson = updateJson(rootNode, translationProperties, locale, targetFields);

    // Write the updated JSON to the output file
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    objectMapper.writeValue(inputFilePath.toFile(), updatedJson);
    FctUtils.printInfo(
        String.format("Merged JSON saved to \u001b[36m%s\u001b[0m", inputFilePath.toString()));
  }

  private static JsonNode updateJson(
      JsonNode node, Properties translationProperties, String locale, Set<String> targetFields)
      throws NoSuchAlgorithmException {
    if (node.isObject()) {
      ObjectNode objectNode = (ObjectNode) node;
      ObjectNode updatedNode = objectNode.objectNode();

      Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> field = fields.next();
        String fieldName = field.getKey();
        JsonNode fieldValue = field.getValue();

        if (targetFields.contains(fieldName)) {
          if (fieldValue.isTextual()) {
            String trimmedStringValue = fieldValue.asText().trim();
            String translationKey = calculateMD5Hash(trimmedStringValue);
            String translation = translationProperties.getProperty(translationKey);

            if (translation != null) {
              String newFieldName = "_" + fieldName;
              JsonNode existingField = objectNode.get(newFieldName);

              if (existingField != null && existingField.isObject()) {
                // If the existing field is an object, add a new language object to it
                ObjectNode extensionNode = updatedNode.objectNode();
                ObjectNode extExtensionNode = updatedNode.objectNode();
                extensionNode.put("url", url);
                extensionNode.set("extension", createExtensionNode(locale, translation));
                ArrayNode extensionArray = (ArrayNode) existingField.get("extension");

                ArrayNode updatedArray =
                    updateExtensionWithTranslation(extensionArray, extensionNode, locale);
                extExtensionNode.set("extension", updatedArray);
                updatedNode.set(newFieldName, extExtensionNode);
              } else {
                // Create a new field with underscore prefix
                ObjectNode extensionNode = updatedNode.objectNode();
                ObjectNode extExtensionNode = updatedNode.objectNode();
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode extensionArray = new ArrayNode(objectMapper.getNodeFactory());
                extensionNode.put("url", url);
                extensionNode.set("extension", createExtensionNode(locale, translation));
                extensionArray.add(extensionNode);
                extExtensionNode.set("extension", extensionArray);
                updatedNode.set(newFieldName, extExtensionNode);
              }
            }
          }
        }
        if (fieldValue.isObject() || fieldValue.isArray()) {
          // Recursively update nested objects or arrays
          updatedNode.set(
              fieldName, updateJson(fieldValue, translationProperties, locale, targetFields));
        } else {
          updatedNode.set(fieldName, fieldValue);
        }
      }
      return updatedNode;
    } else if (node.isArray()) {
      ArrayNode arrayNode = (ArrayNode) node;
      ArrayNode updatedArray = arrayNode.arrayNode();
      for (JsonNode arrayElement : arrayNode) {
        updatedArray.add(updateJson(arrayElement, translationProperties, locale, targetFields));
      }
      return updatedArray;
    } else {
      return node;
    }
  }

  public static ArrayNode updateExtensionWithTranslation(
      ArrayNode arrayNode, ObjectNode objectNode, String locale) {
    boolean localeExists = false;
    int translationIdx = 0;
    for (int i = 0; i < arrayNode.size(); i++) {
      JsonNode jsonNode = arrayNode.get(i);
      if (jsonNode.has("extension")) {
        JsonNode extensionNode = jsonNode.get("extension");
        if (extensionNode.isArray()) {
          for (JsonNode extension : extensionNode) {
            if (extension.has("valueCode") && extension.get("valueCode").asText().equals(locale)) {
              localeExists = true;
              translationIdx = i;
              break;
            }
          }
        }
      }
    }
    if (localeExists) {
      arrayNode.remove(translationIdx);
    }
    arrayNode.add(objectNode);
    return arrayNode;
  }

  private static JsonNode createExtensionNode(String locale, String translation) {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode languageNode = objectMapper.createObjectNode();
    languageNode.put("url", "lang");
    languageNode.put("valueCode", locale);

    ObjectNode contentNode = objectMapper.createObjectNode();
    contentNode.put("url", "content");
    contentNode.put("valueString", translation);

    ArrayNode extensionArray = objectMapper.createArrayNode();
    extensionArray.add(languageNode);
    extensionArray.add(contentNode);

    return extensionArray;
  }

  private void extractContent(
      String translationFile, Path inputFilePath, Set<String> targetFields, String extractionType)
      throws IOException, NoSuchAlgorithmException {
    Map<String, String> textToHash = new HashMap<>();
    Path propertiesFilePath = Paths.get(translationFile);
    if (Files.isRegularFile(inputFilePath)
        && inputFilePath.toString().toLowerCase(Locale.ENGLISH).endsWith(".json")) {
      if (Objects.equals(extractionType, "configs")) {
        String configFileSubDirectory =
            inputFilePath.subpath(2, inputFilePath.getNameCount() - 1).toString();
        try {
          Path tempConfigSubDirectory = tempsConfig.resolve(configFileSubDirectory);
          if (!Files.exists(tempConfigSubDirectory)) {
            Files.createDirectories(tempConfigSubDirectory);
            tempFilePath = tempConfigSubDirectory.resolve(inputFilePath.getFileName());
            // copy over content
            Files.copy(inputFilePath, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
          }

        } catch (IOException e) {
          throw new RuntimeException("Error creating temp file " + e);
        }
        // Extract and replace target fields with hashed values
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode =
            objectMapper.readTree(Files.newBufferedReader(inputFilePath, StandardCharsets.UTF_8));
        FctUtils.printInfo(
            String.format(
                "Extracting config file \u001b[35m%s\u001b[0m", inputFilePath.toString()));

        replaceTargetFieldsWithHashedValues(
            rootNode, targetFields, textToHash, inputFilePath, tempsConfig);
      } else {
        // For other types (content/questionnaire), extract as usual
        processJsonFile(inputFilePath, textToHash, targetFields);
      }
    } else if (Files.isDirectory(inputFilePath)) {
      // Handle the case where inputFilePath is a directory (folders may contain multiple JSON
      // files)
      Path inputDir;
      if (extractionType.equals("configs")) {
        inputDir = tempsConfig;
      } else inputDir = inputFilePath;

      Files.walk(inputDir)
          .filter(Files::isRegularFile)
          .filter(file -> file.toString().endsWith(".json"))
          .forEach(
              file -> {
                try {
                  if ("configs".equals(extractionType)) {
                    // Extract and replace target fields with hashed values
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode =
                        objectMapper.readTree(
                            Files.newBufferedReader(file, StandardCharsets.UTF_8));
                    FctUtils.printInfo(
                        String.format(
                            "Extracting config file \u001b[35m%s\u001b[0m", file.toString()));

                    replaceTargetFieldsWithHashedValues(
                        rootNode, targetFields, textToHash, file, tempsConfig);
                  } else {
                    // For other types (content/questionnaire), extract as usual
                    processJsonFile(file, textToHash, targetFields);
                  }
                } catch (IOException | NoSuchAlgorithmException e) {
                  if (tempsConfig != null && Files.exists(tempsConfig))
                    deleteDirectoryRecursively(tempsConfig);
                  throw new RuntimeException(
                      "Error while reading file " + file.getFileName() + " " + e);
                }
              });
    } else {
      throw new RuntimeException("Provide a valid `resourceFile` directory or file.");
    }

    // Copy over translations from temp
    if (extractionType.equals("configs")) {
      if (Files.isDirectory(inputFilePath)) copyDirectoryContent(tempsConfig, inputFilePath);
      else {
        assert tempFilePath != null;
        Files.copy(tempFilePath, inputFilePath, StandardCopyOption.REPLACE_EXISTING);
      }
    }

    if (!Files.exists(propertiesFilePath)) Files.createFile(propertiesFilePath);
    Properties existingProperties = FctUtils.readPropertiesFile(propertiesFilePath.toString());

    // Merge existing properties with new properties
    existingProperties.putAll(textToHash);
    writePropertiesFile(existingProperties, translationFile);
    FctUtils.printInfo(String.format("Translation file\u001b[36m %s \u001b[0m", translationFile));
    if (tempsConfig != null && Files.exists(tempsConfig)) deleteDirectoryRecursively(tempsConfig);
  }

  private static void processJsonFile(
      Path filePath, Map<String, String> textToHash, Set<String> targetFields)
      throws IOException, NoSuchAlgorithmException {
    FctUtils.printInfo(String.format("Extracting from \u001b[35m%s\u001b[0m", filePath));
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode =
        objectMapper.readTree(Files.newBufferedReader(filePath, StandardCharsets.UTF_8));
    findTargetFields(rootNode, textToHash, targetFields);
  }

  private static void replaceTargetFieldsWithHashedValues(
      JsonNode node,
      Set<String> targetFields,
      Map<String, String> textToHash,
      Path filePath,
      Path tempConfigsDir)
      throws NoSuchAlgorithmException, IOException {

    if (node.isObject()) {
      ObjectNode objectNode = (ObjectNode) node;
      for (String fieldName : targetFields) {
        JsonNode fieldValue = objectNode.get(fieldName);
        if (fieldValue != null && fieldValue.isTextual()) {
          String text = fieldValue.asText().replace("\"", "").trim();
          // Check if the field has not already been extracted
          if (!text.startsWith("{{") || !text.endsWith("}}")) {
            String md5Hash = calculateMD5Hash(text);
            textToHash.put(md5Hash, text);
            objectNode.put(fieldName, "{{ " + md5Hash + " }}");
          }
        }
      }
      Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> field = fields.next();
        JsonNode fieldValue = field.getValue();
        if (fieldValue.isObject() || fieldValue.isArray()) {
          // Recursively update nested objects or arrays
          replaceTargetFieldsWithHashedValues(
              fieldValue, targetFields, textToHash, filePath, tempConfigsDir);
        }
      }
    } else if (node.isArray()) {
      ArrayNode arrayNode = (ArrayNode) node;
      for (int i = 0; i < arrayNode.size(); i++) {
        JsonNode arrayElement = arrayNode.get(i);
        if (arrayElement.isObject() || arrayElement.isArray()) {
          // Recursively update nested objects or arrays
          replaceTargetFieldsWithHashedValues(
              arrayElement, targetFields, textToHash, filePath, tempConfigsDir);
        }
      }
    }

    String configFileSubDirectory = filePath.subpath(2, filePath.getNameCount()).toString();

    Path tempFilePath = tempConfigsDir.resolve(configFileSubDirectory);
    // Write the updated JSON to temp file
    try (BufferedWriter writer = Files.newBufferedWriter(tempFilePath, StandardCharsets.UTF_8)) {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      objectMapper.writeValue(writer, node);
    }
  }

  private static void findTargetFields(
      JsonNode node, Map<String, String> textToHash, Set<String> targetFields)
      throws NoSuchAlgorithmException {
    if (node.isObject()) {
      ObjectNode objectNode = (ObjectNode) node;
      Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();

      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> field = fields.next();
        String fieldName = field.getKey();
        JsonNode fieldValue = field.getValue();

        if (targetFields.contains(fieldName)) {
          if (fieldValue.isTextual()) {
            String text = fieldValue.asText().trim();
            String md5Hash = calculateMD5Hash(text);
            textToHash.put(md5Hash, text);
          }
        }
        if (fieldValue.isObject() || fieldValue.isArray()) {
          findTargetFields(fieldValue, textToHash, targetFields);
        }
      }
    } else if (node.isArray()) {
      for (JsonNode arrayNode : node) {
        findTargetFields(arrayNode, textToHash, targetFields);
      }
    }
  }

  private static String calculateMD5Hash(String text) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));

    StringBuilder result = new StringBuilder();
    for (byte b : bytes) {
      result.append(String.format("%02x", b));
    }
    return result.toString();
  }

  private static void writePropertiesFile(Properties properties, String filePath)
      throws IOException {
    Path propertiesFilePath = Paths.get(filePath);
    Path translationDirectory = propertiesFilePath.getParent();
    if (translationDirectory != null && !Files.exists(translationDirectory)) {
      try {
        Files.createDirectories(translationDirectory);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    try (OutputStream output = new FileOutputStream(filePath)) {
      properties.store(output, null);
    }
  }

  public void copyDirectoryContent(Path sourceDir, Path destinationDir) {
    try {
      Files.walkFileTree(
          sourceDir,
          new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
              Path targetDir = destinationDir.resolve(sourceDir.relativize(dir));
              if (!Files.exists(targetDir)) {
                Files.createDirectory(targetDir);
              }
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {

              Files.copy(
                  file,
                  destinationDir.resolve(sourceDir.relativize(file)),
                  StandardCopyOption.REPLACE_EXISTING);
              return FileVisitResult.CONTINUE;
            }
          });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteDirectoryRecursively(Path dirPath) {

    try {
      // Delete the directory and its contents recursively
      Files.walkFileTree(
          dirPath,
          new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              Files.delete(file);
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
              Files.delete(dir);
              return FileVisitResult.CONTINUE;
            }
          });

      System.out.println("Directory and its contents deleted successfully: " + dirPath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
