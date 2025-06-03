package com.thors.secure_store.service;

import com.thors.secure_store.dto.others.VirusScanResult;
import com.thors.secure_store.dto.response.FileUploadResponse;
import com.thors.secure_store.metadata.MetadataService;
import com.thors.secure_store.storage.MinioStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;

@Service
public class FileService {

    private VirusScanService virusScanService;
    private MetadataService metadataService;
//    private AuditService auditService;
    private MinioStorageService fileStorageService;
    private EncryptionService encryptionService;

    public FileService(MinioStorageService fileStorageService, MetadataService metadataService,
            EncryptionService encryptionService, VirusScanService virusScanService) {
        this.fileStorageService = fileStorageService;
        this.metadataService = metadataService;
        this.encryptionService = encryptionService;
        this.virusScanService = virusScanService;
    }

    public FileUploadResponse uploadFile(MultipartFile file, String ownerId) throws Exception {

        // Virus scan
        //TODO: handle virus scan result
        VirusScanResult virusScanResult = virusScanService.scan(file.getInputStream());
        if (virusScanResult.isInfected()) {
            throw new RuntimeException("File failed virus scan!");
        }

        //Encrypt
        byte[] iv = encryptionService.generateIV();
        SecretKey secretKey = encryptionService.generateKey();
        byte[] encryptedData = encryptionService.encrypt(file.getBytes(), secretKey, iv);

        //Store in MinIO
        String fileId = fileStorageService.storeFile(encryptedData, file.getOriginalFilename());

        //Save metadata
        metadataService.saveFileMetadata(fileId, file.getOriginalFilename(), ownerId, secretKey, iv);

//        //Audit log
//        auditService.logEvent(ownerId, "UPLOAD", fileId);

        //Response
        return new FileUploadResponse(fileId, "File uploaded successfully");
    }
}
