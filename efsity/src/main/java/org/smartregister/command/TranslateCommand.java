package org.smartregister.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.smartregister.util.FctUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

@CommandLine.Command(name = "translate")
public class TranslateCommand implements Runnable {
  @CommandLine.Option(
    names = {"-m", "--mode"},
    description =
      "Options are either `extract` to generate the translation file from a questionnaire or " +
        "`merge` to import a translated file and populate the original questionnaire",
    required = true)
  private String mode;

  @CommandLine.Option(
    names = {"-rf", "--resourceFile"},
    description = "resource file path",
    required = true)
  private String resourceFile;

  @CommandLine.Option(
    names = {"-tf", "--translationFile"},
    description = "translation file path",
    required = true)
  private String translationFile;

  private final String[] modes = { "merge", "extract" };

  @Override
  public void run() {
    if (!Arrays.asList(modes).contains(mode)) {
      throw new RuntimeException("Modes should either be `extract` or `merge`");
    }
    try {
      if (Objects.equals(mode, "extract")) {
        long start = System.currentTimeMillis();

        FctUtils.printInfo("Starting text extraction");
        FctUtils.printInfo(String.format("Input file \u001b[35m%s\u001b[0m", resourceFile));
        FctUtils.printInfo(String.format("Translation file \u001b[35m%s\u001b[0m", translationFile));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(resourceFile));

        // Create a properties object to store the results
        Properties properties = new Properties();

        // Start the recursive extraction
        extractTranslationText(rootNode, "", properties);

        // Write the properties to a file
        properties.store(new FileOutputStream(translationFile), null);

        FctUtils.printInfo(String.format("Output file\u001b[36m %s \u001b[0m", translationFile));
        FctUtils.printCompletedInDuration(start);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  private void extractTranslationText(JsonNode node, String path, Properties properties)
    throws IOException {
    if (node.isObject()) {
      node.fields().forEachRemaining(entry -> {
        String key = entry.getKey();
        JsonNode value = entry.getValue();

        String newPath = path.isEmpty() ? key : path + "." + key;

        if (Objects.equals(key, "text")) {
          properties.setProperty(newPath , value.asText());
        }

        try {
          extractTranslationText(value, newPath, properties);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } else if (node.isArray()) {
      for (int i = 0; i < node.size(); i++) {
        extractTranslationText(node.get(i), path + "." + i, properties);
      }
    }
  }
}
