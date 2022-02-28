package storageSimulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import static io.gatling.javaapi.core.CoreDsl.*;

import static simulationHelper.TestHelper.*;

public class TemplateSimulation extends Simulation {

  // Define how many times to run the sequence; default 200
  // Can be passed using -DiterationCount=X via CLI or JAVA_OPTS
  Integer iterationCount = Integer.getInteger("iterationCount", 200);

  // Scenario Name
  String scenarioName = "Storage-Test-Name";

  // Define the base URL for the test
  String baseUrl = "https://url/container";

  // Use for appending SAS tokens, etc., to each call
  String commonQuery = "?sastoken";

  // File list for downloading - must be located in resources folder
  String fileListName = "fileList.csv";

  // Set up and run simulation
  HttpProtocolBuilder httpProtocol = GetHttpConfig(baseUrl);
  ScenarioBuilder scn = GetScenario(scenarioName, iterationCount, commonQuery, fileListName);
  {
    setUp(scn.injectOpen(atOnceUsers(1)).protocols(httpProtocol));
  }
}
