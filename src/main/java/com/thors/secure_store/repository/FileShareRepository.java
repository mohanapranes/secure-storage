package com.thors.secure_store.repository;

import com.thors.secure_store.model.FileShareEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileShareRepository extends JpaRepository<FileShareEntity, String> {

  List<FileShareEntity> findByOwnerId(String userId);

  FileShareEntity findByShareId(String shareId);

}
