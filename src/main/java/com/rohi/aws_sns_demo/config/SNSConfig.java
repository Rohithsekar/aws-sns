package com.rohi.aws_sns_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class SNSConfig {

    @Value("${aws.credentialsProvider.profileName}")
    private String profile;

    @Bean
    public SnsClient getSNSClient() {

        return SnsClient.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(ProfileCredentialsProvider.create(profile))
                .build();

    }
}
