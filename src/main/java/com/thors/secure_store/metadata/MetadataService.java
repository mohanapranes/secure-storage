package com.thors.secure_store.metadata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class MetadataService {

    private final FileMetadataRepository metadataRepository;

    public void saveFileMetadata(String fileId, String fileName, String ownerId, SecretKey secretKey, byte[] iv){
        FileMetadata meta = new FileMetadata();
        meta.setFileId(fileId);
        meta.setFileName(fileName);
        meta.setIv(iv);
        meta.setOwnerId(ownerId);
        meta.setEncryptedKey(secretKey.getEncoded());
        meta.setUploadTime(System.currentTimeMillis());
        metadataRepository.save(meta);

    }
}
