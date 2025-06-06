package com.thors.secure_store.dto.others;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VirusScanResult {
  private boolean isInfected;
  private String virusName;
  private String rawResponse;
}
