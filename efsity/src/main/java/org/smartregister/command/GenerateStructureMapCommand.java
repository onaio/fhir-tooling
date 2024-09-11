package org.smartregister.command;

import org.smartregister.structuremaptool.GenerateStructureMapServiceKt;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(name = "generateStructureMap")
public class GenerateStructureMapCommand implements Runnable {


    //generateStructureMap --questionnaire /Users/markloshilu/Ona/fhir-tooling/sm-gen/src/main/resources/questionnaire.json --configPath /Users/markloshilu/Ona/fhir-tooling/sm-gen/src/main/resources/StructureMap XLS.xls --questionnaireResponsePath /Users/markloshilu/Ona/fhir-tooling/sm-gen/src/main/resources/questionnaire-response.json
    @CommandLine.Option(
            names = {"-q", "--questionnaire"},
            description = "Questionnaire",
            required = true)
    private String questionnairePath;

    @CommandLine.Option(
            names = {"-c", "--configPath"},
            description = "StructureMap generation configuration in an excel sheet",
            required = true)
    private String configPath;

    @CommandLine.Option(
            names = {"-qr", "--questionnaireResponsePath"},
            description = "Questionnaire response",
            required = true)
    private String questionnaireResponsePath;

    @Override
    public void run(){
        if (configPath != null){
            try {
                generateStructureMap(configPath, questionnairePath, questionnaireResponsePath);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    public static void generateStructureMap(
            String configPath, String questionnairePath, String questionnaireResponsePath) throws IOException{
        long start = System.currentTimeMillis();
        FctUtils.printInfo("Starting StructureMap generation");
        GenerateStructureMapServiceKt.main(configPath, questionnairePath, questionnaireResponsePath);

        FctUtils.printCompletedInDuration(start);
    }
}
