package com.thors.secure_store.controller;

import com.thors.secure_store.dto.response.FileUploadResponse;
import com.thors.secure_store.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam ("file") MultipartFile file,
            Principal principal) throws Exception {

        String ownerId = principal.getName();
        FileUploadResponse response = fileService.uploadFile(file, ownerId);
        return ResponseEntity.ok(response);
    }
}
