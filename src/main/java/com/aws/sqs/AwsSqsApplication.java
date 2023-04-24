package com.aws.sqs;

import com.aws.sqs.config.LocalServiceUri;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(LocalServiceUri.class)
public class AwsSqsApplication {
	public static void main(String[] args) {
		SpringApplication.run(AwsSqsApplication.class, args);
	}
}
