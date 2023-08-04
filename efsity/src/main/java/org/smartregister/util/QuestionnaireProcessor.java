/* (C)2023 */
package org.smartregister.util;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.FCTFile;

public class QuestionnaireProcessor {
  private String directoryPath;
  private String currentFile;
  private String currentQuestionnaireId;

  private String currentStructureMapId;

  private Map<String, Set<String>> questionnairesToLinkIds = new HashMap<>();
  private Map<String, Set<String>> questionnairesToStructureMapIds = new HashMap<>();

  private Map<String, Map<String, Set<String>>> resultsMap = new HashMap<>();

  public QuestionnaireProcessor(String folderPath) {
    this.directoryPath = folderPath;
  }

  public Map<String, Map<String, Set<String>>> process() {

    try {

      Map<String, Map<String, String>> folderTofilesIndexMap =
          FCTUtils.indexConfigurationFiles(directoryPath, "json");

      // Process other configurations
      for (var entry : folderTofilesIndexMap.entrySet()) {

        Map<String, String> fileIndexMap = folderTofilesIndexMap.get(entry.getKey());

        for (var nestedEntry : fileIndexMap.entrySet()) {

          currentFile = nestedEntry.getValue();

          if (nestedEntry.getKey().startsWith(".")) continue;

          FCTFile file = FCTUtils.readFile(nestedEntry.getValue());

          try {

            JSONObject questionnaireJSONObject = new JSONObject(file.getContent());
            currentQuestionnaireId =
                questionnaireJSONObject.getString(FCTValidationEngine.Constants.ID);
            currentStructureMapId =
                questionnaireJSONObject.has(Constants.EXTENSION)
                    ? getStructureMapId(questionnaireJSONObject.getJSONArray(Constants.EXTENSION))
                    : null;

            questionnairesToStructureMapIds.put(
                currentQuestionnaireId,
                currentStructureMapId != null
                    ? Sets.newHashSet(currentStructureMapId)
                    : Sets.newHashSet());
            resultsMap.put(
                FCTValidationEngine.Constants.structuremap, questionnairesToStructureMapIds);

            handleJSONObject(questionnaireJSONObject, true);

          } catch (JSONException jsonException) {

            FCTUtils.printInfo(String.format("\u001b[35;1m%s\u001b[0m", currentFile));
            printJsonExceptionMessages(jsonException.getMessage());
          }
        }
      }

    } catch (IOException ioException) {
      ioException.toString();
    }

    resultsMap.put(FCTValidationEngine.Constants.questionnaire, questionnairesToLinkIds);
    return resultsMap;
  }

  private void printJsonExceptionMessages(String message) {
    if (message.contains("JSONObject[\"id\"] not found")) {
      FCTUtils.printWarning(
          "Questionnaire DOES NOT have an id field. Are we expecting it to be generated on the server?");
    } else {
      FCTUtils.printError(String.format("%s", message));
    }
  }

  private String getStructureMapId(JSONArray extensionJSONArray) {

    for (int i = 0; i < extensionJSONArray.length(); i++) {

      if (Constants.STRUCTURE_DEFINITION_TARGET_STRUCTURE_MAP.equals(
          extensionJSONArray.getJSONObject(i).getString(Constants.URL))) {

        if (extensionJSONArray.getJSONObject(i).has(Constants.VALUE_CANONICAL)) {
          return StringUtils.substringAfterLast(
              extensionJSONArray.getJSONObject(i).optString(Constants.VALUE_CANONICAL).trim(), "/");
        } else if (extensionJSONArray.getJSONObject(i).has(Constants.VALUE_REFERENCE)) {

          JSONObject valueReferenceJSONObject =
              extensionJSONArray.getJSONObject(i).getJSONObject(Constants.VALUE_REFERENCE);
          return StringUtils.substringAfterLast(
              valueReferenceJSONObject.optString(Constants.REFERENCE).trim(), "/");
        } else {

          FCTUtils.printError("Structure Map value format not supported");
        }
      }
    }

    return null;
  }

  private void handleValue(String key, Object value, boolean isComposition) {
    if (value instanceof JSONArray) {

      handleJSONArray(key, (JSONArray) value, isComposition);

    } else if (value instanceof JSONObject) {

      handleJSONObject((JSONObject) value, isComposition);

    } else {

      if (FCTValidationEngine.Constants.linkId.equals(key)) {

        Set<String> results =
            questionnairesToLinkIds.getOrDefault(currentQuestionnaireId, new HashSet<>());
        results.add(value.toString());
        questionnairesToLinkIds.put(currentQuestionnaireId, results);
      }
    }
  }

  private void handleJSONObject(JSONObject jsonObject, boolean isComposition) {
    Iterator<String> jsonObjectIterator = jsonObject.keys();
    jsonObjectIterator.forEachRemaining(
        key -> {
          Object value = jsonObject.get(key);
          handleValue(key, value, isComposition);
        });
  }

  private void handleJSONArray(String key, JSONArray jsonArray, boolean isComposition) {
    Iterator<Object> jsonArrayIterator = jsonArray.iterator();
    jsonArrayIterator.forEachRemaining(element -> handleValue(key, element, isComposition));
  }

  public static final class Constants {
    public static final String VALUE_CANONICAL = "valueCanonical";
    public static final String VALUE_REFERENCE = "valueReference";
    public static final String REFERENCE = "reference";
    public static final String EXTENSION = "extension";
    public static final String URL = "url";
    public static final String STRUCTURE_DEFINITION_TARGET_STRUCTURE_MAP =
        "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap";
  }
}
