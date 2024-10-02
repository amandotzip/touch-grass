package com.io.github.amandotzip.touchgrass.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.amazonaws.services.s3.AmazonS3;
import com.io.github.amandotzip.touchgrass.services.S3Service;
import java.net.URL;


@RestController
public class S3Controller {

    @Autowired
    private AmazonS3 s3Client;
    @Autowired
    private S3Service s3Service;

    // @Value("${local-testing}")
    // private String localTesting;


    //must implement AWS Rekog to check for nsfw images


    @CrossOrigin(origins = "http://localhost:8080") // Allow requests from your Angular app
    @GetMapping("/generate-presigned-url")
    public String generatePresignedUrl(@RequestParam String fileName, @RequestParam String recaptchaToken) {
        s3Service.validateCaptcha(recaptchaToken);

        URL presignedUrl = s3Service.generatePresignedUrl(fileName);
        return presignedUrl.toString();
    }

    @CrossOrigin(origins = "http://localhost:8080") // Allow requests from your Angular app
    @GetMapping("/get-s3-images")
    public List<String> getS3Images() {
        return s3Service.getBucketImages();
    }




}
