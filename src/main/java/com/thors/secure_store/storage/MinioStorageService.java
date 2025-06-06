package com.thors.secure_store.storage;

import com.thors.secure_store.config.MinioConfig;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MinioStorageService {

  private MinioClient minioClient;
  private MinioConfig minioConfig;

  public String storeFile(byte[] data) throws Exception {
    String fileId = UUID.randomUUID().toString();
    ByteArrayInputStream bais = new ByteArrayInputStream(data);

    PutObjectArgs putObjectArgs =
        PutObjectArgs.builder().bucket(minioConfig.getBucket()).object(fileId).stream(
                bais, data.length, -1)
            .contentType("application/octet-stream")
            .build();

    minioClient.putObject(putObjectArgs);
    return fileId;
  }

  public byte[] getFile(String fileId) throws Exception {
    GetObjectArgs getObjectArgs =
        GetObjectArgs.builder().bucket(minioConfig.getBucket()).object(fileId).build();

    try (InputStream is = minioClient.getObject(getObjectArgs)) {
      return is.readAllBytes();
    }
  }

  public void deleteFile(String fileId) throws Exception {
    RemoveObjectArgs removeObjectArgs =
        RemoveObjectArgs.builder().bucket(minioConfig.getBucket()).object(fileId).build();

    minioClient.removeObject(removeObjectArgs);
  }
}
