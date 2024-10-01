package com.io.github.amandotzip.touchgrass.configurations;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Configuration {

    boolean localTesting = true;
    @Bean
    public AmazonS3 s3Client() {
        if (localTesting) {
            //credentials needed for local testing, not for production. AWS provides credentials automatically
            String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
            String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
            return AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1")  // Specify your region
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        }

        else{
            return AmazonS3ClientBuilder.standard()
            .withRegion("us-east-1")  // Specify your region
            .build();
        }
    }
}