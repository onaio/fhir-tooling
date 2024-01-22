package org.smartregister.util.authentication;

import org.json.JSONObject;
import org.smartregister.util.FctUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;
public class OAuthAuthentication {
    public static String getAccessToken(String clientId, String clientSecret,
                                        String accessTokenUrl, String grantType, String username, String password){
        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);
        requestBody.put("grant_type", grantType);

        try{
            final BodyPublisher bodyPublisher = BodyPublishers.ofString(requestBody.keySet().stream()
                    .map(key -> key + "=" + URLEncoder.encode(requestBody.get(key), StandardCharsets.UTF_8))
                    .collect(joining("&")));

            final byte[] clientCredentials = Base64.getEncoder()
                    .encode((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(accessTokenUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic " + new String(clientCredentials, StandardCharsets.UTF_8))
                    .header("Accept", "application/json")
                    .POST(bodyPublisher)
                    .build();

            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .build();

            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());

            if (jsonResponse.has("access_token")){
                return jsonResponse.optString("access_token");
            } else {
                FctUtils.printError("Attempt to get accessToken failed");
                throw new RuntimeException(jsonResponse.toString());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get accessToken", e);
        }
    }
}
