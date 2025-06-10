package com.swarnlink.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "reminder")
@Getter
@Setter
public class ReminderProperties {
    private int daysBeforeClose = 3;
    private int batch = 30;
}