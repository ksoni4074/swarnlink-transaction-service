package com.swarnlink.config.aws.sqs;

import io.awspring.cloud.sqs.config.SqsBootstrapConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;


@Configuration
@Profile("!local")
@Import(SqsBootstrapConfiguration.class)
@RequiredArgsConstructor
public class AwsSQSResourceConfiguration {
    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .build();
    }
}