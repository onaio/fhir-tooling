package org.smartregister.command;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

  @Override
  public void run() {
    if (inputFilePath != null) {
      try {
        generateResponse(inputFilePath, apiKey);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void generateResponse(String inputFilePath, String apiKey) throws IOException {
    long start = System.currentTimeMillis();

    FctUtils.printInfo("Starting FHIR questionnaireResponse generation");
    FctUtils.printInfo(String.format("Input file path \u001b[35m%s\u001b[0m", inputFilePath));

    // Get questionnaire
    FctUtils.printInfo("Reading your questionnaire");
    FctFile inputFile = FctUtils.readFile(inputFilePath);
    String questionnaireData = inputFile.getContent();
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
        return response.body().string();
      } else {
        logger.info(response.body().string());
        System.exit(1);
        return "-1";
      }
    }
  }

  public class Constants {
    public static final String QUESTIONNAIRE_RESPONSE_SUFFIX = "-response.json";
  }
}
