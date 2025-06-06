package com.thors.secure_store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AllFilesListResponse {
  List<String> allFilesIds;
}
