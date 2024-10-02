package com.io.github.amandotzip.touchgrass.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.Date;
import java.util.Map;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 s3Client;

    private final String bucketName = "touch-grass-bucket";
    private final String directoryName = "main-gallery";

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }


    /**
     * Retrieves all image URLs from a given S3 bucket.
     *
     * @param fullPath the name of the S3 bucket
     * @return List of image URLs
     */
    public List<String> getBucketImages() {
        ListObjectsV2Request request = new ListObjectsV2Request().withBucketName("touch-grass-bucket").withPrefix("main-gallery");
        ListObjectsV2Result result = s3Client.listObjectsV2(request);

        return result.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)  // Get object key
                .filter(key -> !key.endsWith("/"))
                .map(key -> s3Client.getUrl("touch-grass-bucket", key).toString())  // Get URL for each object
                .collect(Collectors.toList());
    }


    public URL generatePresignedUrl(String fileName) {
        // Create expiration for temporary presigned url
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 5; // 5 minutes
        expiration.setTime(expTimeMillis);

        String objectKey = directoryName + "/" + fileName;
        // Create a pre-signed URL for PUT request
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucketName, objectKey)
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration);

        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return url;

    }

    /**
     * Verify reCAPTCHA response with Google
     * @param recaptchaToken
     */
    public void validateCaptcha(String recaptchaToken) {
        RestTemplate restTemplate = new RestTemplate();
        String secretValue = getGoogleRecaptchaSecret();

        String verifyUrl = String.format("%s?secret=%s&response=%s", RECAPTCHA_VERIFY_URL, secretValue, recaptchaToken);
        Map<String, Object> response = restTemplate.postForObject(verifyUrl, null, Map.class);
        boolean captchaSuccess = (boolean) response.get("success");
        
        if (!captchaSuccess) {
            throw new RuntimeException("reCAPTCHA validation failed.");
        }
    }
    
    private String getGoogleRecaptchaSecret() {
        SecretManagerService secretManagerService = new SecretManagerService();

        String secretValueJsonString = secretManagerService.getSecretValue("google-recaptcha-api-secret");

        // Parse the JSON string
        JSONObject jsonObject = new JSONObject(secretValueJsonString);
        
        // Extract the value of "recaptcha-secret"
        String secretValue = jsonObject.getString("recaptcha-secret");
        return secretValue;
    }


}
