package com.thors.secure_store.dto.others;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table (name = "file_metadata")
@Data
public class FileMetadata {
    @Id
    private String fileId;
    private String fileName;
    private String ownerId;
    private byte[] encryptedKey;
    private byte[] iv;
    private long uploadTime;
}

