# StructureMap tooling

The StructureMap Tooling is a utility designed to generate Structure Maps for FHIR (Fast Healthcare Interoperability Resources) transformations. Structure Maps are used to define how one FHIR resource is transformed into another. This tooling leverages the HL7 FHIRPath language to provide support for creating and managing these transformations.

## Features
**FHIRPath Engine**: Utilizes the FHIRPath engine to evaluate and transform FHIR resources.

**Transformation Support**: Provides services to support the transformation process, ensuring that resources are accurately converted according to the specified Structure Map.

**Automation**: Simplifies the process of generating Structure Maps through automated tooling.

## Files
**FhirPathEngineHostServices.kt**: Contains services for hosting and running the FHIRPath engine.

**Main.kt**: The entry point of the application. It orchestrates the process of reading input, processing it through the FHIRPath engine, and generating the Structure Map.

**TransformSupportServices.kt**: Provides additional support services required for the transformation process.

**Utils.kt**: Contains the main logic for generating the Structure Maps using the FHIRPath engine and transformation support services.

## Prerequisites
- Questionnaire JSON
- XLS form with the required information based on the questionnaire
### Installation
1. Clone the Repository:

```console
git clone https://github.com/your-repo/structuremap-tooling.git
cd structuremap-tooling
```
2. Once the `structuremap-tooling` folder, click on run.
3. A prompt will appear on the CLI `Kindly enter the XLS filepath`: Enter the absolute path of the file's location. Click `Enter`
4. Another prompt will appear on the CLI `Kindly enter the questionnaire filepath`: Enter the absolute path of the file's location. Click `Enter`
5. The structureMap will be generated in the CLI and two complete files in the folder containing the `.json` and `.map` files

## Contributing
Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.