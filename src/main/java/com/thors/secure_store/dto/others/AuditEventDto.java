package com.thors.secure_store.dto.others;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditEventDto {
  private String eventType; // "UPLOAD", "DOWNLOAD", "LINK_ACCESS", "ERROR"
  private String fileId;
  private String performedBy; // userId or "anonymous"
  private long timestamp;
  private String ipAddress;
  private String userAgent;
  private String additionalInfo;
}

// TODO
enum EventType {
  UPLOAD,
  DOWNLOAD,
  LINK_ACCESS_ERROR
}
