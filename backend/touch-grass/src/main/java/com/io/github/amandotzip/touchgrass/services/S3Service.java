package com.io.github.amandotzip.touchgrass.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.rekognition.model.ModerationLabel;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.io.github.amandotzip.touchgrass.utils.ContentModerator;
import com.io.github.amandotzip.touchgrass.utils.ImageAnalyzer;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 s3Client;




    private final String BUCKET_NAME = "touch-grass-bucket";
    private final String GALLERY_KEY_NAME = "main-gallery";
    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";



    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }


    
    /**
     * Process an image (upload to S3, analyze with AWS Rekognition, content moderate with AWS Rekognition)
     * @param file the image file to process
     * @return a ResponseEntity containing an appropriate HTTP status and a message
     */
    public ResponseEntity<String> processImage(MultipartFile file) {
        System.out.println("Processing image: " + file.getOriginalFilename());

        // Define a unique file name by appending a UUID or timestamp to the original filename
        String uniqueFileName = file.getOriginalFilename().replace(".", "_" + UUID.randomUUID() + ".");
        // Define the S3 object key (file name)
        String imageKeyString = GALLERY_KEY_NAME + '/' + uniqueFileName;


        // Step 1: Upload image to S3
        ResponseEntity<String> s3Response = uploadToS3Bucket(file, imageKeyString);

        // Check if the response is not OK
        if (!s3Response.getStatusCode().is2xxSuccessful()) {
            // If the status code is not OK (2xx), return the same response
            return ResponseEntity.status(s3Response.getStatusCode()).body(s3Response.getBody());
        }

        // FUNCTIONING but usecase not needed atm
        // // Step 2: Analyze the image using AWS Rekognition (Label Detection)
        // ImageAnalyzer analyzer = new ImageAnalyzer();
        // analyzer.analyzeImage(BUCKET_NAME, imageKeyString);

        System.out.println("Content moderation");
        // Step 3: (Optional) Use Rekognition for content moderation (making sure it's not innapropriate)
        ContentModerator contentModerator = new ContentModerator();
        List<ModerationLabel> moderationLabels = contentModerator.moderateImageContent(BUCKET_NAME, imageKeyString);
        if (moderationLabels.size() > 0) {
            // Delete the object
            deleteS3Object(imageKeyString);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("AI Scan has flagged this image as innapropriate");
        }


        return ResponseEntity.ok("File uploaded successfully.");

    }

    private void deleteS3Object(String imageKey) {
        s3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, imageKey));
    }   

    public ResponseEntity<String> uploadToS3Bucket(MultipartFile file, String imageKeyString ) {
        System.out.println("Initial uploadToS3Bucket: " + file.getOriginalFilename());

        try {
            // Create object metadata, which is used to store additional information such as content length and type
            // ObjectMetadata metadata = getMetadata(file);

            System.out.println("put object");
            System.out.println(BUCKET_NAME);
            System.out.println(imageKeyString);
            

            // Upload the file to S3
            s3Client.putObject(new PutObjectRequest(BUCKET_NAME, imageKeyString, file.getInputStream(), null));

            // Return the public URL of the uploaded object
            String fileUrl = s3Client.getUrl(BUCKET_NAME, imageKeyString).toString();
            System.out.println("Initial file uploaded successfully");
            return ResponseEntity.ok("Initial file uploaded successfully. File URL: " + fileUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed!");
        }
        
    }


    // private ObjectMetadata getMetadata(MultipartFile file) {
    //     ObjectMetadata metadata = new ObjectMetadata();
    //     metadata.setContentLength(file.getSize());
    //     metadata.setContentType(file.getContentType());  // e.g., "image/jpeg", "image/png", "application/pdf"
    //     return metadata;
    // }

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
