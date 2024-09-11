package org.smartregister.command;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class QuestionnaireResponseGeneratorCommandTest {

  @Test
  void testGenerateWithFaker() {
    String category = "name";
    String method = "firstName";
    Object result = QuestionnaireResponseGeneratorCommand.generateWithFaker(category, method);
    assertNotNull(result);
    assertTrue(result instanceof String);
  }

  @Test
  void testGenerateWithFaker_invalidCategory() {
    String category = "invalidCategory";
    String method = "firstName";

    RuntimeException thrown =
        assertThrows(
            RuntimeException.class,
            () -> QuestionnaireResponseGeneratorCommand.generateWithFaker(category, method));
    assertEquals("Failed to generate fake data", thrown.getMessage());
  }

  @Test
  void testGenerateAnswer() {
    JSONObject extras = new JSONObject();
    JSONObject fakerConfig = new JSONObject();
    fakerConfig.put("category", "name");
    fakerConfig.put("method", "firstName");
    extras.put("exampleLinkId", fakerConfig);

    JSONArray questions = new JSONArray();
    JSONObject question = new JSONObject();
    question.put("type", "string");
    question.put("linkId", "exampleLinkId");
    questions.put(question);

    JSONObject result =
        QuestionnaireResponseGeneratorCommand.generateAnswer(
            "string", questions, "exampleLinkId", extras);

    assertNotNull(result);
    assertTrue(result.has("valueString"));
    assertFalse(result.getString("valueString").isEmpty());
    assertFalse(result.getString("valueString").startsWith("FakeString"));
  }

  @Test
  void testGenerateAnswer_noExtras() {
    JSONArray questions = new JSONArray();
    JSONObject question = new JSONObject();
    question.put("type", "integer");
    question.put("linkId", "exampleLinkId");
    questions.put(question);

    JSONObject extras = new JSONObject();
    JSONObject result =
        QuestionnaireResponseGeneratorCommand.generateAnswer(
            "integer", questions, "exampleLinkId", extras);

    assertNotNull(result);
    assertTrue(result.has("valueInteger"));
  }
}
