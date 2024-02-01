package org.smartregister.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.jimblackler.jsonschemafriend.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "validateFileStructure")
public class ValidateFileStructureCommand implements Runnable{

    @CommandLine.Option(
            names = {"-i", "--input"},
            description = "path of the project folder/directory",
            required = true)
    String inputFolder;

    @CommandLine.Option(
            names = {"-s", "--schema"},
            description = "path to project schema",
            required = true)
    String structureSchema;

    @Override
    public void run() {
        if (!Files.isDirectory(Paths.get(inputFolder))) {
            throw new RuntimeException("-i, --input path needs to be a directory");
        }

        try {
            validateFileStructure(inputFolder, structureSchema);
        } catch (IOException | ValidationException | GenerationException e) {
            throw new RuntimeException(e);
        }
    }

    JsonNode directoryToJson(String input) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        ObjectNode node = mapper.createObjectNode();

        Path inputPath = Paths.get(input);
        String currentDirectory = inputPath.getFileName().toString();

        List<Path> allDirectories;
        try ( Stream<Path> walk = Files.walk(inputPath, 1)){
            allDirectories = walk.filter(Files::isDirectory).collect(Collectors.toList());
        }

        List<Path> allFiles;
        try ( Stream<Path> walk = Files.walk(inputPath, 1)){
            allFiles = walk.filter(Files::isRegularFile).collect(Collectors.toList());
        }

        if (!allDirectories.isEmpty()){
            for (Path d : allDirectories) {
                if (!Objects.equals(d.getFileName().toString(), currentDirectory)){
                    arrayNode.add(directoryToJson(d.toString()));
                }
            }
        }

        if (!allFiles.isEmpty()){
            for (Path f : allFiles) {
                arrayNode.add(f.getFileName().toString());
            }
        }

        node.put(currentDirectory, arrayNode);
        return node;
    }

    void validateStructure(JsonNode structureNode, String structureSchema)
            throws ValidationException, GenerationException {

        SchemaStore schemaStore = new SchemaStore();
        Schema schema = schemaStore.loadSchema(new File(structureSchema));

        Validator validator = new Validator();
        validator.validateJson(schema, structureNode.toString());
    }

    private void validateFileStructure(String inputPath, String structureSchema)
            throws IOException, ValidationException, GenerationException {

        long start = System.currentTimeMillis();

        FctUtils.printInfo("Starting Project file structure validation");
        FctUtils.printInfo(String.format("Project path: \u001b[35m%s\u001b[0m", inputPath));
        FctUtils.printInfo(String.format("Structure schema path: \u001b[35m%s\u001b[0m", structureSchema));

        JsonNode node = directoryToJson(inputPath);
        validateStructure(node, structureSchema);

        FctUtils.printInfo("Your structure is valid!");
        FctUtils.printCompletedInDuration(start);
    }
}

