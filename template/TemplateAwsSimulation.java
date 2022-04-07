package storageSimulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import static io.gatling.javaapi.core.CoreDsl.*;

import java.util.Map;
import java.util.HashMap;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.S3Configuration;

import static simulationHelper.TestHelper.*;
import static simulationHelper.AwsHelper.*;

public class TemplateAwsSimulation extends Simulation {

  // Define how many times to run the sequence; default 200
  // Can be passed using -DiterationCount=X via CLI or JAVA_OPTS
  Integer iterationCount = Integer.getInteger("iterationCount", 200);

  // Scenario Name
  String scenarioName = "Storage-Test-AWS-Template";

  // Bucket Name
  String bucketName = "";

  // Define the base URL for the test
  String baseUrl = ""; // for AWS this is set by URL builder in scenario

  // Use for appending SAS tokens, etc., to each call
  String commonQuery = "";

  // Upload headers
  static Map<String, String> uploadHeaders = new HashMap<>();
  static {
    uploadHeaders.put("content-type", "application/octet-stream");
  }

  // Folder path (optional)
  long unixTime = System.currentTimeMillis() / 1000L;
  String folderPath = "perftest-" + unixTime;

  // Files source path and array
  String sourcePath = "/app/files";
  String[] filesArray = {"1MB", "2MB", "3MB", "4MB", "5MB", "10MB", "20MB", "50MB", "100MB"};

  // AWS Configuration
  S3Configuration s3config = S3Configuration.builder()
    .accelerateModeEnabled(true) // Transfer Acceleration (TA)
    .build();
  S3Presigner presigner = S3Presigner.builder()
    .region(Region.US_EAST_1) // Set Region Here
    .serviceConfiguration(s3config)
    .build();

  // Set up and run simulation(s)
  HttpProtocolBuilder httpProtocol = GetHttpConfig(baseUrl);

  // Download scenario (to be chained after upload)
  ScenarioBuilder download = GetAwsPresignedSequencedDownloadScenario(scenarioName + "-Download", iterationCount, commonQuery, folderPath, filesArray, presigner, bucketName);

  // Upload scenario (runs first)
  ScenarioBuilder uploadDownload = GetAwsPresignedSequencedUploadScenario(scenarioName + "-Upload", iterationCount, commonQuery, folderPath, filesArray, sourcePath, uploadHeaders, presigner, bucketName);
  {
    setUp(
      uploadDownload.injectOpen(
        atOnceUsers(1)
      ).andThen(download.injectOpen(
        atOnceUsers(1)
      ))
    ).protocols(httpProtocol);
  }

  @Override
  public void after() {
    // Clean up presigner
    presigner.close();
  }
}
