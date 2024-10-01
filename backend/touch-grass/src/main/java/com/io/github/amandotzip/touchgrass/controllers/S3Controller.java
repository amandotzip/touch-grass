package com.io.github.amandotzip.touchgrass.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.List;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.io.github.amandotzip.touchgrass.services.S3Service;



@RestController
public class S3Controller {

    @Autowired
    private AmazonS3 s3Client;
    @Autowired
    private S3Service s3Service;

    private final String bucketName = "touch-grass-bucket";
    private final String directoryName = "main-gallery";

    //must implement AWS Rekog to check for nsfw images


    @CrossOrigin(origins = "http://localhost:8080") // Allow requests from your Angular app
    @GetMapping("/generate-presigned-url")
    public String generatePresignedUrl(@RequestParam String fileName) {
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
        
        return s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();

    }

    @CrossOrigin(origins = "http://localhost:8080") // Allow requests from your Angular app
    @GetMapping("/get-s3-images")
    public List<String> getS3Images() {
        // System.out.println(bucketName);
        return s3Service.getBucketImages();
    }




}
