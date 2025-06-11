package com.thors.secure_store.service;

import com.thors.secure_store.dto.others.VirusScanResult;
import com.thors.secure_store.dto.response.FileUploadResponse;
import com.thors.secure_store.storage.MinioStorageService;
import com.thors.secure_store.util.EncryptionUtils;
import com.thors.secure_store.util.FileMetadataUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.io.IOException;

@Service
@Slf4j
@AllArgsConstructor
public class FileUploadService {

  private final VirusScanService virusScanService;
  private final FileMetadataUtils fileMetadataUtils;
  //    private final AuditService auditService;
  private final MinioStorageService fileStorageService;
  private final EncryptionUtils encryptionUtils;

  public FileUploadResponse uploadFile(MultipartFile file, String ownerId) {
    String originalFileName = file.getOriginalFilename();

    log.info("Initiating upload for file: {} by owner: {}", originalFileName, ownerId);

    try {
      // === Step 1: Validate file ===
      if (file.isEmpty()) {
        return new FileUploadResponse(HttpStatus.BAD_REQUEST.value(), null, "Empty file uploaded.");
      }

      //            if (file.getSize() > MAX_FILE_SIZE_BYTES) {
      //                return new FileUploadResponse(HttpStatus.PAYLOAD_TOO_LARGE.value(), null,
      // "File exceeds maximum allowed size.");
      //            }
      //
      //            if (!ALLOWED_FILE_TYPES.contains(file.getContentType())) {
      //                return new FileUploadResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
      // null, "File type not supported.");
      //            }

      // === Step 2: Virus Scan ===
      VirusScanResult virusScanResult;
      try {
        virusScanResult = virusScanService.scan(file.getInputStream());
      } catch (IOException e) {
        log.warn(
            "Virus scan failed, blocking upload as precaution for file: {}", originalFileName, e);
        return new FileUploadResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "Virus scan failed.");
      }

      if (virusScanResult.isInfected()) {
        log.warn("Infected file upload attempt blocked: {}", originalFileName);
        return new FileUploadResponse(
            HttpStatus.BAD_REQUEST.value(), null, "File is infected with virus.");
      }

      // === Step 3: Encryption ===
      byte[] iv = encryptionUtils.generateIV();
      SecretKey secretKey = encryptionUtils.generateKey();
      byte[] encryptedData = encryptionUtils.encrypt(file.getBytes(), secretKey, iv);

      // === Step 4: Store File in MinIO ===
      String fileId = fileStorageService.storeFile(encryptedData);
      log.info("File stored in storage layer with ID: {}", fileId);

      // === Step 5: Store Metadata ===
      fileMetadataUtils.saveFileMetadata(fileId, originalFileName, ownerId, secretKey, iv);
      log.info("Metadata saved for file ID: {}", fileId);

      // === Step 6: Return success ===
      return new FileUploadResponse(HttpStatus.OK.value(), fileId, "File uploaded successfully.");

    } catch (IOException ioEx) {
      log.error("IOException during file upload: {}", originalFileName, ioEx);
      return new FileUploadResponse(
          HttpStatus.INTERNAL_SERVER_ERROR.value(),
          null,
          "File upload failed due to internal error.");
    } catch (Exception ex) {
      log.error("Unexpected error during file upload: {}", originalFileName, ex);
      return new FileUploadResponse(
          HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "Unexpected error occurred.");
    }
  }
}
