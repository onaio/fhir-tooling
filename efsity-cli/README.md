# efsity
A command line utility to support OpenSRP v2 (FHIRCore) app configs and content authoring. This tool supports the HL7 FHIR R4 spec.

## How to use it

Download the latest release from https://github.com/opensrp/fhircore-tooling/releases

You can run it as a java jar by using the command `java -jar efsity-<version>.jar -h` . This is the help command and will list the available options.

If you are using a linux environment e.g. bash you can choose to create an _alias_ for this as shown below. _(Remember to reload the terminal)_

`alias fct='java -jar ~/Downloads/efsity-<version>.jar'`

To make sure that the alias persists on reload, add the alias to the `.bashrc` file in your `home` directory (you may not see it but you can check if it exists by running the below command). <br>
`vim .bashrc`

Add the alias shown above in the file then run `source .bashrc` to update the alias globally. Run `alias fct` to confirm that the alias was updated.

To run the previous help command you can then run `fct -h` in your terminal.

The rest of the documentation will assume you have configured an _alias_ for running the efsity jar with alias name `fct` as above.

**NB:** The _efsity_ jar is compatible from minimum version JAVA 11. If doesn't run on your workstation please build your own jar file using instructions in the [Building](#Building) section below.

### Converting structure map .txt to .json
To convert your `structure-map.txt` file to its corresponding `.json` file, you can run the command
```console
$ fct convert -t sm --input ~/Workspace/fhir-resources/coda/structure_map/coda-child-structure-map.txt
```

### Converting library .cql to .json
To covert a `library.cql` file to a `library.cql.fhir.json` file you can run the command
```console
$ fct convert -t cql -i /some/path/Patient-1.0.0.cql
```

**Options**
```
-t or --type - the type of conversion, can be sm for structure map to json or cql for cql to json library
-i or --input - the input file or file path
-o or --output - the output path, can be a file or directory. Optional - default is current directory
-sm or --strict-mode - (Optional) whether to enable or disable strict CQL compiler validation for generated CQL json. Optional boolean - default is `true`
```

### Generating a Careplan for a subject
To generate a Careplan, you need to provide the _subject_, the _plan definition_ and the _questionnaire response_. You can optionally provide an output path. The output will be a bundle containing the generated Careplan and list of Tasks. To generate a Careplan you can run the command:

```console
$ fct careplan -s /path/to/subject/e.g./patient.json -qr /path/to/qr/questionnaire-response.json -pd /path/to/plan/definition/plandefinition.json -sm /path/to/structuremap/folder/
```

**Options**
```
-s or --subject - file path to the Careplan subject
-qr or --questionnaire-response - file path to the questionnaire response json file
-sm or --structure-map - file path to the path to the folder containing the structure maps. These can be nested
-pd or --plan-definition - file path to the Plandefinition for the Careplan
-o or --output - the output path, can be a file or directory. Optional - default is current directory
-ws or --with-subject - A flag to determine whether the subject should be passed as part of the Careplan generator data bundle. Default is `false`
```

### Generating a QuestionnaireResponse for a Questionnaire
The tool supports generation of a QuestionnaireResponse via [openai api](https://platform.openai.com/docs/api-reference/introduction) using the [chat completions endpoint](https://platform.openai.com/docs/api-reference/chat/create). You will need to provide a [token](https://platform.openai.com/docs/api-reference/authentication) for authentication and a path to the _Questionnaire_

Optionally, you can also provide the [model](https://platform.openai.com/docs/models) you would like to use, the number of [max-token](https://platform.openai.com/docs/api-reference/completions/create#completions/create-max_tokens) and an output file path

```console
$ fct generateResponse -i /path/to/questionnaire.json -fs https://example.smartregister.org/fhir
```

**Options**
```
-i or --input - file path to the questionnaire
-gm or --generation-mode - the generation mode to be used. Default is set to 'populate', but can also be set 'ai'
-fs or --fhir-server - the FHIR server used to generate the questionnaire response
-k or --apiKey - api key to authenticate
-m or --model - (Optional) model you want to use. The default is `gpt-3.5-turbo-16k`
-t or --tokens - (Optional) max number of tokens to be used for the request. The default is 9000
-o or --output - (Optional) output file path, can be a file or a directory. The default is set to the current directory
```

### Extracting resources from questionnaire response
To extract FHIR Resources you can run the command:
```console
$ fct extract -qr /patient-registration-questionnaire/questionnaire-response.json -sm /path/to/patient-registration-questionnaire/structure-map.txt
```

**Options**
```
-qr or --questionnaire-response - the questionnaire response json file
-sm or --structure-map - the path to the structure map file
-o or --output - the output path, can be a file or directory. Optional - default is current directory
```

### Publish FHIR resources
To publish your FHIR resources run the command:

```console
$ fct publish -e /path/to/env.properties
```

**Options**
```
 -i or --input : Path to the project folder with the resources to be published
-bu or --fhir-base-url : The base url of the FHIR server to post resources to
 -c or --composition-file : The path to the composition file
-at or --access-token : Access token to grant access to the FHIR server
-ci or --client-id : The client identifier for authentication
-cs or --client-secret :The client secret for authentication
 -u or --username : The username for authentication
 -p or --password : The password for authentication
-au or --accessToken-url : The endpoint for the authentication server
 -g or --grant-type : The authorization code grant type
 -e or --env : A properties file that contains the neessary variables
 -vr or --validate-resources : (Optional) whether to validate FHIR resources before publishing or not. Optional boolean - default is `true`
```
You can either pass your variables on the CLI or include them in the properties file. Variables passed on CLI
take precedence over anything in the properties file.

You can either pass the actual accessToken as a variable or pass in the client credentials which will be used 
to get an accessToken from the accessToken url provided

You must pass the path to your composition file if you want to publish any binary resources. 
The binary resources listed in the composition files are the ones that will be published.
For the publishing of binary resources to work correctly, ensure that you are using the correct/recommended file/folder structure and that the file names in the composition file are in camel case.

### Validating your app configurations
The tool supports some validations for the FHIRCore app configurations. To validate you can run the command:
```console
$ fct validate -c ~/path/to/composition_config_file.json -i ~/Workspace/fhir-resources/<project>/app_configs/
```
The above will output a list of errors and warnings based on any configuration rules that have been violated.

**Options**
```
-c or --composition - the composition json file of the project
-i or --input - the input directory path. This should point to the folder with the app configurations e.g. ~/Workspace/fhir-resources/ecbis_cha_preview/
-o or --output - the output path, can be a file or directory. Optional - default is current directory
-sm or --structure-maps - the directory path to the location of structure map .txt or .map files. Optional - Must be a directory. Must be used with the -q flag
-q or --questionnaires - the directory path to the location of questionnaires .json files. Optional - Must be a directory. Must be used with the -sm flag
```

**Note:** To include _Questionnaire_ and _Structure Map_ validation add the `-sm` and `-q` flags

**Sample screenshot output**
<br/>
<img width="715" alt="Screenshot 2023-03-27 at 21 43 09" src="https://user-images.githubusercontent.com/10017086/228037581-209f9bab-d1b9-45eb-a920-aa12c70c5b98.png">

### Validating FHIR resources
The tool supports validating FHIR resources using FHIR's own conformance resources. It will use terminology services to validate codes, StructureDefinitions to validate semantics, and uses a customized XML/JSON parser in order to provide descriptive error messages. To do this run the command:
```console
$ fct validateFhir -i ~/Workspace/fhir-resources/<project>/<resource-type>/resource.json
```
The above will output a list of errors, warnings and information.

**Options**
```
-i or --input - the input file path, can be a file or directory with multiple files. Passing a path to a directory will automatically process all json files in the folder recursively
```

### Validating File Structure
The tool supports validation of a project file structure using JSON schema. To run the command:
```console
$ fct validateFileStructure -i ~/Workspace/fhir-resources/<project> -s ~/path/to/file/structure/schema.json
```

**Options**
```console
-i or --input - path to project folder which needs to be validated
-s or --schema - JSON schema that should be used to validate the file structure
```
If the file structure matches the schema then a positive result is printed to the terminal, otherwise an error 
is thrown showing the issue.

### Validating Questionnaires and  StructureMap 
The tool supports two validation modes:

**1.Single mode** - Validates a single StructureMap and questionnaire.
**2.Project mode** - Validates all project files, including multiple questionnaires and StructureMaps.

The tool maps each questionnaire to the specified StructureMap, then generates the corresponding `QuestionnaireResponse`. Once generated, a bundle is created with the related resources (e.g., Patient, Condition).

To run the tool, ensure the following:

1. Install h`api=fhir-jpaserver-starter`.
2. In `src/main/resources/application.yaml`, set `hapi: fhir`: to `true`.
3. Run the command: `mvn spring-boot:run`

The server will be accessible at http://localhost:8080/fhir, and the `CapabilityStatement` can be found at http://localhost:8080/fhir/metadata.
**Options**
```
-i or --input            Path to the project questionnaire folder to be validated.
-q or --questionnaire     Path to a single questionnaire file to be validated.
-v or --validate          (Optional) Boolean flag to validate FHIR resources before publishing. Default is `false`.
-sm or --structure-map    Path to the folder containing StructureMaps. Nested directories are supported.

```

```console (single-mode)
$  fct validateStructureMap -q ~/Workspace/fhir-resources/<project>/questionnaire-folder/resource.json -sm ~/Workspace/fhir-resources/coda/structure_map-folder/coda-child-structure-map.txt
```

```console (project-mode)
$  fct validateStructureMap -i ~/Workspace/fhir-resources/<project>/questionnaire-folder/ -sm ~/Workspace/fhir-resources/coda/structure_map-folder/`
```

### Localization
Tool that supports localization by the use of the translation extension

#### 1. Extraction
It extracts specific fields from the specific resource provided or from all resources in the directory provided and generates a `strings_default.properties` document in the `translation` folder by default or in the file provided using the `-tf` flag.
Performs best if project is consistent with this [structure](https://docs.google.com/document/d/1Seoo9YYDBI87lmkA5siNqWsdYIgiwE_EYZ6relz8V14/edit#heading=h.qqxoq1r6u4zf)
```console
$ fct translate -m extract -rf ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content -tf ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content/translation/strings_default.properties
```
The above extract all [text and display](https://github.com/onaio/fhir-tooling/blob/main/efsity/src/main/java/org/smartregister/util/FCTConstants.java#L8) fields from the `fhir_content` directory and creates a `strings_default.properties` file in the specified directory.

**Options**
```
-m or --mode - the options are either `extract` to generate the translation file from a questionnaire or `merge` to import a translated file and populate the original questionnaire
-rf or --resourceFile path to the resource file or the folder containing the resource files
-tf or --translationFile (Optional during extraction unless extraction is being done on a specific file) this is path to the string_default.properties file. If not provided it defaults to ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content/translation/strings_default.properties
-et or --extractionType (Optional except when performing extraction for an entire directory) the options are either `all` to perform an extraction of an entire directory, `configs` to perform an exraction of configs or `fhirContent` to perform extraction on questionnaires.
```

**Examples**
```agsl
// extract content
$ fct translate -m extract -rf ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content
or 
$ fct translate -m extract -rf ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content -et fhirContent
or
$ fct translate -m extract -rf ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content/questionnaires/content.json -et fhirContent -tf ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content/translation/strings_default.properties

// extract configs
$ fct translate -m extract -rf ~/Workspace/fhir-resources/<project>/<environment>/<app>/configs
or 
$ fct translate -m extract -rf ~/Workspace/fhir-resources/<project>/<environment>/<app>/configs -et configs
or
$ fct translate -m extract -rf ~/Workspace/fhir-resources/<project>/<environment>/<app>/configs/profile/config.json -et configs -tf ~/Workspace/fhir-resources/<project>/<environment>/<app>/configs/translation/strings_config.properties

// extract configs and content from entire project
$ fct translate -m extract -rf ~/Workspace/fhir-resources/<project>/<environment>/<app>
or 
$ fct translate -m extract -rf ~/Workspace/fhir-resources/<project>/<environment>/<app> -et all

```
Extracts content from specified directory or file and populates the specified translation file or default translation files location consistent with this [format](https://docs.google.com/document/d/1Seoo9YYDBI87lmkA5siNqWsdYIgiwE_EYZ6relz8V14/edit#heading=h.qqxoq1r6u4zf)

#### 2. Merging
It merges specific fields from the specific translation file provided to a resource or resources in the directory provided. 
Performs best if project is consistent with this [structure](https://docs.google.com/document/d/1Seoo9YYDBI87lmkA5siNqWsdYIgiwE_EYZ6relz8V14/edit#heading=h.qqxoq1r6u4zf)
```console
$ fct translate -m merge -rf ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content -tf ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content/translation/strings_fr.properties -l fr
or
$ fct translate -m merge -rf ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content/questionnaires -tf ~/Workspace/fhir-resources/<project>/<environment>/<app>/fhir_content/translation/strings_fr.properties -l fr

```

**Options**
```
-m or --mode - the options are either `extract` to generate the translation file from a questionnaire or `merge` to import a translated file and populate the original questionnaire
-rf or --resourceFile path to the resource file or the folder containing the resource files
-tf or --translationFile this is path to the string_fr.properties file.
-l or --locale (Optional if it can be derived from the properties file). The translation locale
```

### Update Location Lineage Tags
To update locations lineage tags run the command:

```console
$ fct updateLocationLineage -e /path/to/env.properties -ids /path/to/location_ids.txt -bu https://base_url.org/fhir
```

**Options**
```
 -ids or --location-ids : Path to the file containing the location ids
-bu or --fhir-base-url : The base url of the FHIR server to post resources to
-at or --access-token : Access token to grant access to the FHIR server
-ci or --client-id : The client identifier for authentication
-cs or --client-secret :The client secret for authentication
 -u or --username : The username for authentication
 -p or --password : The password for authentication
-au or --accessToken-url : The endpoint for the authentication server
 -g or --grant-type : The authorization code grant type
 -e or --env : A properties file that contains the neessary variables
```
You can either pass your variables on the CLI or include them in the properties file. Variables passed on CLI
take precedence over anything in the properties file.

You can either pass the actual accessToken as a variable or pass in the client credentials which will be used
to get an accessToken from the accessToken url provided

You must pass the path to your location ids file. The file contents should be in the format below:
```commandline
loc-1-id
loc-2-id
loc-3-id
```
The format of the env.properties file should be as below:
```commandline
fhirBaseUrl=<fhir_base_url>
accessToken=<access_token>
clientId=<client_id>
clientSecret=<client_secret>
username=<username>
password=<password>
accessTokenUrl=<access_toke_url>
grantType=<grant_type>
```
## Development
### Set up
This is a Java + Kotlin gradle project. You can import it in you JetBrains IntelliJ IDE as such. The utility is built on the very awesome `Picocli` library found here https://picocli.info/.

### Contributing
Before you push the code remember to run the spotless plugin to format the changed files

```console
./gradlew spotlessApply
```

### Building
To build and create a new jar file run the gradle **assemble** command

```console
./gradlew clean assemble
```

The jar will be created in the path `build/libs/fhircore-tooling-<version>-SNAPSHOT-release.jar`

### Publishing
To publish a snapshot of the jar file to _Sonatype_ , run the following gradle command. 

```console
./gradlew clean publishMavenPublicationToSonatypeRepository
```

Note for this to work you need you sonatype account and credentials set up. To set up your credentials, create a file in your user home gradle folder

```properties
# ~/.gradle/gradle.properties
SonatypeUsername=<your sonatype username>
SonatypePassword=<your sonatype password>
```

**Note:** For development purposes you can publish to maven local using the command

```console
./gradlew clean publishMavenPublicationToMavenLocal
```

### Snapshot Artifact Releases

To publish an artifact snapshot automatically, all you need to do is push a new tag, which then triggers the CI to generate and publish it. The configuration requires the tag to have a prefix in the glob pattern format:

**v[0-9]+.[0-9]+.[0-9]+[0-9a-zA-Z.-]+-SNAPSHOT**

For example the following are all valid tags that will trigger the generation of a release

v2.0.0-SNAPSHOT

v2.0.0-efsity-SNAPSHOT

v2.0.0-efsity-rc1-SNAPSHOT


_Note_: when creating a tag for efsity version 2.0.0 for example, use the command:

`git tag -a v2.0.0-efsity -s  && git push origin v2.0.0-efsity`

### Testing
To run all tests:

```console
./gradlew test
```
To run some tests
```console
./gradlew test --test <fully qualified name glob>
#example
./gradlew test --test com.example.TestClassName.testMethodName
```

To run tests and generate a coverage report:

```console
./gradlew test jacocoTestReport
```

A report will be generated at `$buildDir/reports/jacoco/test`