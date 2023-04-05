/* (C)2023 */
package org.smartregister.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.smartregister.domain.FCTFile;

public class FCTUtils {

  private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public static void printToConsole(String message) {
    System.out.println("EFSITY: " + message);
  }

  public static void printWarning(String message) {
    printToConsole(":: \u001b[33mWARN\u001b[0m  :: " + message);
  }

  public static void printError(String message) {
    printToConsole(":: \u001b[31mERROR\u001b[0m :: " + message);
  }

  public static void printInfo(String message) {
    printToConsole(":: \u001b[34mINFO\u001b[0m  :: " + message);
  }

  public static void printNewLine() {
    System.out.println();
  }

  public static String getHumanDuration(long milliseconds) {

    long minutes = (milliseconds / 1000) / 60;
    long seconds = (milliseconds / 1000) % 60;
    String secondsStr = Long.toString(seconds);
    String secs;
    if (secondsStr.length() >= 2) {
      secs = secondsStr.substring(0, 2);
    } else {
      secs = "0" + secondsStr;
    }

    return minutes == 0 && seconds == 0
        ? "less than a second"
        : minutes + " mins " + secs + " secs";
  }

  public static String getStructureMapName(String firstLine) {
    char quoteType = firstLine.substring(firstLine.lastIndexOf("=")).contains("\'") ? '\'' : '\"';
    return firstLine.substring(
        firstLine.lastIndexOf("= " + quoteType) + 3, firstLine.lastIndexOf(quoteType));
  }

  public static FCTFile readFile(String filePath) throws IOException {
    Path path = Paths.get(filePath);

    StringBuilder content = new StringBuilder();
    String line;
    String firstLine = "";
    int i = 0;
    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8); ) {

      while ((line = reader.readLine()) != null) {

        if (i == 0) {
          i++;
          firstLine = line;
        }

        content.append(line);
        content.append(System.lineSeparator());
      }
    }

    return new FCTFile(path.getFileName().toString(), content.toString(), firstLine);
  }

  public static void writeFile(String outputPath, String structureMapString) throws IOException {
    try (BufferedWriter writer =
        new BufferedWriter(new FileWriter(outputPath, StandardCharsets.UTF_8, false))) {
      writer.write(structureMapString);
    }
  }

  public static void writeJsonFile(String outputPath, String structureMapString)
      throws IOException {
    JsonElement je = JsonParser.parseString(structureMapString);
    String prettyJsonString = gson.toJson(je);
    writeFile(outputPath, prettyJsonString);
  }

  public static Properties readPropertiesFile(String filePath) {
    Properties properties = new Properties();
    try (InputStream input = new FileInputStream(filePath)) {

      properties.load(input);
      return properties;

    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return properties;
  }

  public static void printCompletedInDuration(long startTime) {
    FCTUtils.printToConsole(
        String.format(
            "\u001b[32mCompleted in %s \u001b[0m \n",
            FCTUtils.getHumanDuration(System.currentTimeMillis() - startTime)));
  }

  public static Map<String, Map<String, String>> indexConfigurationFiles(String inputDirectoryPath)
      throws IOException {
    Map<String, Map<String, String>> filesMap = new HashMap<>();
    Path rootDir = Paths.get(inputDirectoryPath);
    Files.walkFileTree(
        rootDir,
        new SimpleFileVisitor<>() {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (!Files.isDirectory(file)) {

              String parentDirKey =
                  file.getParent().equals(rootDir)
                      ? FCTValidationEngine.Constants.ROOT
                      : file.getParent().getFileName().toString();
              Map<String, String> fileList = filesMap.getOrDefault(parentDirKey, new HashMap<>());
              fileList.put(file.getFileName().toString(), file.toAbsolutePath().toString());
              filesMap.put(parentDirKey, fileList);
            }
            return FileVisitResult.CONTINUE;
          }
        });
    return filesMap;
  }

  public static final class Constants {
    public static final String HL7_FHIR_PACKAGE = "hl7.fhir.r4.core";
    public static final String HL7_FHIR_PACKAGE_VERSION = "4.0.1";
  }
}
