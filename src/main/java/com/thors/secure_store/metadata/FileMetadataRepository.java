package com.thors.secure_store.metadata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, String> {

    List<FileMetadata> findByOwnerId(String userId);

    @Query ("SELECT f FROM FileMetadata f WHERE f.fileId = :fileId AND f.ownerId = :userId")
    Optional<FileMetadata> findByFileIdAndUser(String fileId, String userId);
}
