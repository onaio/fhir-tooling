/* (C)2023 */
package org.smartregister.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import com.ibm.icu.text.CharsetDetector;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.smartregister.domain.FctFile;
import org.smartregister.processor.FctValidationProcessor;

public class FctUtils {

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

  public static FctFile readFile(String filePath) throws IOException {
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

    return new FctFile(path.getFileName().toString(), content.toString(), firstLine);
  }

  public static void writeJsonFile(String outputPath, String fhirResourceAsString)
      throws IOException {
    try (BufferedWriter writer =
        new BufferedWriter(new FileWriter(outputPath, StandardCharsets.UTF_8, false))) {
      FhirContext.forR4()
          .newJsonParser()
          .setPrettyPrint(true)
          .encodeResourceToWriter(
              FhirContext.forR4().newJsonParser().parseResource(fhirResourceAsString), writer);
    }
  }

  public static Properties readPropertiesFile(String propertiesFilePath) throws IOException {

    CharsetDetector detector = new CharsetDetector();
    byte[] fileBytes = Files.readAllBytes(Path.of(propertiesFilePath));
    detector.setText(fileBytes);

    Properties properties = new Properties();
    try (InputStreamReader reader =
        new InputStreamReader(
            new ByteArrayInputStream(fileBytes), Charset.forName(detector.detect().getName()))) {
      properties.load(reader);
    }

    return properties;
  }

  public static void printCompletedInDuration(long startTime) {
    FctUtils.printToConsole(
        String.format(
            "\u001b[32mCompleted in %s \u001b[0m \n",
            FctUtils.getHumanDuration(System.currentTimeMillis() - startTime)));
  }

  public static Map<String, Map<String, String>> indexConfigurationFiles(
      String inputDirectoryPath, String... fileExtensions) throws IOException {
    Map<String, Map<String, String>> filesMap = new HashMap<>();
    Path rootDir = Paths.get(inputDirectoryPath);
    Files.walkFileTree(
        rootDir,
        new SimpleFileVisitor<>() {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (!Files.isDirectory(file)
                && (fileExtensions.length == 1 && "*".equals(fileExtensions[0])
                    || Arrays.asList(fileExtensions)
                        .contains(FilenameUtils.getExtension(file.getFileName().toString())))) {

              String parentDirKey =
                  file.getParent().equals(rootDir)
                      ? FctValidationProcessor.Constants.ROOT
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

  /**
   * @param resourcePath a string of the format Resource/id e.g. Patient/1234
   * @return 1234
   */
  public static String getResourceId(String resourcePath) {
    return StringUtils.isNotBlank(resourcePath) && resourcePath.contains("/")
        ? resourcePath.substring(resourcePath.lastIndexOf('/') + 1)
        : resourcePath;
  }

  public static <T extends IBaseResource> T getFhirResource(Class<T> t, String contentAsString) {
    IParser iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser();
    return iParser.parseResource(t, contentAsString);
  }

  public static void copyDirectoryContent(Path sourceDir, Path destinationDir) {
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

  public static void deleteDirectoryRecursively(Path dirPath) {

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

  public static final class Constants {
    public static final String HL7_FHIR_PACKAGE = "hl7.fhir.r4.core";
    public static final String HL7_FHIR_PACKAGE_VERSION = "4.0.1";
  }
}
