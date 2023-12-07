package org.smartregister.command;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.smartregister.domain.FctFile;
import org.smartregister.util.FctUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PublishFhirResourcesCommandTest {

    private PublishFhirResourcesCommand publishFhirResourcesCommand;

    @TempDir
    static Path tempDirectory;

    @BeforeEach
    void setUp(){
        publishFhirResourcesCommand = new PublishFhirResourcesCommand();
    }

    @Test
    void testGetResourceFiles() throws IOException {
        // add folders and file to form sample project structure
        Path testFolder = Files.createDirectory(tempDirectory.resolve("testFileFolder"));
        Path projectFolder = Files.createDirectory(testFolder.resolve("testProject"));

        // create 3 folders in projectFolder
        Path questionnaireFolder = Files.createDirectory(projectFolder.resolve("questionnaires"));
        Path plansFolder = Files.createDirectory(projectFolder.resolve("plan_definitions"));
        Path structureMapsFolder = Files.createDirectory(projectFolder.resolve("structureMaps"));

        // create a file in each of the folders above
        Path questionnaireFile = Files.createFile(questionnaireFolder.resolve("patient_registration.json"));
        Path planFile = Files.createFile(plansFolder.resolve("anc_visit.json"));
        Path structureMapFile = Files.createFile(structureMapsFolder.resolve("pregnancy_screening.json"));

        // get files in the folder
        ArrayList<String> resourceFiles = publishFhirResourcesCommand.getResourceFiles(projectFolder.toString());

        assertEquals(3, resourceFiles.size());
        assertTrue(resourceFiles.contains(questionnaireFile.toString()));
        assertTrue(resourceFiles.contains(planFile.toString()));
        assertTrue(resourceFiles.contains(structureMapFile.toString()));
    }

    @Test
    void testBuildResourceObject() throws IOException {
        Path testFolder = Files.createDirectory(tempDirectory.resolve("testObjectFolder"));
        Path resourceFile = Files.createFile(testFolder.resolve("group.json"));

        String sampleResource = "{\n" +
                "  \"resourceType\": \"Group\",\n" +
                "  \"id\": \"548060c9-8e9b-4b0d-88e7-925e9348fdae\",\n" +
                "  \"identifier\": [\n" +
                "    {\n" +
                "      \"use\": \"official\",\n" +
                "      \"value\": \"548060c9-8e9b-4b0d-88e7-925e9348fdae\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"active\": false,\n" +
                "  \"name\": \"Test Group\"\n" +
                "}";
        FileWriter writer = new FileWriter(String.valueOf(resourceFile));
        writer.write(sampleResource);
        writer.flush();
        writer.close();

        FctFile testFile = FctUtils.readFile(resourceFile.toString());
        JSONObject resourceObject = publishFhirResourcesCommand.buildResourceObject(testFile);

        // assert that object has request
        assertEquals(
               "{\"method\":\"PUT\",\"url\":\"Group/548060c9-8e9b-4b0d-88e7-925e9348fdae\"}",
               resourceObject.get("request").toString());

        // assert object has meta with version tag
        JSONObject resource = (JSONObject) resourceObject.get("resource");
        assertTrue(resource.get("meta").toString()
                .contains("{\"tag\":[{\"system\":\"https://smartregister.org/fct-release-version\",\"code\":\""));
    }
}