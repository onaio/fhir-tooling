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

    if (Objects.equals(mode, "extract")) {
      long start = System.currentTimeMillis();

      FctUtils.printInfo("Starting text extraction");
      FctUtils.printInfo(String.format("Input file \u001b[35m%s\u001b[0m", resourceFile));
      Map<String, String> textToHash = new HashMap<>();

      try {
        // Check if the input path is a directory or a JSON file
        Path inputFilePath = Paths.get(resourceFile);
        if (Files.isDirectory(inputFilePath)) {
          if (translationFile == null) {
            translationFile = inputFilePath.resolve("translations/strings_default.properties").toString();
          }
          Files.walk(inputFilePath)
            .filter(Files::isRegularFile)
            .filter(file -> file.toString().endsWith(".json"))
            .forEach(file -> {
              try {
                processJsonFile(file, textToHash);
              } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
              }
            });
        } else if (Files.isRegularFile(inputFilePath) && resourceFile.endsWith(".json")) {
          if (translationFile == null) {
            translationFile = inputFilePath.getParent()
              .resolve("translation/strings_default.properties").toString();
          }
          processJsonFile(inputFilePath, textToHash);
        } else {
          System.out.println("Invalid input path. Please provide a directory or a JSON file.");
          return;
        }

        // Read existing properties file, if it exists
        Properties existingProperties = new Properties();
        Path propertiesFilePath = Paths.get(translationFile);

        if (Files.exists(propertiesFilePath)) {
          try (InputStream input = new FileInputStream(propertiesFilePath.toFile())) {
            existingProperties.load(input);
          }
        }
        // Merge existing properties with new properties
        existingProperties.putAll(textToHash);

        // Write the updated properties to a new file
        writePropertiesFile(existingProperties, translationFile);
        FctUtils.printInfo(String.format("Translation file \u001b[35m%s\u001b[0m", translationFile));
        FctUtils.printInfo(String.format("Output file\u001b[36m %s \u001b[0m", translationFile));
        FctUtils.printCompletedInDuration(start);
      } catch (IOException | NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    }

  }

  private static void processJsonFile(Path filePath, Map<String, String> textToHash)
    throws IOException, NoSuchAlgorithmException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(Files.newBufferedReader(filePath, StandardCharsets.UTF_8));
    findTextFields(rootNode, textToHash);
  }

  private static void findTextFields(JsonNode node, Map<String, String> textToHash)
    throws NoSuchAlgorithmException {
    if (node.isObject()) {
      ObjectNode objectNode = (ObjectNode) node;
      Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> field = fields.next();
        if ("text".equals(field.getKey()) && field.getValue().isTextual()) {
          String text = field.getValue().asText();
          String md5Hash = calculateMD5Hash(text);
          textToHash.put(md5Hash, text);
        } else if (field.getValue().isObject() || field.getValue().isArray()) {
          findTextFields(field.getValue(), textToHash);
        }
      }
    } else if (node.isArray()) {
      for (JsonNode arrayNode : node) {
        findTextFields(arrayNode, textToHash);
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
