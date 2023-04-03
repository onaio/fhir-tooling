/* (C)2023 */
package org.smartregister.util;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.domain.FCTFile;

public class FCTValidationEngine {
  private Map<String, Set<String>> compositionReferencedResources = new HashMap<>();
  private Map<String, Properties> translationsMap = new HashMap<>();
  private Map<String, Map<String, Set<String>>> errorsMap = new HashMap<>();

  private JSONObject currentParamDataWorkflowJSONObject;
  private JSONObject previousParentJSONObject;
  private JSONObject parentJSONObject;
  private String parentJSONObjectKey;
  private String currentFile;
  private int configurationFilesCount;
  private Set<String> factMapKeys = new HashSet<>();
  private Map<String, String> fileConfigTypeIdentifierToFilenameMap = new HashMap<>();

  private void handleValue(String key, Object value, boolean isComposition) {
    if (value instanceof JSONArray) {

      if (Constants.base.equals(key) || Constants.resourcesToSync.equals(key))
        validateDuplicates(((JSONArray) value).toList(), key);

      handleJSONArray(key, (JSONArray) value, isComposition);

    } else if (value instanceof JSONObject) {

      parentJSONObjectKey = key;
      previousParentJSONObject = parentJSONObject;
      parentJSONObject = (JSONObject) value;

      handleJSONObject((JSONObject) value, isComposition);

    } else {

      if (isComposition
          && Constants.REFERENCE.equals(key)
          && value != null
          && !value.toString().isEmpty()) {
        String resourceKey = StringUtils.substringBefore(value.toString(), Constants.SEPARATOR);
        Set<String> resourceList =
            compositionReferencedResources.getOrDefault(resourceKey, new HashSet<>());
        resourceList.add(StringUtils.substringAfter(value.toString(), Constants.SEPARATOR));
        compositionReferencedResources.put(resourceKey, resourceList);

      } else if (!isComposition) { // Access to values of all arrays and objects

        if (Constants.REFERENCE.equals(key) && key.contains(Constants.SEPARATOR)) {

          String[] resourceIdPair =
              value.toString().split(Constants.SEPARATOR); // Questionnaire/some-id-here

          validateCompositionResource(resourceIdPair[0], resourceIdPair[1]);

        } else {

          if (Constants.questionnaire.equals(parentJSONObjectKey)) {

            if (Constants.ID.equals(key) && !value.toString().contains("{")) {

              validateCompositionResource("Questionnaire", value.toString());

            } else if (Constants.planDefinitions.equals(key)) {

              validateCompositionResource("PlanDefinition", value.toString());
            }
          }

          if (Constants.translatables.contains(key) && StringUtils.isNotBlank(value.toString())) {

            if (!value.toString().contains("{")) {

              addToErrorMap(
                  Constants.Translations,
                  String.format(
                      "i18N :: \u001b[36m%s\u001b[0m for key \u001b[36m%s\u001b[0m should be in a .properties file",
                      value, key));

            } else if (value.toString().contains("{{")) {

              String translationKey =
                  StringUtils.substringBetween(value.toString(), "{{", "}}").trim();

              if (translationsMap.containsKey(Constants.DEFAULT_LANGUAGE_RESOURCE_FILE)
                  && !translationsMap
                      .get(Constants.DEFAULT_LANGUAGE_RESOURCE_FILE)
                      .containsKey(translationKey)) {

                addToErrorMap(
                    Constants.Translations,
                    String.format(
                        "i18N :: \u001b[35m%s\u001b[0m for key \u001b[36m%s\u001b[0m is not present in the default .properties file",
                        value, key));
              }
            }
          }

          if (value.toString().contains("data.put(")) {
            String ruleFactKey = StringUtils.substringBetween(value.toString(), "'", "'");
            factMapKeys.add(ruleFactKey);
            if (errorsMap.getOrDefault(currentFile, new HashMap<>()).containsKey(Constants.Rules))
              errorsMap.get(currentFile).get(Constants.Rules).remove(ruleFactKey);
          } else if (value.toString().contains("@{")) {
            String ruleFactKey = StringUtils.substringBetween(value.toString(), "@{", "}");
            if (!factMapKeys.contains(ruleFactKey)) {
              addToErrorMap(Constants.Rules, ruleFactKey);
            }
          }

          if (Constants.PARAMDATA.equals(value)) {

            if (previousParentJSONObject != null
                && previousParentJSONObject.has(Constants.workflow))
              currentParamDataWorkflowJSONObject = previousParentJSONObject;

            String configFileIdentifier =
                currentParamDataWorkflowJSONObject.getString(Constants.ID);

            // Pop any pre-persisted errors
            errorsMap
                .getOrDefault(
                    fileConfigTypeIdentifierToFilenameMap.getOrDefault(configFileIdentifier, ""),
                    new HashMap<>())
                .getOrDefault(Constants.Rules, new HashSet<>())
                .remove(parentJSONObject.getString(Constants.KEY));

            // Add to fact-map for this file
            if (currentFile.equals(
                fileConfigTypeIdentifierToFilenameMap.getOrDefault(configFileIdentifier, null)))
              factMapKeys.add(parentJSONObject.getString(Constants.KEY));
          }
        }
      }
    }
  }

  private void validateDuplicates(List<Object> currentArrayList, String keyToValidate) {

    Set<Object> duplicates =
        currentArrayList.stream()
            .filter(entry -> Collections.frequency(currentArrayList, entry) > 1)
            .collect(Collectors.toSet());

    if (duplicates.size() > 0)
      addToErrorMap(
          "Duplicate",
          "\u001b[33mDUPS\u001b[0m :: "
              + duplicates.size()
              + " duplicate \u001b[36m"
              + keyToValidate
              + "\u001b[0m resource"
              + (duplicates.size() > 1 ? "s" : "")
              + " found - \u001b[36m"
              + StringUtils.join(duplicates, ", ")
              + "\u001b[0m");
  }

  private void validateCompositionResource(String fhirResource, String id) {
    if (!compositionReferencedResources.getOrDefault(fhirResource, new HashSet<>()).contains(id)) {

      addToErrorMap(
          fhirResource,
          String.format(
              "\u001b[32mCONF\u001b[0m :: \u001b[36m%s\u001b[0m id \u001b[36m%s\u001b[0m not in composition file",
              fhirResource, id));
    }
  }

  private void addToErrorMap(String resource, String errorMessageOrContent) {

    Map<String, Set<String>> innerErrorsMap = errorsMap.getOrDefault(currentFile, new HashMap<>());
    Set<String> errorList = innerErrorsMap.getOrDefault(resource, new HashSet<>());
    errorList.add(errorMessageOrContent);
    innerErrorsMap.put(resource, errorList);

    errorsMap.put(currentFile, innerErrorsMap);
  }

  private void handleJSONObject(JSONObject jsonObject, boolean isComposition) {
    Iterator<String> jsonObjectIterator = jsonObject.keys();
    jsonObjectIterator.forEachRemaining(
        key -> {
          Object value = jsonObject.get(key);
          handleValue(key, value, isComposition);
        });

    parentJSONObjectKey = null;
  }

  private void handleJSONArray(String key, JSONArray jsonArray, boolean isComposition) {
    Iterator<Object> jsonArrayIterator = jsonArray.iterator();
    jsonArrayIterator.forEachRemaining(element -> handleValue(key, element, isComposition));
  }

  private Map<String, Map<String, String>> indexConfigurationFiles(String inputDirectoryPath)
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

  public void process(String compositionPath, String directoryPath) throws IOException {
    FCTUtils.printToConsole("Processing starting... \uD83D\uDE80");

    long startTime = System.currentTimeMillis();
    Map<String, Map<String, String>> configDirIndexMap = indexConfigurationFiles(directoryPath);

    // Load Composition
    currentFile = compositionPath;
    FCTFile compositionFile = FCTUtils.readFile(compositionPath);
    handleJSONObject(new JSONObject(compositionFile.getContent()), true);

    // Process other configurations
    for (var entry : configDirIndexMap.entrySet()) {

      Map<String, String> fileIndexMap = configDirIndexMap.get(entry.getKey());

      for (var nestedEntry : fileIndexMap.entrySet()) {

        if (nestedEntry.getKey().endsWith(".properties")) {

          // Translations
          Properties properties = FCTUtils.readPropertiesFile(nestedEntry.getValue());
          translationsMap.put(nestedEntry.getKey(), properties);

        } else if (nestedEntry.getKey().endsWith(".json")) {
          resetStatePerFile();
          // Configurations
          configurationFilesCount++;
          currentFile = nestedEntry.getValue();
          FCTFile configFile = FCTUtils.readFile(nestedEntry.getValue());
          JSONObject fileJSONObject = new JSONObject(configFile.getContent());
          if (fileJSONObject.has("configType") && fileJSONObject.has(Constants.ID))
            fileConfigTypeIdentifierToFilenameMap.put(
                fileJSONObject.getString(Constants.ID), currentFile);
          handleJSONObject(
              fileJSONObject, Paths.get(compositionPath).equals(Paths.get(nestedEntry.getValue())));

        } else {
          FCTUtils.printWarning("Unrecognized Config File Format");
        }
      }
    }

    FCTUtils.printToConsole(String.format("%d translation files found", translationsMap.size()));
    FCTUtils.printToConsole(String.format("%d configuration files found", configurationFilesCount));
    printValidationResults();

    FCTUtils.printNewLine();
    FCTUtils.printCompletedInDuration(startTime);
  }

  private void resetStatePerFile() {
    currentFile = null;
    factMapKeys.clear();
  }

  private void printValidationResults() {

    if (!translationsMap.containsKey(Constants.DEFAULT_LANGUAGE_RESOURCE_FILE)) {
      FCTUtils.printError("i18N :: Default translations file strings_config.json is missing");
    }

    Map<String, Integer> errorsMapCount = new HashMap<>();

    for (var entry : errorsMap.entrySet()) {

      FCTUtils.printNewLine();
      FCTUtils.printInfo(String.format("\u001b[35m%s\u001b[0m", entry.getKey()));

      for (var innerEntry : entry.getValue().entrySet()) {

        Set<String> errorList = innerEntry.getValue();

        int errorCountByType = errorsMapCount.getOrDefault(innerEntry.getKey(), 0);
        errorsMapCount.put(
            innerEntry.getKey(),
            errorCountByType + entry.getValue().get(innerEntry.getKey()).size());

        for (String errorMessage : errorList) {

          if (innerEntry.getKey().equals(Constants.Translations) && !errorMessage.contains("{{")) {

            FCTUtils.printWarning(errorMessage);

          } else if (innerEntry.getKey().equals(Constants.Rules)) {

            FCTUtils.printError(
                String.format(
                    "\u001b[35;1mRULE\u001b[0m :: \u001b[36m%s\u001b[0m fact is missing",
                    errorMessage));

          } else FCTUtils.printError(errorMessage);
        }
      }
    }

    // Validation summary
    StringBuilder errorMessageBuilder =
        new StringBuilder(
                String.format(
                    "%d out of %d files with errors", errorsMap.size(), configurationFilesCount))
            .append("\n\n\u001b[32mVALIDATION SUMMARY\u001b[0m \n----------------");

    for (var entry : errorsMapCount.entrySet()) {

      errorMessageBuilder
          .append('\n')
          .append(entry.getKey())
          .append(" errors - ")
          .append(entry.getValue());
    }
    FCTUtils.printNewLine();
    FCTUtils.printToConsole(errorMessageBuilder.toString());

    if (errorsMap.size() == 0) {
      FCTUtils.printToConsole(
          "\u001b[33mAs far as we can tell\u001b[0m \u001b[35myour configs\u001b[0m are flawless \uD83D\uDC9A ...\n");
    }
  }

  public static final class Constants {
    public static final String ROOT = "root";
    public static final String ID = "id";
    public static final String base = "base";
    public static final String resourcesToSync = "resourcesToSync";
    public static final String planDefinitions = "planDefinitions";
    public static final String questionnaire = "questionnaire";
    public static final String REFERENCE = "reference";
    public static final String Translations = "Translations";
    public static final String Rules = "Rules";
    public static final String DEFAULT_LANGUAGE_RESOURCE_FILE = "strings_config.properties";
    public static final String PARAMDATA = "PARAMDATA";
    public static final String workflow = "workflow";
    public static final String KEY = "key";
    public static final Set translatables =
        ImmutableSet.of(
            "saveButtonText", "title", "display", "actionButtonText", "message"); // , "description"
    public static final String SEPARATOR = "/";
  }
}
