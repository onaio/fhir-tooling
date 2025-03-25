package org.smartregister.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;
import org.hl7.fhir.r4.model.Location;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.smartregister.helpers.LocationHelper;
import org.smartregister.util.FctUtils;

public class UpdateLocationLineageCommandTest {

  private UpdateLocationLineageCommand updateLocationLineageCommand;

  @TempDir static Path tempDir;

  @BeforeEach
  public void setUp() {
    updateLocationLineageCommand = new UpdateLocationLineageCommand();
  }

  @Test
  public void testSetPropertiesFromFile() throws IOException {
    Path propertiesFile = tempDir.resolve("env.properties");
    String content = "locationIdsFile=ids.txt\nfhirBaseUrl=http://fhir.test";
    Files.writeString(propertiesFile, content, StandardOpenOption.CREATE);

    Properties properties = FctUtils.readPropertiesFile(propertiesFile.toString());
    updateLocationLineageCommand.setProperties(properties);

    Assert.assertEquals("ids.txt", updateLocationLineageCommand.locationIdsFile);
    Assert.assertEquals("http://fhir.test", updateLocationLineageCommand.fhirBaseUrl);
  }

  @Test
  public void testRunWithValidLocationIdsCallsUpdateLocationLineage() throws IOException {
    Path locationIdsFile = tempDir.resolve("location_ids.txt");
    Files.write(locationIdsFile, List.of("loc1"), StandardOpenOption.CREATE);
    IGenericClient iGenericClient = mock(IGenericClient.class);

    updateLocationLineageCommand.locationIdsFile = locationIdsFile.toString();
    updateLocationLineageCommand.fhirBaseUrl = "http://fhir.test";
    updateLocationLineageCommand.accessToken = "token123";

    Location location = new Location();
    location.setId("Location/loc1");

    try (MockedStatic<LocationHelper> locationHelperMock =
        Mockito.mockStatic(LocationHelper.class)) {
      locationHelperMock
          .when(() -> LocationHelper.updateLocationLineage(any(), any()))
          .thenReturn(location);
      updateLocationLineageCommand.run();

      locationHelperMock.verify(() -> LocationHelper.updateLocationLineage(any(), any()));
    }
  }
}
