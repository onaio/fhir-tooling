package org.smartregister.command;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jimblackler.jsonschemafriend.ListValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class ValidateFileStructureCommandTest {

    private ValidateFileStructureCommand validateFileStructureCommand;

    @TempDir
    static Path tempDirectory;

    @BeforeEach
    void setUp() {
        validateFileStructureCommand = new ValidateFileStructureCommand();
    }

    // Test that directory structure is correctly converted to Json
    @Test
    void testDirectoryToJson() throws IOException {
        // add folders and file to form sample project structure
        Path projectFolder = Files.createDirectory(tempDirectory.resolve("testProject"));
        Path appsFolder = Files.createDirectory(projectFolder.resolve("apps"));
        Path profilesFolder = Files.createDirectory(appsFolder.resolve("profiles"));
        Path defaultProfile = Files.createFile(profilesFolder.resolve("default_profile_config.json"));

        // actual json from directory path
        JsonNode actualJson = validateFileStructureCommand.directoryToJson(projectFolder.toString());

        // expected json
        String json = "{\"testProject\": [{\"apps\": [{\"profiles\": [\"default_profile_config.json\"]}]} ]}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.readTree(json);

        // assert that the actual matches what is expected
        assertEquals(expectedJson, actualJson);
    }

    // Test that directory structure is correctly validated with correct JSON schema with no exceptions
    @Test
    void testValidateStructureSuccess() throws IOException {
        // structure schema
        Path structureSchema = Files.createFile(tempDirectory.resolve("successStructureSchema.json"));
        String structureSchemaString = "{\n" +
                "    \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                "    \"title\": \"Project structure\",\n" +
                "    \"type\": \"object\",\n" +
                "    \"properties\": {\n" +
                "        \"project\": {\n" +
                "            \"type\": \"array\",\n" +
                "            \"items\": {\n" +
                "                \"type\": \"object\",\n" +
                "                \"properties\": {\n" +
                "                    \"apps\": {\n" +
                "                        \"type\": \"array\",\n" +
                "                        \"items\": {\n" +
                "                            \"type\": [\"object\", \"string\"],\n" +
                "                            \"properties\": {\n" +
                "                                \"profiles\": {\n" +
                "                                    \"type\": \"array\",\n" +
                "                                    \"items\": {\n" +
                "                                        \"type\": \"string\",\n" +
                "                                        \"pattern\": \"._config.json$\"\n" +
                "                                    }}}}}}}}}}";
        Files.writeString(structureSchema, structureSchemaString);

        // structure node
        String projectFolder = "{\"project\" : [{\"apps\" : [{\"profiles\" : [\"default_profile_config.json\"]}," +
                " \"application_config.json\" ]}]}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode structureNode = mapper.readTree(projectFolder);

        // assert no exception is thrown
        assertDoesNotThrow(() -> {
            validateFileStructureCommand.validateStructure(structureNode, structureSchema.toString());
        });
    }

    // Test that incorrect schema + structure combination fails with exception as expected
    @Test
    void testValidateStructureFailure() throws IOException {
        // structure schema
        Path structureSchema = Files.createFile(tempDirectory.resolve("failStructureSchema.json"));
        String structureSchemaString = "{\n" +
                "    \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                "    \"title\": \"Project structure\",\n" +
                "    \"type\": \"object\",\n" +
                "    \"properties\": {\n" +
                "        \"project\": {\n" +
                "            \"type\": \"object\",\n" + // Changed array to object to fail type
                "            \"items\": {\n" +
                "                \"type\": \"object\",\n" +
                "                \"properties\": {}\n" +
                "            }}}}";
        Files.writeString(structureSchema, structureSchemaString);

        // structure node
        String projectFolder = "{\"project\" : [{\"apps\" : [{\"profiles\" : [\"default_profile_config.json\"]}," +
                " \"application_config.json\" ]}]}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode structureNode = mapper.readTree(projectFolder);

        Exception failException = assertThrows(ListValidationException.class, () ->
                validateFileStructureCommand.validateStructure(structureNode, structureSchema.toString()));

        // assert exception contains the expected message
        String expectedMessage = "Expected: [object] Found: [array]";
        String actualMessage = failException.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
