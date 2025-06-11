package com.thors.secure_store.service;

import com.thors.secure_store.config.AppConfig;
import com.thors.secure_store.dto.response.SecureLinkResponse;
import com.thors.secure_store.model.AccessRole;
import com.thors.secure_store.model.FileMetadata;
import com.thors.secure_store.model.FileShareEntity;
import com.thors.secure_store.storage.MinioStorageService;
import com.thors.secure_store.util.EncryptionUtils;
import com.thors.secure_store.util.FileMetadataUtils;
import com.thors.secure_store.util.FileShareUtils;
import com.thors.secure_store.util.SecureRandomTokenGenerator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class FileSharingService {

  private final FileMetadataUtils fileMetadataUtils;
  private final MinioStorageService fileStorageService;
  private final EncryptionUtils encryptionUtils;
  private final FileShareUtils fileShareUtils;
  private final AppConfig appConfig;

  public SecureLinkResponse createShareLink(String fileId, String userId, String accessRoleStr) {
    log.info("Creating share link for file [{}] by user [{}]", fileId, userId);

    try {
      // 1. Validate accessRole
      AccessRole accessRole = parseAccessRole(accessRoleStr);
      if (accessRole == null) {
        log.warn("Invalid access role [{}] requested by user [{}]", accessRoleStr, userId);
        return SecureLinkResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .message("Invalid access role")
            .build();
      }

      // 2. Load file metadata
      FileMetadata metadata = fileMetadataUtils.getFileMetadataForId(fileId);
      if (metadata == null) {
        log.warn("File metadata not found for fileId [{}]", fileId);
        return SecureLinkResponse.builder()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .message("File not found")
            .build();
      }

      // 3. Ownership check
      if (!userId.equals(metadata.getOwnerId())) {
        log.warn("Unauthorized share attempt: user [{}] tried to share file [{}]", userId, fileId);
        return SecureLinkResponse.builder()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .message("Unauthorized access")
            .build();
      }

      // 4. Generate share ID and expiry time
      String shareId = SecureRandomTokenGenerator.generate(32);
      // TODO
      Instant now = Instant.now();
      Instant expiresAt = now.plus(Duration.ofHours(24));

      // 5. Save sharing metadata
      fileShareUtils.saveFileShareDetails(shareId, fileId, userId, expiresAt, accessRole);

      // 6. Build the share link
      String shareLink = String.format("%s/share/%s", appConfig.getFrontendBaseUrl(), shareId);

      return SecureLinkResponse.builder()
          .shareLink(shareLink)
          .expiresAt(expiresAt)
          .accessRole(accessRole)
          .statusCode(HttpStatus.OK.value())
          .message("Share link created successfully")
          .build();

    } catch (Exception ex) {
      log.error("Error creating share link for file [{}] by user [{}]", fileId, userId, ex);
      return SecureLinkResponse.builder()
          .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
          .message("Failed to create share link")
          .build();
    }
  }

  public void revokeLink(String shareId, String userId) throws AccessDeniedException {
    FileShareEntity share = fileShareUtils.getFileShareDetails(shareId);
    if (share == null) {
      throw new RuntimeException("Share link not found");
    }

    if (!share.getOwnerId().equals(userId)) {
      throw new AccessDeniedException("You are not the owner of this link");
    }

    if (share.isRevoked()) {
      return; // Already revoked, idempotent
    }

    fileShareUtils.setRevokeToTrue(share);
  }

  public InputStreamResource verifyAndDownload(String shareId) throws Exception {
    // 🔍 Step 1: Find the shared link entry
    FileShareEntity share = fileShareUtils.getFileShareDetails(shareId);
    if(share == null){
      //TODO
      throw new RuntimeException("Invalid or expired share link");
    }

    // ⏳ Step 2: Check revocation and expiry
    if (share.isRevoked()) {
      throw new AccessDeniedException("Share link is revoked");
    }

    if (Instant.now().isAfter(share.getExpiresAt())) {
      //TODO
//      throw new LinkExpiredException("Share link has expired");
      throw new RuntimeException("Share link has expired");
    }

    // 📦 Step 3: Load file metadata
    String fileId = share.getFileId();
    FileMetadata metadata = fileMetadataUtils.getFileMetadataForId(fileId);
    if (metadata == null) {
      log.warn("Metadata not found for fileId: {}", fileId);
      throw new RuntimeException("File not found");
    }

    // 3. Load encrypted file bytes
    byte[] encryptedBytes = fileStorageService.getFile(fileId);
    if (encryptedBytes == null || encryptedBytes.length == 0) {
      log.error("File data missing for fileId: {}", fileId);
      throw new RuntimeException("Internal server error");
    }

    // 4. Decrypt
    byte[] decryptedBytes =
            encryptionUtils.decrypt(encryptedBytes, metadata.getEncryptedKey(), metadata.getIv());

    InputStream inputStream = new ByteArrayInputStream(decryptedBytes);
    InputStreamResource resource = new InputStreamResource(inputStream);

    return resource;
  }

  private AccessRole parseAccessRole(String role) {
    try {
      return AccessRole.valueOf(role.toUpperCase());
    } catch (IllegalArgumentException | NullPointerException ex) {
      return null;
    }
  }
}
