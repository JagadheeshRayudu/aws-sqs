package com.aws.sqs.controller;

import com.aws.sqs.exception.ProblemDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SQSController {
    private final QueueMessagingTemplate queueMessagingTemplate;
    @Value("${cloud.aws.end-point.uri}")
    private String endpointUri;
    @Autowired
    private Environment env;

    @PutMapping("/put/{message}")
    public ResponseEntity<?> putMessageInQueue(@PathVariable String message, @RequestParam(value = "queueName", required = false, defaultValue = "jagadheesh-queue") String queueName) {
        String[] activeProfiles = env.getActiveProfiles();
        List<String> profiles = Arrays.asList(activeProfiles);
        try {
            if (profiles.contains("local")) {
                queueMessagingTemplate.send(queueName, MessageBuilder.withPayload(message).build());
                log.info("Message has been sent to ElasticMQ");
            } else {
                queueMessagingTemplate.send(endpointUri, MessageBuilder.withPayload(message).build());
                log.info("Message has been sent to AWS SQS");
            }
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body(new ProblemDetails(500, e.getMessage(), "Cannot Send Message To Queue"));
        }
        return ResponseEntity.noContent().build();
    }

    @SqsListener("jagadheesh-queue")
    private void queueListener(String message) {
        System.err.println("Message from Queue ====>  " + message);
    }
}
