package com.thors.secure_store.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties (prefix = "kafka")
@Data
public class KafkaConfig {
    private String bootstrapServers;
    private String topic;
}

