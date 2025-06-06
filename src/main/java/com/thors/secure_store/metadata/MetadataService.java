package com.thors.secure_store.metadata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetadataService {

  private final FileMetadataRepository metadataRepository;

  public void saveFileMetadata(
      String fileId, String fileName, String ownerId, SecretKey secretKey, byte[] iv) {
    FileMetadata meta = new FileMetadata();
    meta.setFileId(fileId);
    meta.setFileName(fileName);
    meta.setIv(iv);
    meta.setOwnerId(ownerId);
    meta.setEncryptedKey(secretKey.getEncoded());
    meta.setUploadTime(System.currentTimeMillis());
    metadataRepository.save(meta);
  }

  public List<String> getAllFileMetadataForAUser(String ownerId) {
    List<FileMetadata> allFilesMetaData = metadataRepository.findByOwnerId(ownerId);
    return allFilesMetaData.stream().map(FileMetadata::getFileId).toList();
  }

  public FileMetadata getFileMetadataForId(String fileId) {
    return metadataRepository.findByFileId(fileId);
  }
}
