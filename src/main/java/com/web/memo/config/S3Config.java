package com.web.memo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String awsAccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String awsSecretKey;

    /**
     * S3Client 설정
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    private StaticCredentialsProvider getCredentialsProvider() {
        AwsBasicCredentials awsCred = AwsBasicCredentials.create(
                awsAccessKey,
                awsSecretKey);
        return StaticCredentialsProvider.create(awsCred);
    }
}
