/* (C)2023 */
package org.smartregister.util;

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

public class StructureMapProcessor {
  private String directoryPath;
  private String currentFile;

  private Map<String, Map<String, Set<String>>> exceptionsMap = new HashMap<>();

  public StructureMapProcessor(String folderPath) {
    this.directoryPath = folderPath;
  }

  public Map<String, Set<String>> process() {
    Map<String, Set<String>> structureMapToLinkIds = new HashMap<>();

    try {

      Map<String, Map<String, String>> folderTofilesIndexMap =
          FCTUtils.indexConfigurationFiles(directoryPath);

      // Process other configurations
      for (var entry : folderTofilesIndexMap.entrySet()) {

        Map<String, String> fileIndexMap = folderTofilesIndexMap.get(entry.getKey());

        for (var nestedEntry : fileIndexMap.entrySet()) {

          currentFile = nestedEntry.getValue();

          if (nestedEntry.getKey().startsWith(".")) continue;

          Path path = Paths.get(nestedEntry.getValue());
          Set<String> linkIds = new HashSet<>();

          String line;
          String firstLine = "";
          int i = 0;
          try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8); ) {

            while ((line = reader.readLine()) != null) {

              if (i == 0) {
                i++;
                firstLine = line;
              }

              if (line.contains(FCTValidationEngine.Constants.linkId)) {

                String[] linkIdRaw =
                    StringUtils.deleteWhitespace(line.toString())
                        .split(FCTValidationEngine.Constants.linkId);
                for (int j = 0; j < linkIdRaw.length; j++) {

                  String subLine =
                      StringUtils.deleteWhitespace(line.toString())
                          .split(FCTValidationEngine.Constants.linkId)[j];
                  if (subLine.startsWith("=")) {

                    try {
                      linkIds.add(getSubstringBetween(subLine, "='", "'"));

                    } catch (StringIndexOutOfBoundsException e) {

                      linkIds.add(getSubstringBetween(subLine, "=\"", "\""));
                    }
                  }
                }
              }
            }
          }

          structureMapToLinkIds.put(getStructureMapId(firstLine), linkIds);
        }
      }

    } catch (IOException ioException) {
      ioException.toString();
    }
    return structureMapToLinkIds;
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
