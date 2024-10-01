package com.io.github.amandotzip.touchgrass.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class S3Service {

    private AmazonS3 s3Client;

    @Autowired
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
        // System.out.println(fullPath);
        // Split the fullPath into bucket name and object key
        // String[] parts = fullPath.split("/", 2);  // Split into 2 parts: bucketName and objectKey (or directory)
        
        // if (parts.length < 2) {
        //     throw new IllegalArgumentException("Invalid S3 path. It must contain both the bucket name and object key.");
        // }


        // String bucketName = parts[0];  // The bucket name is the first part
        // String directory = parts[1];   // The "directory" or object key is the second part
        // System.out.println(bucketName);
        // System.out.println(directory);

        // ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(directory);

        String fullPath = "touch-grass-bucket/main-gallery";

        ListObjectsV2Request request = new ListObjectsV2Request().withBucketName("touch-grass-bucket").withPrefix("main-gallery");
        ListObjectsV2Result result = s3Client.listObjectsV2(request);

        return result.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)  // Get object key
                .map(key -> s3Client.getUrl("touch-grass-bucket", key).toString())  // Get URL for each object
                .collect(Collectors.toList());
    }



}
