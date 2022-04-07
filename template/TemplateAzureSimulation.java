package storageSimulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import static io.gatling.javaapi.core.CoreDsl.*;

import java.util.Map;
import java.util.HashMap;

import static simulationHelper.TestHelper.*;

public class AzureEastUs2PremiumSimulation extends Simulation {

  // Define how many times to run the sequence; default 200
  // Can be passed using -DiterationCount=X via CLI or JAVA_OPTS
  Integer iterationCount = Integer.getInteger("iterationCount", 200);

  // Scenario Name
  String scenarioName = "Storage-Test-Azure-Template";

  // Account Name
  String accountName = "";

  // Define the base URL for the test
  String baseUrl = "https://" + accountName + ".blob.core.windows.net/";

  // Use for appending SAS tokens, etc., to each call
  String commonQuery = System.getProperty("azureSasToken",
    System.getenv("AZURE_SAS_TOKEN")
  );

  // Upload headers
  static Map<String, String> uploadHeaders = new HashMap<>();
  static {
    uploadHeaders.put("content-type", "application/octet-stream");
    uploadHeaders.put("x-ms-blob-type", "BlockBlob");
    uploadHeaders.put("x-ms-version", "2021-04-10");
  }

  // Folder path (optional)
  long unixTime = System.currentTimeMillis() / 1000L;
  String folderPath = "perftest/perftest-" + unixTime;

  // Files source path and array
  String sourcePath = "/app/files";
  String[] filesArray = {"1MB", "2MB", "3MB", "4MB", "5MB", "10MB", "20MB", "50MB", "100MB"};

  // Set up and run simulation(s)
  HttpProtocolBuilder httpProtocol = GetHttpConfig(baseUrl);

  // Download scenario (to be chained after upload)
  ScenarioBuilder download = GetSequencedDownloadScenario(scenarioName + "-Download", iterationCount, commonQuery, folderPath, filesArray);

  // Upload scenario (runs first)
  ScenarioBuilder upload = GetSequencedUploadScenario(scenarioName + "-Upload", iterationCount, commonQuery, folderPath, filesArray, sourcePath, uploadHeaders);
  {
    setUp(upload.injectOpen(
      atOnceUsers(1)
    ).andThen(download.injectOpen(
      atOnceUsers(1))
    )).protocols(httpProtocol);
  }
}
