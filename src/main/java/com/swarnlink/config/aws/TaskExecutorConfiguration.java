package com.swarnlink.config.aws;


import io.awspring.cloud.sqs.MessageExecutionThreadFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.UUID;
import java.util.concurrent.*;

@Configuration
@EnableAsync
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableScheduling
@RequiredArgsConstructor
public class TaskExecutorConfiguration {

    @Bean(name = "taskExecutor")
    public Executor asyncExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1000);
        executor.setMaxPoolSize(Integer.MAX_VALUE);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("Swarnlink-");
        executor.setThreadFactory(createThreadFactory());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.afterPropertiesSet();
        return executor;
    }

    protected ThreadFactory createThreadFactory() {
        MessageExecutionThreadFactory threadFactory = new MessageExecutionThreadFactory();
        threadFactory.setThreadNamePrefix(UUID.randomUUID() + "-");
        return threadFactory;
    }
    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(1000, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(500));
    }

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster =
                new SimpleApplicationEventMulticaster();

        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }
}
