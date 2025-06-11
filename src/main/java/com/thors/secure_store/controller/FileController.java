package com.thors.secure_store.controller;

import com.thors.secure_store.dto.response.AllFilesListResponse;
import com.thors.secure_store.dto.response.FileUploadResponse;
import com.thors.secure_store.service.FileDownloadService;
import com.thors.secure_store.service.FileUploadService;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/files")
public class FileController {

  private final FileUploadService fileUploadService;
  private final FileDownloadService fileDownloadService;

  @PostMapping("/upload")
  public ResponseEntity<FileUploadResponse> uploadFile(
      @RequestParam("file") MultipartFile file, Principal principal) {

    String ownerId = principal.getName();
    FileUploadResponse response = fileUploadService.uploadFile(file, ownerId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/all-files")
  public ResponseEntity<AllFilesListResponse> getAllFilesForTheUser(Principal principal) {

    String ownerId = principal.getName();
    AllFilesListResponse response = fileDownloadService.getAllFilesForTheUser(ownerId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{fileId}")
  public ResponseEntity<InputStreamResource> downloadFile(
      @PathVariable String fileId, Principal principal) {
    String ownerId = principal.getName();
    return fileDownloadService.downloadFile(fileId, ownerId);
  }

}
