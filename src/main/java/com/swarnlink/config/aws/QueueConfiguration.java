
package com.swarnlink.config.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.util.concurrent.Executor;

@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
@RequiredArgsConstructor
public class QueueConfiguration {
    public  interface AcknowledgementFactories{
        String ALWAYS = "alwaysAcknowledgeContainerFactory";
        String MANUAL = "manualAcknowledgeContainerFactory";
        String ON_SUCCESS = "onSuccessAcknowledgeContainerFactory";

    }

    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(SqsAsyncClient sqsAsyncClient,
                                                                                         @Qualifier("taskExecutor") Executor executor,
                                                                                         @Qualifier("sqsMessagingConverter") SqsMessagingMessageConverter messageConverter ) {
        return SqsMessageListenerContainerFactory
                .builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(options -> options.messageConverter(messageConverter)
                        .componentsTaskExecutor((TaskExecutor) executor))
                .build();
    }

    @Bean(AcknowledgementFactories.ALWAYS)
    public SqsMessageListenerContainerFactory<Object> alwaysAcknowledgeContainerFactory(SqsAsyncClient sqsAsyncClient,
                                                                                        @Qualifier("taskExecutor") Executor executor,
                                                                                        @Qualifier("sqsMessagingConverter") SqsMessagingMessageConverter messageConverter ) {
        return acknowledgeContainerFactory(sqsAsyncClient,executor,messageConverter,AcknowledgementMode.ALWAYS);
    }

    @Bean(AcknowledgementFactories.ON_SUCCESS)
    public SqsMessageListenerContainerFactory<Object> onSuccessAcknowledgeContainerFactory(SqsAsyncClient sqsAsyncClient,
                                                                                        @Qualifier("taskExecutor") Executor executor,
                                                                                        @Qualifier("sqsMessagingConverter") SqsMessagingMessageConverter messageConverter ) {
        return acknowledgeContainerFactory(sqsAsyncClient,executor,messageConverter,AcknowledgementMode.ON_SUCCESS);
    }


    public SqsMessageListenerContainerFactory<Object> acknowledgeContainerFactory(SqsAsyncClient sqsAsyncClient,
                                                                                  @Qualifier("taskExecutor") Executor executor,
                                                                                  @Qualifier("sqsMessagingConverter") SqsMessagingMessageConverter messageConverter,
                                                                                  AcknowledgementMode acknowledgementMode) {
        return SqsMessageListenerContainerFactory
                .builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(options -> options.messageConverter(messageConverter)
                        .acknowledgementMode(acknowledgementMode)
                        .componentsTaskExecutor((TaskExecutor) executor))
                .build();
    }

    @Bean(AcknowledgementFactories.MANUAL)
    public SqsMessageListenerContainerFactory<Object> manualAcknowledgeContainerFactory(SqsAsyncClient sqsAsyncClient,
                                                                                        @Qualifier("taskExecutor") Executor executor,
                                                                                        @Qualifier("sqsMessagingConverter") SqsMessagingMessageConverter messageConverter ) {
        return acknowledgeContainerFactory(sqsAsyncClient,executor,messageConverter,AcknowledgementMode.MANUAL);
    }

    private SqsMessagingMessageConverter sqsMessagingMessageConverter(@Qualifier("swarnlinkObjectMapper")  ObjectMapper objectMapper) {
        SqsMessagingMessageConverter converter = new SqsMessagingMessageConverter();
        converter.setPayloadMessageConverter(mappingJackson2MessageConverter(objectMapper));
        converter.setPayloadTypeMapper(m-> null);
        return converter;
    }

    @Bean
    public MappingJackson2MessageConverter mappingJackson2MessageConverter(@Qualifier("swarnlinkObjectMapper") ObjectMapper objectMapper) {
        MappingJackson2MessageConverter jacksonMessageConverter = new MappingJackson2MessageConverter();
        jacksonMessageConverter.setObjectMapper(objectMapper);
        jacksonMessageConverter.setSerializedPayloadClass(String.class);
        jacksonMessageConverter.setStrictContentTypeMatch(true);
        return jacksonMessageConverter;
    }

    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient,@Qualifier("swarnlinkObjectMapper") ObjectMapper objectMapper) {
        var messageConverter = new SqsMessagingMessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        return SqsTemplate.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(o -> o.queueNotFoundStrategy(QueueNotFoundStrategy.FAIL))
                .messageConverter(messageConverter)
                .build();
    }


    @Bean("sqsMessagingConverter")
    public SqsMessagingMessageConverter converter() {
        return sqsMessagingMessageConverter(objectMapper());
    }


    @Bean("swarnlinkObjectMapper")
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        return mapper.findAndRegisterModules()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }


}
