package com.example.cloudgallery;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LambdaService {

    private final AWSLambda lambdaClient;

    @Value("${aws.lambda.function}")
    private String functionName;

    public LambdaService(@Value("${aws.accessKey}") String accessKey,
            @Value("${aws.secretKey}") String secretKey,
            @Value("${aws.region}") String region) {
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
        this.lambdaClient = AWSLambdaClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .withRegion(region)
                .build();
    }

    public void triggerAudit(Long imageId) {
        try {
            String payload = "{ \"imageId\": " + imageId + ", \"action\": \"AUDIT_REQUEST\" }";

            InvokeRequest request = new InvokeRequest()
                    .withFunctionName(functionName)
                    .withPayload(payload);

            InvokeResult result = lambdaClient.invoke(request);
            System.out.println("Lambda invocada! Status: " + result.getStatusCode());
        } catch (Exception e) {
            System.err.println("Erro ao chamar Lambda: " + e.getMessage());
        }
    }
}
