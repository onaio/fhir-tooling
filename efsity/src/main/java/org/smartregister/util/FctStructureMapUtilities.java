/* (C)2023 */
package org.smartregister.util;

import java.io.IOException;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.context.IWorkerContext;
import org.hl7.fhir.r4.context.SimpleWorkerContext;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StructureMap;
import org.hl7.fhir.r4.utils.StructureMapUtilities;
import org.hl7.fhir.utilities.npm.FilesystemPackageCacheManager;
import org.hl7.fhir.utilities.npm.ToolsVersion;
import org.smartregister.domain.FctFile;
import org.smartregister.external.TransformSupportServices;

/** Wrapper around the org.hl7.fhir.r4.utils.StructureMapUtilities class */
public class FctStructureMapUtilities {
  private IWorkerContext context;
  private StructureMapUtilities structureMapUtilities;

  public FctStructureMapUtilities() throws IOException {

    FilesystemPackageCacheManager pcm =
        new FilesystemPackageCacheManager(true);

    // Package name manually checked from
    // https://simplifier.net/packages?Text=hl7.fhir.core&fhirVersion=All+FHIR+Versions
    SimpleWorkerContext simpleWorkerContext =
        SimpleWorkerContext.fromPackage(
            pcm.loadPackage(
                FctUtils.Constants.HL7_FHIR_PACKAGE, FctUtils.Constants.HL7_FHIR_PACKAGE_VERSION));
    simpleWorkerContext.setExpansionProfile(new Parameters());
    simpleWorkerContext.setCanRunWithoutTerminology(true);

    TransformSupportServices transformSupportServices =
        new TransformSupportServices(simpleWorkerContext);

    this.structureMapUtilities =
        new StructureMapUtilities(simpleWorkerContext, transformSupportServices);
    this.context = simpleWorkerContext;
  }

  public IWorkerContext getSimpleWorkerContext() {
    return context;
  }

  public void transform(Object appInfo, Base source, StructureMap map, Base target)
      throws FHIRException {
    this.structureMapUtilities.transform(appInfo, source, map, target);
  }

  public StructureMap parse(String text, String srcName) throws FHIRException {
    return this.structureMapUtilities.parse(text, srcName);
  }

  public StructureMap getStructureMap(String structureMapFilePath) throws IOException {
    FctFile structureMapFile = FctUtils.readFile(structureMapFilePath);
    return structureMapUtilities.parse(
        structureMapFile.getContent(),
        FctUtils.getStructureMapName(structureMapFile.getFirstLine()));
  }
}
