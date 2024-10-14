package com.io.github.amandotzip.touchgrass.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.io.github.amandotzip.touchgrass.services.S3Service;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URL;


@RestController
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    private static final int MAX_UPLOADS_PER_MINUTE = 5;
    private static final long TIME_WINDOW = TimeUnit.MINUTES.toMillis(1);
    // To track uploads per user (you can use IP address, session ID, or user ID as the key)
    private Map<String, UploadRecord> uploadAttempts = new ConcurrentHashMap<>();


    @PostMapping("/upload")
    public ResponseEntity<String> uploadToGallery(@RequestParam("file") MultipartFile file, 
    @RequestParam("recaptchaToken") String recaptchaToken, 
    HttpServletRequest request) {
        s3Service.validateCaptcha(recaptchaToken);

        String userIp = getClientIp(request);
        if (isRateLimited(userIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded. Try again later.");
        }
        return s3Service.processImage(file);
    }


    @GetMapping("/get-s3-images")
    public List<String> getS3Images() {
        
        return s3Service.getBucketImages();
    }


    // Helper method to check if the user has exceeded the rate limit
    private boolean isRateLimited(String userIp) {
        UploadRecord record = uploadAttempts.getOrDefault(userIp, new UploadRecord(0, System.currentTimeMillis()));
        
        if (System.currentTimeMillis() - record.timestamp > TIME_WINDOW) {
            // Reset the counter after the time window has passed
            record = new UploadRecord(0, System.currentTimeMillis());
        }

        if (record.attempts >= MAX_UPLOADS_PER_MINUTE) {
            return true;
        }

        // Update the record and store it back
        record.attempts++;
        uploadAttempts.put(userIp, record);
        return false;
    }

    // Helper class to track upload attempts per user
    private static class UploadRecord {
        int attempts;
        long timestamp;

        public UploadRecord(int attempts, long timestamp) {
            this.attempts = attempts;
            this.timestamp = timestamp;
        }
    }

        // Helper method to extract client IP address from request
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        // X-Forwarded-For can contain a comma-separated list of IPs, we take the first one (client's IP)
        return xfHeader.split(",")[0];
    }

}
