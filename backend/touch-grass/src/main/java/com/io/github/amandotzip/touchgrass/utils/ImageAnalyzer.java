package com.io.github.amandotzip.touchgrass.utils;

import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;

public class ImageAnalyzer {


      private static final String REGION = "us-east-1"; // Change based on your region

    public void analyzeImage(String bucketName, String imageKey) {
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
                .withRegion(REGION)
                .build();

        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image().withS3Object(new S3Object().withBucket(bucketName).withName(imageKey)))
                .withMaxLabels(10)
                .withMinConfidence(75F);

        DetectLabelsResult result = rekognitionClient.detectLabels(request);
        List<Label> labels = result.getLabels();

        System.out.println("Detected labels for " + imageKey);
        for (Label label : labels) {
            System.out.println(label.getName() + ": " + label.getConfidence().toString());
        }
    }

}
