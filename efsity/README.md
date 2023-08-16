# efsity
A command line utility to support OpenSRP v2 (FHIRCore) app configs and content authoring. This tool supports the HL7 FHIR R4 spec.

## How to use it

Download the latest release from https://github.com/opensrp/fhircore-tooling/releases

To run it as a java jar by using the command `java -jar efsity-2.2.0.jar -h` . This is the help command and will list the available options.

If you are using a linux environment e.g. bash you can choose to create an _alias_ for this as shown below. _(Remember to reload the terminal)_

`alias fct='java -jar ~/Downloads/efsity-2.2.0.jar'`

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

## Development
### Set up
This is a Java + Kotlin maven project. You can import it in you JetBrains IntelliJ IDE as such. The utility is built on the very awesome `Picocli` library found here https://picocli.info/.

### Building
To build and create a new jar file run the maven package command

`mvn spotless:apply package`
