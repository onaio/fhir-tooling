package org.smartregister.command;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.smartregister.domain.FctFile;
import org.smartregister.fhircore_tooling.BuildConfig;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.smartregister.util.authentication.OAuthAuthentication.getAccessToken;

@CommandLine.Command(name = "publish")
public class PublishFhirResourcesCommand implements Runnable{

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

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        if(propertiesFile != null && !propertiesFile.isBlank()){
            try(InputStream inputProperties = new FileInputStream(propertiesFile)){
                Properties properties = new Properties();
                properties.load(inputProperties);
                setProperties(properties);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            publishResources();
            stateManagement();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FctUtils.printCompletedInDuration(start);
    }

    void setProperties(Properties properties){
        if (projectFolder == null || projectFolder.isBlank()){
            if (properties.getProperty("projectFolder") != null){
                projectFolder = properties.getProperty("projectFolder");
            } else {
                throw new NullPointerException("The projectFolder is missing");
            }
        }
        if (fhirBaseUrl == null || fhirBaseUrl.isBlank()){
            if (properties.getProperty("fhirBaseUrl") != null){
                fhirBaseUrl = properties.getProperty("fhirBaseUrl");
            } else {
                throw new NullPointerException("The fhirBaseUrl is missing");
            }
        }
        if (accessToken == null || accessToken.isBlank()){
            if (properties.getProperty("accessToken") != null){
                accessToken = properties.getProperty("accessToken");
            }
        }
        if (clientId == null || clientId.isBlank()){
            if (properties.getProperty("clientId") != null){
                clientId = properties.getProperty("clientId");
            }
        }
        if (clientSecret == null || clientSecret.isBlank()){
            if (properties.getProperty("clientSecret") != null){
                clientSecret = properties.getProperty("clientSecret");
            }
        }
        if (username == null || username.isBlank()){
            if (properties.getProperty("username") != null){
                username = properties.getProperty("username");
            }
        }
        if (password == null || password.isBlank()){
            if (properties.getProperty("password") != null){
                password = properties.getProperty("password");
            }
        }
        if (accessTokenUrl == null || accessTokenUrl.isBlank()){
            if (properties.getProperty("accessTokenUrl") != null){
                accessTokenUrl = properties.getProperty("accessTokenUrl");
            }
        }
        if (grantType == null || grantType.isBlank()){
            if (properties.getProperty("grantType") != null){
                grantType = properties.getProperty("grantType");
            }
        }
    }

    void publishResources() throws IOException {
        ArrayList<String> resourceFiles = getResourceFiles(projectFolder);
        ArrayList<JSONObject> resourceObjects = new ArrayList<>();
        for(String f: resourceFiles){
            FctFile inputFile = FctUtils.readFile(f);
            // TODO check if file contains valid fhir resource
            JSONObject resourceObject = buildResourceObject(inputFile);
            resourceObjects.add(resourceObject);
        }

        // build the bundle
        JSONObject bundle = new JSONObject();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        bundle.put("entry",resourceObjects);
        FctUtils.printToConsole("Full Payload to POST: ");
        FctUtils.printToConsole(bundle.toString());

        if (accessToken == null || accessToken.isBlank()){
            if(clientId == null || clientId.isBlank()){
                throw new IllegalArgumentException("You must provide either the accessToken or the clientId");
            }
            if(clientSecret == null || clientSecret.isBlank()){
                throw new IllegalArgumentException("You must provide either the accessToken or the clientSecret");
            }
            if(username == null || username.isBlank()){
                throw new IllegalArgumentException("You must provide either the accessToken or the username");
            }
            if(password == null || password.isBlank()){
                throw new IllegalArgumentException("You must provide either the accessToken or the password");
            }
            accessToken = getAccessToken(clientId, clientSecret, accessTokenUrl, grantType, username, password);
        }
        postRequest(bundle.toString(), accessToken);
    }

    ArrayList<String> getResourceFiles(String pathToFolder) throws IOException {
        ArrayList<String> filesArray = new ArrayList<>();
        Path projectPath = Paths.get(pathToFolder);
        if (Files.isDirectory(projectPath)){
            Files.walk(projectPath).forEach(path -> getFiles(filesArray, path.toFile()));
        } else if (Files.isRegularFile(projectPath)) {
            filesArray.add(pathToFolder);
        }
        return filesArray;
    }

    void getFiles(ArrayList<String> filesArray, File file){
        if (file.isFile()) {
            filesArray.add(file.getAbsolutePath());
        }
    }

    JSONObject buildResourceObject(FctFile inputFile){
        JSONObject resource = new JSONObject(inputFile.getContent());
        String resourceType = null;
        String resourceID;
        if(resource.has("resourceType")) {
            resourceType = resource.getString("resourceType");
        }
        if(resource.has("id")){
            resourceID = resource.getString("id");
        } else {
            resourceID = UUID.randomUUID().toString();
        }

        JSONObject request = new JSONObject();
        request.put("method", "PUT");
        request.put("url", resourceType + "/" + resourceID );

        ArrayList<JSONObject> tags = new ArrayList<>();
        JSONObject version = new JSONObject();
        version.put("system", "https://smartregister.org/fct-release-version");
        version.put("code", BuildConfig.RELEASE_VERSION);
        tags.add(version);

        JSONObject meta = new JSONObject();
        meta.put("tag", tags);
        resource.put("meta", meta);

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
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
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
        if(Files.isDirectory(Paths.get(projectFolder))){
            pathToManifest = projectFolder + "/.efsity/state.json";
        } else {
            pathToManifest = getProjectFolder(projectFolder);
        }
        File manifestFile = new File(pathToManifest);

        // Create folder if it does not exist
        if (Files.notExists(Paths.get(pathToManifest))){
            if(manifestFile.getParentFile().mkdirs()){
                if(manifestFile.createNewFile()){
                    FctUtils.printToConsole("Manifest file created successfully");
                }
            }
        }

        // Set initial content
        String initialContent;
        if (manifestFile.length() != 0){
            initialContent = FctUtils.readFile(pathToManifest).getContent();
        } else {
            initialContent = "[]";
        }

        JSONObject currentState = new JSONObject();
        currentState.put("fctVersion", BuildConfig.RELEASE_VERSION);
        currentState.put("url", fhirBaseUrl);
        currentState.put("updated", updatedAt());
        String finalString;
        if (manifestFile.length() != 0){
            finalString = initialContent.substring(0, initialContent.length() - 2) + ",\n" + currentState + "]";
        } else {
            finalString = initialContent.substring(0, initialContent.length() - 1) + currentState + "]";
        }

        FileWriter writer = new FileWriter(pathToManifest);
        writer.write(finalString);
        writer.flush();
        writer.close();
    }

    String updatedAt(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    // This function assumes the .efsity folder already exists in the project/parent folder
    // and simply tries to find it
    String getProjectFolder(String projectFolder){
        File resourceFile = new File(projectFolder);
        File parentFolder = resourceFile.getParentFile();
        boolean check = new File(parentFolder, ".efsity").exists();
        if (!check){
            return getProjectFolder(parentFolder.toString());
        }
        return parentFolder.toString();
    }
}
