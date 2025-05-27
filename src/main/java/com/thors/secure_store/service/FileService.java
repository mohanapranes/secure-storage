package com.thors.secure_store.service;

import com.thors.secure_store.dto.response.FileUploadResponse;
import com.thors.secure_store.metadata.MetadataService;
import com.thors.secure_store.storage.MinioStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;

@Service
public class FileService {

//    private VirusScanService virusScanService;
    private MetadataService metadataService;
//    private AuditService auditService;
    private MinioStorageService fileStorageService;

    public FileService(MinioStorageService fileStorageService, MetadataService metadataService) {
        this.fileStorageService = fileStorageService;
        this.metadataService = metadataService;
    }
    //    public FileService(VirusScanService virusScanService, MinioStorageService fileStorageService, MetadataService metadataService, AuditService auditService) {
//        this.virusScanService = virusScanService;
//        this.fileStorageService = fileStorageService;
//        this.metadataService = metadataService;
//        this.auditService = auditService;
//    }

    public FileUploadResponse uploadFile(MultipartFile file, String ownerId) throws Exception {
        // Virus scan
//        boolean isSafe = virusScanService.scan(file.getInputStream());
//        if (!isSafe) {
//            throw new RuntimeException("File failed virus scan!");
//        }
//
//        //Encrypt
//        byte[] iv = encryptionService.generateIV();
//        SecretKey secretKey = encryptionService.generateKey();
//        byte[] encryptedData = encryptionService.encrypt(file.getBytes(), secretKey, iv);

        //Store in MinIO
        String fileId = fileStorageService.storeFile(file.getBytes(), file.getOriginalFilename());

        //Save metadata
//        metadataService.saveFileMetadata(fileId, file.getOriginalFilename(), ownerId, secretKey, iv);

//        //Audit log
//        auditService.logEvent(ownerId, "UPLOAD", fileId);

        //Response
        return new FileUploadResponse(fileId, "File uploaded successfully");
    }
}
