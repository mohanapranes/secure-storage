package com.thors.secure_store.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {

  private String frontendBaseUrl;
  private String backendBaseUrl;
  private int shareLinkExpiryMinutes;
}
