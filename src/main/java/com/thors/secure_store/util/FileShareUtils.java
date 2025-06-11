package com.thors.secure_store.util;

import com.thors.secure_store.model.AccessRole;
import com.thors.secure_store.model.FileMetadata;
import com.thors.secure_store.model.FileShareEntity;
import com.thors.secure_store.repository.FileShareRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@AllArgsConstructor
public class FileShareUtils {

  private final FileShareRepository fileShareRepository;

  public void saveFileShareDetails(
      String shareId, String fileId, String ownerId, Instant expiredAt, AccessRole accessRole) {
    FileShareEntity fileShareEntity = new FileShareEntity();
    fileShareEntity.setFileId(fileId);
    fileShareEntity.setShareId(shareId);
    fileShareEntity.setOwnerId(ownerId);
    fileShareEntity.setExpiresAt(expiredAt);
    fileShareEntity.setAccessRole(accessRole);
    fileShareEntity.setCreatedAt(Instant.ofEpochSecond(System.currentTimeMillis()));
    fileShareRepository.save(fileShareEntity);
  }

  public FileShareEntity getFileShareDetails(String fileId) {
    return fileShareRepository.findByShareId(fileId);
  }

  public void setRevokeToTrue(FileShareEntity fileShareEntity) {
    fileShareEntity.setRevoked(true);
    fileShareRepository.save(fileShareEntity);
  }

}
