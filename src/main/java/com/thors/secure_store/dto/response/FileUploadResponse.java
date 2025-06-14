package com.thors.secure_store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadResponse {
  private int status;
  private String fileId;
  private String message;
}
