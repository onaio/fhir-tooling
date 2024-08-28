# QuestionnaireResponse Generator Command
## Overview
The QuestionnaireResponseGeneratorCommand is designed to generate FHIR QuestionnaireResponse resources from FHIR Questionnaire resources. This tool supports two modes of generation:

1. **Populate Mode**: (Default) Uses the FHIR Clinical Reasoning Module's `$populate` operation to generate a questionnaireResponse from a questionnaire.
2. **AI Mode**: Utilizes an AI model to generate a QuestionnaireResponses based on a Questionnaire.


### Options
```
-i, --input: (Required) Path to the input file containing the FHIR Questionnaire resource.
-gm, --generation-mode: (Optional) Generation mode. Options: populate (default), ai.
-fs, --fhir-server: (Optional) Base URL of the FHIR server to use for generation (required for populate mode).
-e, --extras: (Optional) Path to extra definitions for Faker value generation.
-k, --apiKey: (Optional) API key for accessing AI services (required for ai mode).
-m, --model: (Optional) AI model to use. Default: gpt-3.5-turbo-16k.
-t, --tokens: (Optional) Max tokens for AI generation. Default: 9000.
-o, --output: (Optional) Path to the output file or directory. Default: current directory.
```

### Example Usage
Generate a QuestionnaireResponse using populate mode:
```shell
fct generateResponse -i /path/to/questionnaire.json -fs http://fhir-server.example.com/fhir -e /path/to/extras.json
```
Generate a QuestionnaireResponse using AI mode:

```shell
fct generateResponse -i /path/to/questionnaire.json -gm ai -k YOUR_API_KEY
```

#### FHIR Server
You can quickly spin up the latest HAPI FHIR server to try this out by:
- Cloning the repo https://github.com/hapifhir/hapi-fhir-jpaserver-starter
- Open your `application.yaml` file in `src/main/resources` and update `cr-enabled` to `true`. This will enable the Clinical Reasoning module
- Run the server: `mvn spring-boot:run`
- _extra tip_: Ensure that the questionnaire you are using has a uuid as the identifier. Sometimes the resource creation fails if the id is all numbers

### Answer generation
Using the populate mode, once the QuestionnaireResponse is generated, we also need to generate answers to populate it. The tool by default uses Java's `Random` mainly for this. Generating integers, decimals, booleans and strings that are basically a concatenation of "FakeString" with a random integer.

The tool optionally, also leverages the `Faker` library for more realistic fake data generation. To use this option you need to provide an _extras.json_ file that has a list of the questions _(their link_ids)_ that you want to generate andswers for, and the categories/methods from the Faker class that you'd like to use.

Example extras.json file
```json
{
  "445e224e-1b01-4003-95d3-429d0e6459c4": {"category": "name", "method": "firstName"},
  "64554be3-acd2-4ea9-bcde-9bff728b22bc": {"category": "name", "method": "lastName"},
  "dcef66ee-f162-4619-b7a0-0560fee4638c": {"category": "demographic", "method": "sex"},
  "48b9e73a-04eb-4084-9566-76ff86ff25a2": {"category": "address", "method": "fullAddress"},
  "a98e3117-3bbd-4b4e-8634-8a4cac2641de": {"category": "medical", "method": "diseaseName"},
  "5a6477d2-431d-4711-9f41-3f43991f88e2": {"category": "number", "method": "randomDigit"},
  "3c331069-7e95-4df1-bc0b-f383f1304c37": {"category": "lorem", "method": "sentence"}
}
```

You can see a full list of options from Faker [here](https://dius.github.io/java-faker/apidocs/index.html)

## AI Mode
NOTE: Temporarily unsupported at the moment
