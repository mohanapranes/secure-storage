package com.thors.secure_store.service;

import com.thors.secure_store.dto.response.AllFilesListResponse;
import com.thors.secure_store.model.FileMetadata;
import com.thors.secure_store.storage.MinioStorageService;
import com.thors.secure_store.util.EncryptionUtils;
import com.thors.secure_store.util.FileMetadataUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FileDownloadService {

  private final FileMetadataUtils fileMetadataUtils;
  private final MinioStorageService fileStorageService;
  private final EncryptionUtils encryptionUtils;

  public AllFilesListResponse getAllFilesForTheUser(String ownerId) {
    try {
      log.info("Retrieving all files for user: {}", ownerId);
      List<String> fileIds = fileMetadataUtils.getAllFileMetadataForAUser(ownerId);
      return new AllFilesListResponse(fileIds);
    } catch (Exception e) {
      log.error("Failed to fetch files for user: {}", ownerId, e);
      return new AllFilesListResponse(List.of());
    }
  }

  public ResponseEntity<InputStreamResource> downloadFile(String fileId, String userId) {
    log.info("Initiating download for file: {} by owner: {}", fileId, userId);
    try {
      // 1. Load metadata
      FileMetadata metadata = fileMetadataUtils.getFileMetadataForId(fileId);
      if (metadata == null) {
        log.warn("Metadata not found for fileId: {}", fileId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }

      // 2. Check ownership
      if (!metadata.getOwnerId().equals(userId)) {
        log.warn("Unauthorized download attempt: fileId={} by user={}", fileId, userId);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }

      // 3. Load encrypted file bytes
      byte[] encryptedBytes = fileStorageService.getFile(fileId);
      if (encryptedBytes == null || encryptedBytes.length == 0) {
        log.error("File data missing for fileId: {}", fileId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }

      // 4. Decrypt
      byte[] decryptedBytes =
          encryptionUtils.decrypt(encryptedBytes, metadata.getEncryptedKey(), metadata.getIv());

      InputStream inputStream = new ByteArrayInputStream(decryptedBytes);
      InputStreamResource resource = new InputStreamResource(inputStream);

      // 5. Build response
      HttpHeaders headers = new HttpHeaders();
      headers.setContentDisposition(
          ContentDisposition.attachment().filename(metadata.getFileName()).build());
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

      return ResponseEntity.ok()
          .headers(headers)
          .contentLength(decryptedBytes.length)
          .body(resource);

    } catch (Exception e) {
      log.error("Failed to download a file with the file id {} for user: {}", fileId, userId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
