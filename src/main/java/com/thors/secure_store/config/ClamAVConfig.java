package com.thors.secure_store.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties (prefix = "clamav")
@Data
public class ClamAVConfig {
    private String host;
    private Integer port;
    private int connectTimeoutMs;
    private int readTimeoutMs;
}

