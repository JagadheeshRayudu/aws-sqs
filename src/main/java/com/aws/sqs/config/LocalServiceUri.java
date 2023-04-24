package com.aws.sqs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "elasticmq")
public class LocalServiceUri {
    private String scheme;
    private String host;
    private String path;
    private String port;
}
