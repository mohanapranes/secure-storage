package com.thors.secure_store.storage;

import com.thors.secure_store.config.MinioConfig;
import io.minio.MinioClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class MinioClientConfig {

  private MinioConfig minioConfig;

  @Bean
  public MinioClient minioClient() {

    return MinioClient.builder()
        .endpoint(minioConfig.getEndpoint())
        .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey())
        .build();
  }
}
