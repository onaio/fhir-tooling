package org.smartregister.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.*;

import org.smartregister.util.FCTConstants;
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

  @CommandLine.Option(
    names = {"-et", "--extractionType"},
    description = "extraction type",
    required = false)
  private String extractionType;

  private final String[] modes = {"merge", "extract"};
  private final String[] extractionTypes = {"all", "configs", "fhirContent"};

  @Override
  public void run() {
    if (!Arrays.asList(modes).contains(mode)) {
      throw new RuntimeException("Modes should either be `extract` or `merge`");
    }
    if (extractionType != null && !Arrays.asList(extractionTypes).contains(extractionType)) {
      throw new RuntimeException("extractionTypes should either be `all`, `configs`, `fhir_content`");
    }

    if (Objects.equals(mode, "extract")) {
      long start = System.currentTimeMillis();

      Path inputFilePath = Paths.get(resourceFile);
      FctUtils.printInfo("Starting extraction");
      FctUtils.printInfo(String.format("Input file \u001b[35m%s\u001b[0m", resourceFile));

      try {
        // Check if the input path is a directory or a JSON file
        if (Files.isDirectory(inputFilePath)) {

          if (Objects.equals(extractionType, "configs") || inputFilePath.endsWith("configs") ) {
            Set targetFields = FCTConstants.configTranslatables;
            if (translationFile == null) {
              translationFile = inputFilePath.resolve("translations/strings_configs.properties").toString();
            }
            extractDirectories(translationFile, inputFilePath, targetFields);
          } else if (Objects.equals(extractionType, "fhirContent") || inputFilePath.endsWith("fhir_content") ) {
            Set targetFields = FCTConstants.questionnaireTranslatables;
            if (translationFile == null) {
              translationFile = inputFilePath.resolve("translations/strings_default.properties").toString();
            }
            extractDirectories(translationFile, inputFilePath, targetFields);
          } else if (extractionType == null || Objects.equals(extractionType, "all") ) {
            Path configsPath = inputFilePath.resolve("configs");
            Path fhirContentPath = inputFilePath.resolve("fhir_content");
            if (Files.exists(configsPath) && Files.isDirectory(configsPath)) {
              Set targetFields = FCTConstants.configTranslatables;
              String configsTranslationFile = null;
              if (translationFile == null) {
                configsTranslationFile = configsPath.resolve(
                  "translations/strings_configs.properties").toString();
              } else {
                configsTranslationFile = translationFile;
              }
              extractDirectories(configsTranslationFile, configsPath, targetFields);
            } else {
              FctUtils.printWarning("`configs` directory not found in directory");
            }
            if (Files.exists(fhirContentPath) && Files.isDirectory(fhirContentPath)) {
              Set targetFields = FCTConstants.questionnaireTranslatables;
              String contentTranslationFile = null;
              if (translationFile == null) {
                contentTranslationFile = fhirContentPath.resolve(
                  "translations/strings_default.properties").toString();
              } else {
                contentTranslationFile = translationFile;
              }
              extractDirectories(contentTranslationFile, fhirContentPath, targetFields);
            } else {
              FctUtils.printWarning("`fhir_content` directory not found in directory");
            }
          }
        } else if (Files.isRegularFile(inputFilePath) && resourceFile.endsWith(".json")) {
          if (translationFile == null) {
            throw new RuntimeException("Provide translation file when extracting from a specific file");
          }
          if (extractionType == null) {
            throw new RuntimeException("Provide extractionType when extracting from a specific file");
          }
          Set targetFields;
          if (extractionType.equals("configs")) {
            targetFields = FCTConstants.configTranslatables;
          } else {
            targetFields = FCTConstants.questionnaireTranslatables;
          }
          extractDirectories(translationFile, inputFilePath, targetFields);
        } else {
          throw new RuntimeException("Invalid input path. Please provide a directory or a JSON file.");
        }
        FctUtils.printCompletedInDuration(start);
      } catch (IOException | NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static void extractDirectories(
    String translationFile, Path inputFilePath, Set targetFields)
    throws IOException, NoSuchAlgorithmException {
    Map<String, String> textToHash = new HashMap<>();
    Path propertiesFilePath = Paths.get(translationFile);

    if (Files.isRegularFile(inputFilePath) && inputFilePath.endsWith(".json")) {
      processJsonFile(inputFilePath, textToHash, targetFields);
    } else {
      Files.walk(inputFilePath)
        .filter(Files::isRegularFile)
        .filter(file -> file.toString().endsWith(".json"))
        .forEach(file -> {
          try {
            processJsonFile(file, textToHash, targetFields);
          } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
          }
        }) ;
    }

    // Read existing properties file, if it exists
    Properties existingProperties = new Properties();

    if (Files.exists(propertiesFilePath)) {
      try (InputStream input = new FileInputStream(propertiesFilePath.toFile())) {
        existingProperties.load(input);
      }
    }
    // Merge existing properties with new properties
    existingProperties.putAll(textToHash);
    writePropertiesFile(existingProperties, translationFile);
    FctUtils.printInfo(String.format("Translation file \u001b[35m%s\u001b[0m", translationFile));
    FctUtils.printInfo(String.format("Output file\u001b[36m %s \u001b[0m", translationFile));

  }

  private static void processJsonFile(Path filePath, Map<String, String> textToHash, Set targetFields)
    throws IOException, NoSuchAlgorithmException {
    FctUtils.printInfo(String.format("Extracting from \u001b[35m%s\u001b[0m", filePath));
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(Files.newBufferedReader(filePath, StandardCharsets.UTF_8));
    findTargetFields(rootNode, textToHash, targetFields);
  }

  private static void findTargetFields(JsonNode node, Map<String, String> textToHash, Set targetFields)
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
            String text = fieldValue.asText();
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

  private static void writePropertiesFile(Properties properties, String filePath) throws IOException {
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
}
