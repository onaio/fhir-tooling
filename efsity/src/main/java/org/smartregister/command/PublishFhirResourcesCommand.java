package org.smartregister.command;

import static org.smartregister.util.authentication.OAuthAuthentication.getAccessToken;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import net.jimblackler.jsonschemafriend.GenerationException;
import net.jimblackler.jsonschemafriend.ValidationException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.domain.FctFile;
import org.smartregister.fhircore_tooling.BuildConfig;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "publish")
public class PublishFhirResourcesCommand implements Runnable {
  static JsonParser jsonParser = new JsonParser();

  @CommandLine.Option(
      names = {"-i", "--input"},
      description = "path of the file or folder to publish")
  String projectFolder;

  @CommandLine.Option(
      names = {"-bu", "--fhir-base-url"},
      description = "fhir server base url")
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

  @CommandLine.Option(
      names = {"-vr", "--validate-resource"},
      description =
          "(Optional) whether to validate FHIR resources before publishing or not. Boolean - default is `true`",
      required = false)
  String validateResource = "true";

  @CommandLine.Option(
      names = {"-c", "--composition"},
      description = "path of the composition configuration file",
      required = false)
  String compositionFilePath;

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
      if (compositionFilePath != null) {
        ArrayList<JSONObject> resourceObjects = buildBinaries(compositionFilePath, projectFolder);
        buildBundle(resourceObjects);
      }
      buildResources();
      stateManagement();
    } catch (IOException | ValidationException | GenerationException e) {
      throw new RuntimeException(e);
    }
    FctUtils.printCompletedInDuration(start);
  }

  void setProperties(Properties properties) {
    if (properties == null)
      throw new IllegalStateException("Properties file is missing or could not be parsed");

    if (projectFolder == null || projectFolder.isBlank()) {
      if (properties.getProperty("projectFolder") != null) {
        projectFolder = properties.getProperty("projectFolder");
      } else {
        throw new IllegalStateException("The projectFolder is missing");
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

  /**
   * This function takes in the name of a binary component/resource, converts it from camel case to
   * separated by underscores. It then appends a string depending on the name, to return the actual
   * file name of the binary file. For example the binaryName 'ancRegister' will be converted to
   * 'anc_register_config.json'
   *
   * @param binaryName This is the name of a binary component/resource as it appears in the
   *     composition resource. Usually in camel case and matches the start of the actual file name
   * @return filename This is the actual file name of the binary resource in the project folder
   */
  String getFileName(String binaryName) {
    String filename;
    if ((binaryName.endsWith("Register")) || (binaryName.endsWith("Profile"))) {
      String regex = "([a-z])([A-Z]+)";
      String replacer = "$1_$2";
      binaryName = binaryName.replaceAll(regex, replacer).toLowerCase();
    }
    if (binaryName.startsWith("strings")) {
      filename = binaryName + "_config.properties";
    } else {
      filename = binaryName + "_config.json";
    }
    return filename;
  }

  HashMap<String, String> getDetails(JSONObject jsonObject) {
    JSONObject focus = jsonObject.getJSONObject("focus");
    String reference = focus.getString("reference");
    JSONObject identifier = focus.getJSONObject("identifier");
    String name = identifier.getString("value");

    HashMap<String, String> map = new HashMap<>();
    map.put("reference", reference);
    map.put("name", getFileName(name));
    return map;
  }

  /**
   * This function takes in a binary file name and project folder, it then opens the filename in the
   * folder ( assuming the recommended folder structure ), reads the content and returns a base64
   * encoded version of the content
   *
   * @param fileName This is the name of the json binary file
   * @param projectFolder This is the folder with all the config files
   * @return base64 encoded version of the content in the binary json file
   * @throws IOException
   */
  String getBinaryContent(String fileName, String projectFolder) throws IOException {
    String pathToFile;
    if (fileName.contains("register")) {
      pathToFile = projectFolder + "/registers/" + fileName;
    } else if (fileName.contains("profile")) {
      pathToFile = projectFolder + "/profiles/" + fileName;
    } else if (fileName.startsWith("strings_")) {
      pathToFile = projectFolder + "/translations/" + fileName;
    } else {
      pathToFile = projectFolder + "/" + fileName;
    }

    String fileContent = FctUtils.readFile(pathToFile).getContent();
    return Base64.getEncoder().encodeToString(fileContent.getBytes(StandardCharsets.UTF_8));
  }

  ArrayList<JSONObject> buildBinaries(String compositionFilePath, String projectFolder)
      throws IOException {
    FctFile compositionFile = FctUtils.readFile(compositionFilePath);
    JSONObject compositionResource = new JSONObject(compositionFile.getContent());
    List<Map<String, String>> mapList = new ArrayList<>();
    Map<String, String> detailsMap = new HashMap<>();
    ArrayList<JSONObject> resourceObjects = new ArrayList<>();

    if (compositionResource.has("section")) {
      JSONArray compositionObjects = compositionResource.getJSONArray("section");

      for (Object obj : compositionObjects) {
        JSONObject jsonObject = new JSONObject(obj.toString());
        if (jsonObject.has("section")) {
          JSONArray section = jsonObject.getJSONArray("section");
          for (Object subObj : section) {
            JSONObject jo = new JSONObject(subObj.toString());
            detailsMap = getDetails(jo);
          }
        } else {
          detailsMap = getDetails(jsonObject);
        }
        mapList.add(detailsMap);
      }

      for (Map<String, String> e : mapList) {
        String filename = e.get("name");
        String binaryContent = getBinaryContent(filename, projectFolder);
        String contentType;

        if (filename.startsWith("strings_")) {
          contentType = "text/plain";
        } else {
          contentType = "application/json";
        }

        JSONObject binaryResourceObject = new JSONObject();
        binaryResourceObject.put("resourceType", "Binary");
        binaryResourceObject.put("id", e.get("reference").substring(7));
        binaryResourceObject.put("contentType", contentType);
        binaryResourceObject.put("data", binaryContent);

        JSONObject finalResourceObject = buildResourceObject(binaryResourceObject.toString());
        resourceObjects.add(finalResourceObject);
      }
    }
    return resourceObjects;
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

  void buildBundle(ArrayList<JSONObject> resourceObjects) throws IOException {
    JSONObject bundle = new JSONObject();
    bundle.put("resourceType", "Bundle");
    bundle.put("type", "transaction");
    bundle.put("entry", resourceObjects);
    FctUtils.printToConsole("Full Payload to POST: ");
    FctUtils.printToConsole(bundle.toString());

    postRequest(bundle.toString(), getToken());
  }

  void buildResources() throws IOException, ValidationException, GenerationException {
    ArrayList<String> resourceFiles = getResourceFiles(projectFolder);
    ArrayList<JSONObject> resourceObjects = new ArrayList<>();
    boolean validateResourceBoolean = Boolean.parseBoolean(validateResource);

    for (String f : resourceFiles) {
      if (validateResourceBoolean) {
        FctUtils.printInfo(String.format("Validating file \u001b[35m%s\u001b[0m", f));
        ValidateFhirResourcesCommand.validateFhirResources(f);
      } else {
        FctUtils.printInfo(String.format("Publishing \u001b[35m%s\u001b[0m Without Validation", f));
      }

      FctFile inputFile = FctUtils.readFile(f);
      JSONObject resourceObject = buildResourceObject(inputFile.getContent());
      resourceObjects.add(resourceObject);
    }
    buildBundle(resourceObjects);
  }

  static ArrayList<String> getResourceFiles(String pathToFolder) throws IOException {
    ArrayList<String> filesArray = new ArrayList<>();
    Path projectPath = Paths.get(pathToFolder);
    if (Files.isDirectory(projectPath) && !pathToFolder.startsWith("x_")) {
      Files.walk(projectPath).forEach(path -> getFiles(filesArray, path.toFile()));
    } else if (Files.isRegularFile(projectPath)) {

      if (!projectPath.getFileName().toString().startsWith("x_")
          && projectPath.getFileName().toString().endsWith(".json")) {
        addFhirResource(pathToFolder, filesArray);
      } else {
        FctUtils.printWarning("Dropping " + projectPath.getFileName());
      }
    }
    return filesArray;
  }

  static void getFiles(ArrayList<String> filesArray, File file) {
    if (file.isFile()) {
      if (!file.getName().startsWith("x_") && file.getName().endsWith(".json")) {
        addFhirResource(file.getAbsolutePath(), filesArray);
      } else {
        FctUtils.printWarning(
            "Dropping " + file.getAbsolutePath() + " with name: " + file.getName());
      }
    }
  }

  private static void addFhirResource(String filePath, List<String> filesArray) {

    try {
      JsonElement jsonElement = jsonParser.parse(new FileReader(filePath));
      if (jsonElement.getAsJsonObject().get("resourceType") != null) filesArray.add(filePath);

    } catch (Exception e) {
      FctUtils.printError(e.getMessage());
    }
  }

  JSONObject buildResourceObject(String fileContent) {
    JSONObject resource = new JSONObject(fileContent);
    String resourceType = null;
    String resourceID;
    if (resource.has("resourceType")) {
      resourceType = resource.getString("resourceType");
    }
    if (resource.has("id")) {
      resourceID = resource.getString("id");
    } else {
      resourceID = UUID.randomUUID().toString();
    }

    JSONObject request = new JSONObject();
    request.put("method", "PUT");
    request.put("url", resourceType + "/" + resourceID);

    JSONObject version = new JSONObject();
    version.put("system", "https://smartregister.org/fct-release-version");
    version.put("code", BuildConfig.RELEASE_VERSION);

    if (resource.has("meta")) {
      JSONObject resource_meta = (JSONObject) resource.get("meta");
      if (resource_meta.has("tag")) {
        JSONArray resource_tags = resource_meta.getJSONArray("tag");
        resource_tags.put(version);
      }
    } else {
      ArrayList<JSONObject> tags = new ArrayList<>();
      tags.add(version);

      JSONObject meta = new JSONObject();
      meta.put("tag", tags);
      resource.put("meta", meta);
    }

    JSONObject object = new JSONObject();
    object.put("resource", resource);
    object.put("request", request);

    return object;
  }

  void postRequest(String payload, String accessToken) throws IOException {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(fhirBaseUrl);
    httpPost.setHeader("Content-Type", "application/fhir+json");
    httpPost.setHeader("Authorization", "Bearer " + accessToken);
    httpPost.setEntity(new StringEntity(payload));
    HttpResponse response = httpClient.execute(httpPost);

    FctUtils.printToConsole("Response Status: " + response.getStatusLine().getStatusCode());
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    String inputLine;
    StringBuilder responseString = new StringBuilder();

    while ((inputLine = reader.readLine()) != null) {
      responseString.append(inputLine);
    }
    reader.close();
    FctUtils.printToConsole("Response Content: " + responseString);
  }

  void stateManagement() throws IOException {
    String pathToManifest;
    if (Files.isDirectory(Paths.get(projectFolder))) {
      pathToManifest = projectFolder + "/.efsity/state.json";
    } else {
      pathToManifest = getProjectFolder(projectFolder);
    }
    File manifestFile = new File(pathToManifest);

    // Create folder if it does not exist
    if (Files.notExists(Paths.get(pathToManifest))) {
      if (manifestFile.getParentFile().mkdirs()) {
        if (manifestFile.createNewFile()) {
          FctUtils.printToConsole("Manifest file created successfully");
        }
      }
    }

    // Set initial content
    String initialContent;
    if (manifestFile.length() != 0) {
      initialContent = FctUtils.readFile(pathToManifest).getContent();
    } else {
      initialContent = "[]";
    }

    JSONObject currentState = new JSONObject();
    currentState.put("fctVersion", BuildConfig.RELEASE_VERSION);
    currentState.put("url", fhirBaseUrl);
    currentState.put("updated", updatedAt());
    String finalString;
    if (manifestFile.length() != 0) {
      finalString =
          initialContent.substring(0, initialContent.length() - 2) + ",\n" + currentState + "]";
    } else {
      finalString = initialContent.substring(0, initialContent.length() - 1) + currentState + "]";
    }

    FileWriter writer = new FileWriter(pathToManifest);
    writer.write(finalString);
    writer.flush();
    writer.close();
  }

  String updatedAt() {
    Date date = new Date(System.currentTimeMillis());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sdf.format(date);
  }

  // This function assumes the .efsity folder already exists in the project/parent folder
  // and simply tries to find it
  String getProjectFolder(String projectFolder) {
    File resourceFile = new File(projectFolder);
    File parentFolder = resourceFile.getParentFile();
    boolean check = new File(parentFolder, ".efsity").exists();
    if (!check) {
      return getProjectFolder(parentFolder.toString());
    }
    return parentFolder.toString();
  }

  @VisibleForTesting
  public static final String getFCTReleaseVersion() {
    return BuildConfig.RELEASE_VERSION;
  }
}
