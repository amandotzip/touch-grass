package com.io.github.amandotzip.touchgrass.utils;

import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectModerationLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectModerationLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.ModerationLabel;
import com.amazonaws.services.rekognition.model.S3Object;

public class ContentModerator {

      private static final String REGION = "us-east-1"; // Change based on your region

    public List<ModerationLabel> moderateImageContent(String bucketName, String imageKey) {
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
                .withRegion(REGION)
                .build();

        DetectModerationLabelsRequest request = new DetectModerationLabelsRequest()
                .withImage(new Image().withS3Object(new S3Object().withBucket(bucketName).withName(imageKey)))
                .withMinConfidence(75F);

        DetectModerationLabelsResult result = rekognitionClient.detectModerationLabels(request);
        List<ModerationLabel> labels = result.getModerationLabels();

        System.out.println("Moderation labels for " + imageKey);
        for (ModerationLabel label : labels) {
            System.out.println(label.getName() + ": " + label.getConfidence().toString());
        }
            
        return labels;
    }

}
