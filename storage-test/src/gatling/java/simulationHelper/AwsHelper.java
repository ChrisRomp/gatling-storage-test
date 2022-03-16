package simulationHelper;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import io.gatling.javaapi.core.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

public class AwsHelper {
    enum OperationType {
        GET, PUT
    }

    public static String GetS3PresignedGetUrl(S3Presigner presigner, String bucketName, String keyName) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(12))
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    public static String GetS3PresignedPutUrl(S3Presigner presigner, String bucketName, String keyName) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .contentType("application/octet-stream")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(12))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toString();
    }

    static ArrayList<Map<String, Object>> GetFilesMapList (int iterationCount, String folderPath, String[] filesArray, S3Presigner presigner, String bucketName, OperationType operationType) {
        // Generate files list with presigned URLs
        ArrayList<Map<String, Object>> fileMapList = new ArrayList<Map<String, Object>>();

        for (String file : filesArray) {
            for (int i = 0; i < iterationCount; i++) {
                String fileName = file + "." + i;
                String filePath = folderPath + "/" + fileName;

                // Generate signature URL for each file
                String url = (operationType == OperationType.GET)
                    ? GetS3PresignedGetUrl(presigner, bucketName, filePath)
                    : GetS3PresignedPutUrl(presigner, bucketName, filePath);

                Map<String, Object> fileMap = new HashMap<String, Object>();
                fileMap.put("fileType", file);
                fileMap.put("fileName", fileName);
                fileMap.put("url", url);
                fileMapList.add(fileMap);
            }
        }

        return fileMapList;
    }

    public static ScenarioBuilder GetAwsPresignedSequencedDownloadScenario(String scenarioName, int iterationCount, String commonQuery, String folderPath, String[] filesArray, S3Presigner presigner, String bucketName) {

        // Generate files list with presigned URLs
        ArrayList<Map<String, Object>> fileMapList = GetFilesMapList(iterationCount, folderPath, filesArray, presigner, bucketName, OperationType.GET);

        ScenarioBuilder scn = scenario(scenarioName)
            .repeat(fileMapList.size(), "count").on(
                feed(fileMapList.iterator())
                    .exec(http("#{fileType}" + "-Download")
                        .get("#{url}" + commonQuery)
                        .header("x-amz-te", "append-md5") // Added by presigner; has to match
                        .check(status().is(200))
                    )
            );

        return scn;
    }

    public static ScenarioBuilder GetAwsPresignedSequencedUploadScenario(String scenarioName, int iterationCount, String commonQuery, String folderPath, String[] filesArray, String sourcePath, Map<String, String> headers, S3Presigner presigner, String bucketName) {

        String localSourcePath = sourcePath.isEmpty() ? "files" : sourcePath;

        // Generate files list with presigned URLs
        ArrayList<Map<String, Object>> fileMapList = GetFilesMapList(iterationCount, folderPath, filesArray, presigner, bucketName, OperationType.PUT);

        ScenarioBuilder scn = scenario(scenarioName)
            .repeat(fileMapList.size(), "count").on(
                feed(fileMapList.iterator())
                // .foreach(fileMapList, "file").on(
                    .exec(http("#{fileType}" + "-Upload")
                        .put("#{url}" + commonQuery)
                        .headers(headers)
                        .body(RawFileBody(localSourcePath + "/#{fileName}"))
                        .check(status().is(200))
                    )
            );

        return scn;
    }
}
