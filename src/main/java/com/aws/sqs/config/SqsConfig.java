package com.aws.sqs.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SqsConfig {
    @Autowired
    LocalServiceUri elasticMqLocalSqsUri;
    @Autowired
    AWSCredentials awsCredentials;
    @Autowired
    private Environment env;

    @Bean
    @DependsOn("sqsRestServer")
    public QueueMessagingTemplate queueMessagingTemplate(UriComponents elasticMqLocalSqsUriComponents) {
        QueueMessagingTemplate queueMessagingTemplate;
        String[] activeProfiles = env.getActiveProfiles();
        List<String> profiles = Arrays.asList(activeProfiles);
        if (profiles.contains("local")) {
            queueMessagingTemplate = new QueueMessagingTemplate(buildAmazonSQSAsyncLocal(elasticMqLocalSqsUriComponents));
            return queueMessagingTemplate;
        }
        return new QueueMessagingTemplate(buildAmazonSQSAsync());
    }

    private AmazonSQSAsync buildAmazonSQSAsyncLocal(UriComponents elasticMqLocalSqsUriComponents) {
        AmazonSQSAsyncClient amazonSQSAsyncClient = new AmazonSQSAsyncClient(new BasicAWSCredentials("x", "x"));
        amazonSQSAsyncClient.setEndpoint(elasticMqLocalSqsUriComponents.toUriString());
        amazonSQSAsyncClient.createQueue("jagadheesh-queue");
        return amazonSQSAsyncClient;
    }

    private AmazonSQSAsync buildAmazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(awsCredentials.getAccessKey(), awsCredentials.getSecretKey())
                ))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    @Bean
    SQSRestServer sqsRestServer(UriComponents elasticMqLocalSqsUriComponents) {
        return SQSRestServerBuilder
                .withInterface(elasticMqLocalSqsUriComponents.getHost())
                .withPort(elasticMqLocalSqsUriComponents.getPort())
                .start();
    }

    @Bean
    public UriComponents elasticMqLocalSqsUriComponents() {
        return UriComponentsBuilder.newInstance()
                .scheme(elasticMqLocalSqsUri.getScheme())
                .host(elasticMqLocalSqsUri.getHost())
                .port(elasticMqLocalSqsUri.getPort())
                .build()
                .encode();
    }
}
