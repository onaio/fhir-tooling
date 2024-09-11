package org.smartregister.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;

public class HttpClient {

  public static List<String> postRequest(String payload, String url, String accessToken)
      throws IOException {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(url);
    httpPost.setHeader("Content-Type", "application/fhir+json");
    // TODO pull accesstoken from OAUTH in utils
    if (accessToken != null) {
      httpPost.setHeader("Authorization", "Bearer " + accessToken);
    }
    httpPost.setEntity(new StringEntity(payload));
    HttpResponse response = httpClient.execute(httpPost);

    return getStrings(response);
  }

  @NotNull private static List<String> getStrings(HttpResponse response) throws IOException {
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    String inputLine;
    StringBuilder responseString = new StringBuilder();

    while ((inputLine = reader.readLine()) != null) {
      responseString.append(inputLine);
    }
    reader.close();
    List<String> result = new ArrayList<>();
    result.add(String.valueOf(response.getStatusLine().getStatusCode()));
    result.add(String.valueOf(responseString));
    return result;
  }
}
