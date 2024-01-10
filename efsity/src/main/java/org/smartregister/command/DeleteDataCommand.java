package org.smartregister.command;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;


import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpDelete;

import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "delete")
public class DeleteDataCommand implements Runnable {
  @CommandLine.Option(
    names = {"-u", "--baseUrl"},
    description =
      "FHIR base URL example http://localhost:8888/fhir",
    required = true)
  String baseUrl;

  @CommandLine.Option(
    names = {"-r", "--resourceType"},
    description = "Resource to be deleted",
    required = true)
  String resourceType;

  @CommandLine.Option(
    names = {"-id", "--identifiers"},
    description = "Resource Identifiers if deleting a single resource",
    required = true)
  String identifiers;

  @CommandLine.Option(
    names = {"-at", "--access-token"},
    description = "access token for fhir server")
  String accessToken;

  @Override
  public void run() {
    String url = baseUrl + "/" + resourceType + "?_id=" + identifiers + "&_cascade=delete";
    FctUtils.printInfo("Starting deletion");

    FctUtils.printInfo(String.format("URL: \u001b[35m%s\u001b[0m", url));

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(url);
    httpDelete.setHeader("Content-Type", "application/fhir+json");
    httpDelete.setHeader("Authorization", "Bearer " + accessToken);
    try {
      HttpResponse response = httpClient.execute(httpDelete);
      int statusCode = response.getStatusLine().getStatusCode();
      FctUtils.printToConsole("Response Status: " + statusCode);

      if (statusCode == 200) {
        handleSuccessfulResponse(response);
      } else {
        handleErrorResponse(response);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        httpClient.close();
      } catch (IOException e) {
        FctUtils.printError(e.getMessage());
      }
    }
  }
  private void handleSuccessfulResponse(HttpResponse response) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(response.getEntity().getContent());

    if (rootNode.has("issue")) {
      ArrayNode issues = (ArrayNode) rootNode.get("issue");
      for (JsonNode issue : issues) {
        if (issue.has("diagnostics")) {
          FctUtils.printToConsole("Diagnostics: " + issue.get("diagnostics").asText());
        }
      }
    }
  }

  private void handleErrorResponse(HttpResponse response) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    JsonNode rootNode = objectMapper.readTree(response.getEntity().getContent());
    FctUtils.printToConsole("Error Response:");
    FctUtils.printToConsole(objectMapper.writeValueAsString(rootNode));
  }
}
