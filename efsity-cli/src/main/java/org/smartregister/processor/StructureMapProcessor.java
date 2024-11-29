/* (C)2023 */
package org.smartregister.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.util.FctUtils;

public class StructureMapProcessor {
  private String directoryPath;
  private String currentFile;

  public StructureMapProcessor(String folderPath) {
    this.directoryPath = folderPath;
  }

  public Map<String, Set<String>> process() {
    Map<String, Set<String>> structureMapToLinkIds = new HashMap<>();

    try {

      Map<String, Map<String, String>> folderTofilesIndexMap =
          FctUtils.indexConfigurationFiles(directoryPath, "map", "txt");

      // Process other configurations
      for (Map.Entry<String, Map<String, String>> entry : folderTofilesIndexMap.entrySet()) {

        Map<String, String> fileIndexMap = folderTofilesIndexMap.get(entry.getKey());

        for (Map.Entry<String, String> nestedEntry : fileIndexMap.entrySet()) {

          currentFile = nestedEntry.getValue();

          if (nestedEntry.getKey().startsWith(".")) continue;

          Path path = Paths.get(nestedEntry.getValue());
          Set<String> linkIds = new HashSet<>();

          String line;
          String firstLine = "";
          int lineNumber = 0;
          try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8); ) {

            while ((line = reader.readLine()) != null) {

              if (lineNumber == 0) {
                firstLine = line;
              }
              lineNumber++;

              if (line.contains(FctValidationProcessor.Constants.linkId)) {

                String[] linkIdRaw =
                    StringUtils.deleteWhitespace(line.toString())
                        .split(FctValidationProcessor.Constants.linkId);
                for (int j = 0; j < linkIdRaw.length; j++) {

                  String subLine =
                      StringUtils.deleteWhitespace(line.toString())
                          .split(FctValidationProcessor.Constants.linkId)[j];
                  if (subLine.startsWith("=")) {

                    try {
                      linkIds.add(getSubstringBetween(subLine, "='", "'"));

                    } catch (StringIndexOutOfBoundsException e) {

                      try {
                        linkIds.add(getSubstringBetween(subLine, "=\"", "\""));

                      } catch (StringIndexOutOfBoundsException innerException) {
                        FctUtils.printWarning(
                            String.format(
                                "Parsing failed for link id value at \u001b[36m'linkId%s'\u001b[0m line %d, file \u001b[35;1m%s\u001b[0m. Could it be a dynamic link id?",
                                subLine, lineNumber, currentFile));
                      }
                    }
                  }
                }
              }
            }
          }

          if (!firstLine.isBlank())
            structureMapToLinkIds.put(getStructureMapId(firstLine), linkIds);
        }
      }

    } catch (IOException ioException) {
      ioException.toString();
    }
    return structureMapToLinkIds;
  }

  public Map<String, String> generateIdToFilepathMap() {
    // Map to hold StructureMap ID to file name mapping
    Map<String, String> structureMapToFilename = new HashMap<>();

    try {
      // Index the files in the specified directory with "map" and "txt" extensions
      Map<String, Map<String, String>> folderToFilesIndexMap =
          FctUtils.indexConfigurationFiles(directoryPath, "map", "txt");

      // Iterate through the indexed files
      for (Map.Entry<String, Map<String, String>> entry : folderToFilesIndexMap.entrySet()) {

        Map<String, String> fileIndexMap = folderToFilesIndexMap.get(entry.getKey());

        for (Map.Entry<String, String> nestedEntry : fileIndexMap.entrySet()) {

          // Get the full path to the current file
          currentFile = nestedEntry.getValue();

          // Skip hidden or invalid files
          if (nestedEntry.getKey().startsWith(".")) continue;

          Path path = Paths.get(nestedEntry.getValue());
          String firstLine;

          // Read the first line of the file to extract the StructureMap ID
          try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            firstLine = reader.readLine();
          }

          // If the first line is not blank, get the StructureMap ID and map it to the file name
          if (StringUtils.isNotBlank(firstLine)) {
            String structureMapId = getStructureMapId(firstLine);

            // Extract the actual file name (e.g., child.map)
            String actualFileName = path.getFileName().toString();

            if (actualFileName != null) {

              // Map the StructureMap ID to the file name
              structureMapToFilename.put(structureMapId, actualFileName);
            }
            // Map the StructureMap ID to the file path
            structureMapToFilename.put(structureMapId, currentFile);
          }
        }
      }

    } catch (IOException ioException) {
      ioException.printStackTrace(); // Print the exception for debugging purposes
    }

    // Return the map of StructureMap IDs to their corresponding file names
    return structureMapToFilename;
  }

  private String getSubstringBetween(String str, String opening, String closing) {
    String temp = str.substring(str.indexOf(opening) + opening.length());
    return temp.substring(0, temp.indexOf(closing));
  }

  private String getStructureMapId(String firstLine) {

    return StringUtils.substringAfterLast(
        firstLine.replace("\'", "").replace("\"", "").split("=")[0].trim(), '/');
  }
}
