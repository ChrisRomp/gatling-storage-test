package simulationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class TestHelper {

    public static HttpProtocolBuilder GetHttpConfig(String baseUrl) {
        HttpProtocolBuilder httpProtocol = http
            .baseUrl(baseUrl) // Here is the root for all relative URLs
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
            .disableCaching();
        
        return httpProtocol;
    }

    public static List<Map<String, Object>> GetFiles(int iterationCount, String fileListName) {

        List<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < iterationCount; i++) {
            fileList.addAll(csv(fileListName).readRecords());
        }

        return fileList;
    }

    public static ScenarioBuilder GetScenario(String scenarioName, int iterationCount, String commonQuery, String fileListName) {
        List<Map<String, Object>> fileList = GetFiles(iterationCount, fileListName);
        int fileCount = fileList.size();

        ScenarioBuilder scn = scenario(scenarioName) // A scenario is a chain of requests and pauses
            .repeat(fileCount).on( // Number of times to run the series
                feed(fileList.iterator())
                    .exec(http("#{name}").get("#{uri}" + commonQuery))
                    .exec(flushHttpCache()))
            .pause(1);
        
        return scn;
    }
}
