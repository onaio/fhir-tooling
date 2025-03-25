package org.smartregister.command;

import static org.smartregister.util.authentication.OAuthAuthentication.getAccessToken;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import org.smartregister.helpers.LocationHelper;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "updateLocationLineage")
public class UpdateLocationLineageCommand implements Runnable {
  @CommandLine.Option(
      names = {"-ids", "--location-ids"},
      description = "path of the file that contains location ids",
      required = true)
  String locationIdsFile;

  @CommandLine.Option(
      names = {"-bu", "--fhir-base-url"},
      description = "fhir server base url",
      required = true)
  String fhirBaseUrl;

  @CommandLine.Option(
      names = {"-at", "--access-token"},
      description = "access token for fhir server")
  String accessToken;

  @CommandLine.Option(
      names = {"-ci", "--client-id"},
      description = "The client identifier for authentication")
  String clientId;

  @CommandLine.Option(
      names = {"-cs", "--client-secret"},
      description = "The client secret for authentication")
  String clientSecret;

  @CommandLine.Option(
      names = {"-u", "--username"},
      description = "The username for authentication")
  String username;

  @CommandLine.Option(
      names = {"-p", "--password"},
      description = "The password for authentication")
  String password;

  @CommandLine.Option(
      names = {"-au", "--accessToken-url"},
      description = "The endpoint for the authentication server")
  String accessTokenUrl;

  @CommandLine.Option(
      names = {"-g", "--grant-type"},
      description = "The authorization code grant type",
      defaultValue = "password")
  String grantType;

  @CommandLine.Option(
      names = {"-e", "--env"},
      description = "path to env.properties file")
  String propertiesFile;

  @Override
  public void run() {
    long start = System.currentTimeMillis();
    if (propertiesFile != null && !propertiesFile.isBlank()) {

      Properties properties = null;
      try {
        properties = FctUtils.readPropertiesFile(propertiesFile);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      setProperties(properties);
    }
    try {
      String token = getToken();
      IGenericClient client = getClient(fhirBaseUrl, token);
      List<String> locationIds = Files.readAllLines(Paths.get(locationIdsFile));

      for (String locationId : locationIds) {
        FctUtils.printInfo(
            String.format("Updating lineage for location id:  \u001b[35m%s\u001b[0m", locationId));
        LocationHelper.updateLocationLineage(client, locationId);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    FctUtils.printCompletedInDuration(start);
  }

  void setProperties(Properties properties) {
    if (properties == null)
      throw new IllegalStateException("Properties file is missing or could not be parsed");

    if (locationIdsFile == null || locationIdsFile.isBlank()) {
      if (properties.getProperty("locationIdsFile") != null) {
        locationIdsFile = properties.getProperty("locationIdsFile");
      } else {
        throw new IllegalStateException("The locationIds file is missing");
      }
    }
    if (fhirBaseUrl == null || fhirBaseUrl.isBlank()) {
      if (properties.getProperty("fhirBaseUrl") != null) {
        fhirBaseUrl = properties.getProperty("fhirBaseUrl");
      } else {
        throw new IllegalStateException("The fhirBaseUrl is missing");
      }
    }
    if (accessToken == null || accessToken.isBlank()) {
      if (properties.getProperty("accessToken") != null) {
        accessToken = properties.getProperty("accessToken");
      }
    }
    if (clientId == null || clientId.isBlank()) {
      if (properties.getProperty("clientId") != null) {
        clientId = properties.getProperty("clientId");
      }
    }
    if (clientSecret == null || clientSecret.isBlank()) {
      if (properties.getProperty("clientSecret") != null) {
        clientSecret = properties.getProperty("clientSecret");
      }
    }
    if (username == null || username.isBlank()) {
      if (properties.getProperty("username") != null) {
        username = properties.getProperty("username");
      }
    }
    if (password == null || password.isBlank()) {
      if (properties.getProperty("password") != null) {
        password = properties.getProperty("password");
      }
    }
    if (accessTokenUrl == null || accessTokenUrl.isBlank()) {
      if (properties.getProperty("accessTokenUrl") != null) {
        accessTokenUrl = properties.getProperty("accessTokenUrl");
      }
    }
    if (grantType == null || grantType.isBlank()) {
      if (properties.getProperty("grantType") != null) {
        grantType = properties.getProperty("grantType");
      }
    }
  }

  String getToken() {
    if (accessToken == null || accessToken.isBlank()) {
      if (clientId == null || clientId.isBlank()) {
        throw new IllegalArgumentException(
            "You must provide either the accessToken or the clientId");
      }
      if (clientSecret == null || clientSecret.isBlank()) {
        throw new IllegalArgumentException(
            "You must provide either the accessToken or the clientSecret");
      }
      if (username == null || username.isBlank()) {
        throw new IllegalArgumentException(
            "You must provide either the accessToken or the username");
      }
      if (password == null || password.isBlank()) {
        throw new IllegalArgumentException(
            "You must provide either the accessToken or the password");
      }
      accessToken =
          getAccessToken(clientId, clientSecret, accessTokenUrl, grantType, username, password);
    }
    return accessToken;
  }

  IGenericClient getClient(String fhirBaseUrl, String accessToken) {
    FhirContext fhirContext = FhirContext.forR4();

    IGenericClient client = fhirContext.newRestfulGenericClient(fhirBaseUrl);
    client.registerInterceptor(new LoggingInterceptor(true));

    if (accessToken != null && !accessToken.isEmpty()) {
      client.registerInterceptor(new BearerTokenAuthInterceptor(accessToken));
    }
    return client;
  }
}
