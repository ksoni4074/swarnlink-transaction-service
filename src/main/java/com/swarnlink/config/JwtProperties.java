package com.swarnlink.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties{
    private String issuer;
    private Duration expiration;
    private String privateKeyFileName;
    private String publicKeyFileName;
}
