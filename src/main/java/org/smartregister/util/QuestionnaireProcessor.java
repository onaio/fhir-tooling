/* (C)2023 */
package org.smartregister.util;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.*;
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
                questionnaireJSONObject.has("extension")
                    ? getStructureMapId(questionnaireJSONObject.getJSONArray("extension"))
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

            FCTUtils.printError(String.format("Error processing file %s", currentFile));
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
          "Questionnaire DOES NOT have an id field. Are we expecting it to be generated on the Server?");
    } else {
      FCTUtils.printError(String.format("%s", message));
    }
  }

  private String getStructureMapId(JSONArray extensionJSONArray) {

    for (int i = 0; i < extensionJSONArray.length(); i++) {

      if ("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap"
          .equals(extensionJSONArray.getJSONObject(i).getString("url"))) {

        if (extensionJSONArray.getJSONObject(i).has("valueCanonical")) {
          return StringUtils.substringAfterLast(
              extensionJSONArray.getJSONObject(i).optString("valueCanonical").trim(), "/");
        } else if (extensionJSONArray.getJSONObject(i).has("valueReference")) {

          JSONObject valueReferenceJSONObject =
              extensionJSONArray.getJSONObject(i).getJSONObject("valueReference");
          return StringUtils.substringAfterLast(
              valueReferenceJSONObject.optString("reference").trim(), "/");
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
}
