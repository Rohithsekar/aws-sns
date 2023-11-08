package com.rohi.aws_sns_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class SNSConfig {

    @Value("${aws.accessKey}")
    private String accesskey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Bean
    public SnsClient getSNSClient() {

        AwsCredentials awsCredentials = AwsBasicCredentials.create(accesskey, secretKey);
        return SnsClient.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

    }
}
