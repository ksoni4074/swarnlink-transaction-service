package com.swarnlink.config.aws.sqs;

import io.awspring.cloud.sqs.config.SqsBootstrapConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
@Profile("local")
@Import(SqsBootstrapConfiguration.class)
@RequiredArgsConstructor
public class LocalAwsResourceConfiguration {

    @Value("${spring.cloud.aws.endpoint.sqs}")
    private String awsEndpoint;

    @Value("${cloud.aws.sqs.region}")
    private String awsRegion;

    @Bean
    public SqsAsyncClient localSqsAsyncClient() {
        return SqsAsyncClient.builder()
                .endpointOverride(URI.create(awsEndpoint))
                .region(Region.of(awsRegion))
                .build();
    }

/*    @Bean
    @ConditionalOnProperty(name="aws.s3.enabled", havingValue = "true")
    public S3Client s3Client() {

        return S3Client.builder()
                .endpointOverride(URI.create(awsEndpoint))
                .region(Region.of(awsRegion))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    @Bean
    @ConditionalOnProperty(name="aws.s3.enabled", havingValue = "true")
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .endpointOverride(URI.create(awsEndpoint))
                .region(Region.of(awsRegion))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build()).build();
    }*/

}