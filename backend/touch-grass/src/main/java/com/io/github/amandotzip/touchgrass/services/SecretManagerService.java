package com.io.github.amandotzip.touchgrass.services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;

public class SecretManagerService {

    private final AWSSecretsManager secretsManagerClient;

    public SecretManagerService() {
        // Initialize the Secrets Manager client with a region
        this.secretsManagerClient = AWSSecretsManagerClientBuilder.standard()
                .withRegion(Regions.US_EAST_1) // Specify the region
                .build();
    }

    /**
     * Retrieves a secret value from AWS Secrets Manager
     * @param secretName The name of the secret to retrieve
     * @return The secret value
     */
    public String getSecretValue(String secretName) {
        // Create a request for the secret
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        
        // Retrieve the secret value
        GetSecretValueResult getSecretValueResult = secretsManagerClient.getSecretValue(getSecretValueRequest);
        
        // Return the secret value
        return getSecretValueResult.getSecretString();
    }
}
