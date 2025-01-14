package org.smartregister.command;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

  @Test
  void testGenerateRandomDate() {
    LocalDate randomDate = QuestionnaireResponseGeneratorCommand.generateRandomDate();
    assertNotNull(randomDate);
    assertTrue(
        randomDate.isAfter(LocalDate.of(1959, 12, 31))
            && randomDate.isBefore(LocalDate.of(2024, 1, 1)));
  }

  @Test
  void testGenerateRandomDateTime() {
    LocalDateTime randomDateTime = QuestionnaireResponseGeneratorCommand.generateRandomDateTime();
    assertNotNull(randomDateTime);
    assertTrue(
        randomDateTime.isAfter(LocalDateTime.of(1999, 12, 31, 23, 59))
            && randomDateTime.isBefore(LocalDateTime.of(2025, 1, 1, 0, 0)));
  }

  @Test
  void testGenerateChoiceValue_validOption() {
    JSONArray questions = new JSONArray();
    JSONObject question = new JSONObject();
    question.put("type", "choice");
    question.put("linkId", "exampleLinkId");
    JSONArray answerOptions = new JSONArray();
    JSONObject option = new JSONObject();
    JSONObject coding = new JSONObject();
    coding.put("code", "12345");
    coding.put("display", "Option 1");
    option.put("valueCoding", coding);
    answerOptions.put(option);
    question.put("answerOption", answerOptions);
    questions.put(question);

    JSONObject result =
        QuestionnaireResponseGeneratorCommand.generateChoiceValue(questions, "exampleLinkId");
    assertNotNull(result);
    assertEquals("12345", result.getString("code"));
    assertEquals("Option 1", result.getString("display"));
  }

  @Test
  void testGenerateChoiceValue_noAnswerOptions() {
    JSONArray questions = new JSONArray();
    JSONObject question = new JSONObject();
    question.put("type", "choice");
    question.put("linkId", "exampleLinkId");
    questions.put(question);

    JSONObject result =
        QuestionnaireResponseGeneratorCommand.generateChoiceValue(questions, "exampleLinkId");
    assertNull(result);
  }

  @Test
  void testGenerateQuantityValue_withExtensions() {
    JSONArray questions = new JSONArray();
    JSONObject question = new JSONObject();
    question.put("type", "quantity");
    question.put("linkId", "exampleLinkId");

    JSONArray extensions = new JSONArray();
    JSONObject minValue = new JSONObject();
    minValue.put("url", "http://example.com/minValue");
    minValue.put("valueInteger", 50);
    JSONObject maxValue = new JSONObject();
    maxValue.put("url", "http://example.com/maxValue");
    maxValue.put("valueInteger", 500);
    extensions.put(minValue);
    extensions.put(maxValue);
    question.put("extension", extensions);

    questions.put(question);

    JSONObject result =
        QuestionnaireResponseGeneratorCommand.generateQuantityValue(questions, "exampleLinkId");
    assertNotNull(result);
    assertTrue(result.getInt("value") >= 50 && result.getInt("value") <= 500);
    assertEquals("cm", result.getString("unit"));
  }

  @Test
  void testGenerateReferenceValue() {
    JSONObject reference = QuestionnaireResponseGeneratorCommand.generateReferenceValue();
    assertNotNull(reference);
    assertTrue(
        reference
            .getString("reference")
            .matches("(Patient|Practitioner|Location|Immunization)/[a-f0-9-]{36}"));
  }

  @Test
  void testGenerateStringAnswer() {
    JSONObject extras = new JSONObject();
    JSONArray questions = new JSONArray();

    JSONObject answer =
        QuestionnaireResponseGeneratorCommand.generateAnswer("string", questions, "linkId", extras);

    assertNotNull(answer);
    assertTrue(answer.has("valueString"));
    assertTrue(answer.getString("valueString").startsWith("FakeString"));
  }

  @Test
  void testGenerateIntegerAnswer() {
    JSONObject extras = new JSONObject();
    JSONArray questions = new JSONArray();

    JSONObject answer =
        QuestionnaireResponseGeneratorCommand.generateAnswer(
            "integer", questions, "linkId", extras);

    assertNotNull(answer);
    assertTrue(answer.has("valueInteger"));
  }

  @Test
  void testGenerateBooleanAnswer() {
    JSONObject extras = new JSONObject();
    JSONArray questions = new JSONArray();

    JSONObject answer =
        QuestionnaireResponseGeneratorCommand.generateAnswer(
            "boolean", questions, "linkId", extras);

    assertNotNull(answer);
    assertTrue(answer.has("valueBoolean"));
    assertTrue(answer.get("valueBoolean") instanceof Boolean);
  }

  @Test
  void testGenerateDecimalAnswer() {
    JSONObject extras = new JSONObject();
    JSONArray questions = new JSONArray();

    JSONObject answer =
        QuestionnaireResponseGeneratorCommand.generateAnswer(
            "decimal", questions, "linkId", extras);

    assertNotNull(answer);
    assertTrue(answer.has("valueDecimal"));
    assertTrue(answer.get("valueDecimal") instanceof Double);
  }

  @Test
  void testGenerateDateAnswer() {
    JSONObject extras = new JSONObject();
    JSONArray questions = new JSONArray();

    JSONObject answer =
        QuestionnaireResponseGeneratorCommand.generateAnswer("date", questions, "linkId", extras);

    assertNotNull(answer);
    assertTrue(answer.has("valueDate"));
    assertTrue(answer.get("valueDate") instanceof String);
  }

  @Test
  void testGenerateDateTimeAnswer() {
    JSONObject extras = new JSONObject();
    JSONArray questions = new JSONArray();

    JSONObject answer =
        QuestionnaireResponseGeneratorCommand.generateAnswer(
            "datetime", questions, "linkId", extras);

    assertNotNull(answer);
    assertTrue(answer.has("valueDateTime"));
    assertTrue(answer.get("valueDateTime") instanceof String);
  }

  @Test
  void testGenerateQuantityAnswer() {
    JSONArray questions = new JSONArray();
    JSONObject question = new JSONObject();
    question.put("type", "quantity");
    question.put("linkId", "linkId");
    questions.put(question);

    JSONObject extras = new JSONObject();
    JSONObject answer =
        QuestionnaireResponseGeneratorCommand.generateAnswer(
            "quantity", questions, "linkId", extras);

    assertNotNull(answer);
    assertTrue(answer.has("valueQuantity"));
  }

  @Test
  void testGenerateTextAnswer() {
    JSONObject extras = new JSONObject();
    JSONArray questions = new JSONArray();

    JSONObject answer =
        QuestionnaireResponseGeneratorCommand.generateAnswer("text", questions, "linkId", extras);

    assertNotNull(answer);
    assertTrue(answer.has("valueString"));
    assertTrue(answer.getString("valueString").startsWith("This is a fake text"));
  }

  @Test
  void testGenerateReferenceAnswer() {
    JSONObject extras = new JSONObject();
    JSONArray questions = new JSONArray();

    JSONObject answer =
        QuestionnaireResponseGeneratorCommand.generateAnswer(
            "reference", questions, "linkId", extras);

    assertNotNull(answer);
    assertTrue(answer.has("valueReference"));
  }

  @Test
  void testGenerateDefaultAnswer() {
    JSONObject extras = new JSONObject();
    JSONArray questions = new JSONArray();

    JSONObject answer =
        QuestionnaireResponseGeneratorCommand.generateAnswer(
            "unsupported", questions, "linkId", extras);

    assertNotNull(answer);
    assertTrue(answer.isEmpty());
  }
}
