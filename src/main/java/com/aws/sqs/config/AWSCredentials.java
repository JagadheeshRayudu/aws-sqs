package com.aws.sqs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cloud.aws.credentials")
public class AWSCredentials {
    private String accessKey;
    private String secretKey;
}
