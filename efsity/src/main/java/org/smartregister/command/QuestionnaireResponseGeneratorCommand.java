package org.smartregister.command;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.domain.FctFile;
import org.smartregister.util.FctUtils;
import org.smartregister.util.HttpClient;
import picocli.CommandLine;

@CommandLine.Command(name = "generateResponse")
public class QuestionnaireResponseGeneratorCommand implements Runnable {
  private static final Logger logger =
      Logger.getLogger(QuestionnaireResponseGeneratorCommand.class.getCanonicalName());

  @CommandLine.Option(
      names = {"-i", "--input"},
      description = "input file path",
      required = true)
  private String inputFilePath;

  @CommandLine.Option(
      names = {"-gm", "--generation-mode"},
      description = "The generation mode to be used",
      defaultValue = "populate",
      required = false)
  private String mode;

  @CommandLine.Option(
      names = {"-fs", "--fhir-server"},
      description = "fhir server base url",
      required = false)
  private String fhir_base_url;

  @CommandLine.Option(
      names = {"-k", "--apiKey"},
      description = "api key",
      required = false)
  private String apiKey;

  @CommandLine.Option(
      names = {"-m", "--model"},
      description = "AI model",
      defaultValue = "gpt-3.5-turbo-16k")
  private String aiModel;

  @CommandLine.Option(
      names = {"-t", "--tokens"},
      description = "max tokens",
      defaultValue = "9000")
  private String maxTokens;

  @CommandLine.Option(
      names = {"-o", "--output"},
      description = "(Optional) output path, can be file or a directory",
      defaultValue = ".")
  private String outputFilePath;

  private static Random random = new Random();

  @Override
  public void run() {
    if (Objects.equals(mode, "populate")) {
      if (fhir_base_url == null) {
        throw new IllegalArgumentException(
            "The FHIR server url is required when using the 'populate' mode");
      }
    } else if (Objects.equals(mode, "ai")) {
      if (apiKey == null) {
        throw new IllegalArgumentException("The API key is required when using the 'ai' mode");
      }
    } else {
      throw new IllegalArgumentException("Invalid generation mode");
    }
    if (inputFilePath != null) {
      try {
        generateResponse(inputFilePath, mode, fhir_base_url, apiKey);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void generateResponse(
      String inputFilePath, String mode, String fhir_base_url, String apiKey) throws IOException {
    long start = System.currentTimeMillis();

    FctUtils.printInfo("Starting FHIR questionnaireResponse generation");
    FctUtils.printInfo(String.format("Input file path \u001b[35m%s\u001b[0m", inputFilePath));

    // Get questionnaire
    FctUtils.printInfo("Reading your questionnaire");
    FctFile inputFile = FctUtils.readFile(inputFilePath);
    String questionnaireData = inputFile.getContent();
    String questionnaireResponseString;

    if (Objects.equals(mode, "populate")) {
      questionnaireResponseString = populateMode(questionnaireData, fhir_base_url);
    } else {
      questionnaireResponseString = aiMode(questionnaireData, apiKey);
    }

    // Write response to file
    FctUtils.printInfo("Writing response to file");
    String output =
        Files.isDirectory(Paths.get(this.outputFilePath))
            ? this.outputFilePath
                + File.separator
                + inputFile.getNameWithoutExtension()
                + Constants.QUESTIONNAIRE_RESPONSE_SUFFIX
            : this.outputFilePath;

    FctUtils.writeJsonFile(output, questionnaireResponseString);
    FctUtils.printInfo(
        String.format(
            "Result Questionnaire Response generated and saved to path\u001b[36m %s \u001b[0m",
            output));

    FctUtils.printCompletedInDuration(start);
  }

  Boolean checkResource(
      String questionnaire_id, String resourceType, JSONObject resource, String fhir_base_url)
      throws IOException {
    JSONObject request = new JSONObject();
    request.put("method", "PUT");
    request.put("url", resourceType + "/" + questionnaire_id);

    JSONObject object = new JSONObject();
    object.put("resource", resource);
    object.put("request", request);

    JSONObject bundle = new JSONObject();
    bundle.put("resourceType", "Bundle");
    bundle.put("type", "transaction");
    bundle.put("entry", object);

    FctUtils.printToConsole("Checking resource: " + resourceType + "/" + questionnaire_id);
    List<String> result = HttpClient.postRequest(bundle.toString(), fhir_base_url, null);
    List<String> valid_codes = Arrays.asList("200", "201");
    return valid_codes.contains(result.get(0));
  }

  public static LocalDate generateRandomDate() {
    LocalDate startDate = LocalDate.of(1960, 1, 1);
    LocalDate endDate = LocalDate.of(2023, 12, 31);
    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
    long randomDays = new Random().nextLong(daysBetween + 1);
    return startDate.plusDays(randomDays);
  }

  public static LocalDateTime generateRandomDateTime() {
    LocalDateTime startDateTime = LocalDateTime.of(2000, 1, 1, 0, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2024, 12, 31, 23, 59);
    long secondsBetween = ChronoUnit.SECONDS.between(startDateTime, endDateTime);
    long randomSeconds = new Random().nextLong(secondsBetween + 1);
    return startDateTime.plusSeconds(randomSeconds);
  }

  private static JSONObject generateAnswer(String type, JSONArray questions, String link_id) {
    JSONObject answer = new JSONObject();
    switch (type.toLowerCase()) {
      case "string":
        return answer.put("valueString", "FakeString" + random.nextInt(100));
      case "integer":
        return answer.put("valueInteger", random.nextInt(100));
      case "boolean":
        return answer.put("valueBoolean", random.nextBoolean());
      case "decimal":
        return answer.put("valueDecimal", random.nextDouble());
      case "date":
        return answer.put("valueDate", String.valueOf(generateRandomDate()));
      case "choice":
        for (int i = 0; i < questions.length(); i++) {
          JSONObject current_object = questions.getJSONObject(i);
          String current_id = current_object.getString("linkId");
          if (Objects.equals(current_id, link_id)) {
            if (current_object.has("answerOption")) {
              JSONArray answer_options = current_object.getJSONArray("answerOption");
              int random_index = random.nextInt(answer_options.length());
              JSONObject random_value =
                  answer_options.getJSONObject(random_index).getJSONObject("valueCoding");
              return answer.put("valueCoding", random_value);
            }
          }
        }
      case "quantity":
        int minValue = 0;
        int maxValue = 1000;
        String unit = "cm";
        for (int i = 0; i < questions.length(); i++) {
          JSONObject current_object = questions.getJSONObject(i);
          String current_id = current_object.getString("linkId");
          if (Objects.equals(current_id, link_id)) {
            if (current_object.has("extension")) {
              JSONArray extension = current_object.getJSONArray("extension");
              for (int j = 0; j < extension.length(); j++) {
                JSONObject curr = extension.getJSONObject(j);
                String url = curr.getString("url");
                if (url.contains("minValue")) {
                  minValue = curr.getInt("valueInteger");
                } else if (url.contains("maxValue")) {
                  maxValue = curr.getInt("valueInteger");
                } else if (url.contains("unit")) {
                  unit = curr.getJSONObject("valueCoding").getString("display");
                }
              }
            }
          }
        }
        JSONObject quantityAnswer = new JSONObject();
        quantityAnswer.put("value", random.nextInt(minValue, maxValue));
        quantityAnswer.put("unit", unit);
        quantityAnswer.put("system", "http://unitsofmeasure.org");
        quantityAnswer.put("code", unit);
        return answer.put("valueQuantity", quantityAnswer);
      case "datetime":
        return answer.put("valueDateTime", String.valueOf(generateRandomDateTime()));
      case "text":
        return answer.put("valueString", "This is a fake text" + random.nextInt(100));
      case "reference":
        List<String> exampleResourceTypes =
            Arrays.asList("Patient", "Practitioner", "Location", "Immunization");
        int randomPick = random.nextInt(0, exampleResourceTypes.size());
        JSONObject reference = new JSONObject();
        reference.put(
            "reference",
            String.join("/", exampleResourceTypes.get(randomPick), UUID.randomUUID().toString()));
        return answer.put("valueReference", reference);
      default:
        return answer;
    }
  }

  JSONArray getAnswers(JSONArray questions, JSONArray responses) {
    for (int i = 0; i < questions.length(); i++) {
      JSONObject current_question = questions.getJSONObject(i);
      String question_type = current_question.getString("type");
      String link_id = current_question.getString("linkId");

      JSONArray answer_arr = new JSONArray();
      JSONObject answer = generateAnswer(question_type, questions, link_id);
      answer_arr.put(answer);

      if (question_type.equals("group")) {
        if (current_question.has("item")) {
          JSONArray group_questions = current_question.getJSONArray("item");
          JSONArray group_responses = responses.getJSONObject(i).getJSONArray("item");
          getAnswers(group_questions, group_responses);
        }
      }
      responses.getJSONObject(i).put("answer", answer_arr);
    }
    return responses;
  }

  String populateMode(String questionnaireData, String fhir_base_url) throws IOException {
    JSONObject resource = new JSONObject(questionnaireData);
    String questionnaire_id = resource.getString("id");
    String resourceType = resource.getString("resourceType");

    Boolean exists = checkResource(questionnaire_id, resourceType, resource, fhir_base_url);
    if (!exists) {
      throw new IllegalStateException("Error creating/updating the questionnaire!");
    }
    JSONObject subject = new JSONObject();
    subject.put("name", "subject");
    subject.put("valueString", UUID.randomUUID().toString());
    JSONArray arr = new JSONArray();
    arr.put(subject);
    JSONObject params = new JSONObject();
    params.put("resourceType", "Parameters");
    params.put("parameter", arr);

    FctUtils.printToConsole("Populating Questionnaire " + questionnaire_id);
    String populate_endpoint =
        String.join("/", fhir_base_url, resourceType, questionnaire_id, "$populate");
    List<String> result = HttpClient.postRequest(params.toString(), populate_endpoint, null);

    JSONObject questionnaire_response = new JSONObject(result.get(1));
    JSONArray response = (JSONArray) questionnaire_response.get("item");
    JSONArray questions = resource.getJSONArray("item");

    JSONArray response_with_answers = getAnswers(questions, response);
    questionnaire_response.put("item", response_with_answers);
    return String.valueOf(questionnaire_response);
  }

  String aiMode(String questionnaireData, String apiKey) throws IOException {
    Gson gson = new Gson();
    String questionnaireJsonString = gson.toJson(questionnaireData);
    // remove extra white spaces
    questionnaireJsonString = questionnaireJsonString.replaceAll("^ +| +$|( )+", "$1");
    // remove opening and closing quotes
    questionnaireJsonString =
        questionnaireJsonString.substring(1, questionnaireJsonString.length() - 1);

    // Generate response
    String generatedResponseString =
        aiGenerated(questionnaireJsonString, apiKey, this.aiModel, this.maxTokens);
    JSONObject obj = new JSONObject(generatedResponseString);
    JSONArray choices = obj.getJSONArray("choices");
    String questionnaireResponseString = "";
    for (int i = 0; i < choices.length(); i++) {
      questionnaireResponseString =
          choices.getJSONObject(i).getJSONObject("message").getString("content");
    }
    return questionnaireResponseString;
  }

  private static String aiGenerated(
      String questionnaireData, String apiKey, String model, String tokens) throws IOException {
    FctUtils.printInfo("Generating your questionnaire response");
    String endpoint = "https://api.openai.com/v1/chat/completions";
    String message =
        "Generate a fhir questionnaire response for the following fhir questionnaire: "
            + questionnaireData;
    String payload =
        "{\"model\": \""
            + model
            + "\", \"messages\": [{\"role\": \"user\", \"content\": \""
            + message
            + "\"}], \"max_tokens\": "
            + tokens
            + "}";

    OkHttpClient httpClient =
        new OkHttpClient()
            .newBuilder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .callTimeout(300, TimeUnit.SECONDS)
            .build();

    final MediaType JSON = MediaType.get("application/json");
    RequestBody body = RequestBody.create(payload, JSON);
    Request request =
        new Request.Builder()
            .url(endpoint)
            .post(body)
            .addHeader("Authorization", "Bearer " + apiKey)
            .build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (response.isSuccessful()) {
        assert response.body() != null;
        return response.body().string();
      } else {
        assert response.body() != null;
        logger.info(response.body().string());
        System.exit(1);
        return "-1";
      }
    }
  }

  public static class Constants {
    public static final String QUESTIONNAIRE_RESPONSE_SUFFIX = "-response.json";
  }
}
