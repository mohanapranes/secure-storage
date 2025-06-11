package com.thors.secure_store.controller;

import com.thors.secure_store.dto.response.SecureLinkResponse;
import com.thors.secure_store.service.FileSharingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/api/share")
@Validated
public class SecureLinkController {

  private final FileSharingService fileSharingService;

  @Operation(summary = "Create a secure sharing link for a file")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Share link created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access"),
        @ApiResponse(responseCode = "404", description = "File not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PostMapping("/{fileId}")
  public ResponseEntity<SecureLinkResponse> createShareLink(
      @PathVariable @NotBlank String fileId,
      @RequestParam("accessRole") @NotBlank String accessRole, Principal principal) {
    String userId = principal.getName();
    SecureLinkResponse response = fileSharingService.createShareLink(fileId, userId, accessRole);
    return ResponseEntity.status(response.getStatusCode()).body(response);
  }

  @DeleteMapping("/{shareId}")
  public ResponseEntity<String> revokeShareLink(
          @PathVariable String shareId,
          Principal principal) throws AccessDeniedException {

    String currentUser = principal.getName();
    fileSharingService.revokeLink(shareId, currentUser);
    return ResponseEntity.ok("Link revoked successfully");
  }


  @GetMapping("/{shareId}/view")
  public ResponseEntity<InputStreamResource> downloadViaShareLink(
      @PathVariable String shareId) throws Exception {
    InputStreamResource file = fileSharingService.verifyAndDownload(shareId);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(file);
  }

  //TODO
  @DeleteMapping("/{shareId}/delete")
  public ResponseEntity<InputStreamResource> deleteFileViaShareLink(
          @PathVariable String shareId) throws Exception {
    InputStreamResource file = fileSharingService.verifyAndDownload(shareId);
    return ResponseEntity.ok()
            .header(
                    HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(file);
  }
}
