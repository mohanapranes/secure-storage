package com.thors.secure_store.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "file_shares")
@Data
public class FileShareEntity {

  @Id private String shareId; // UUID or secure token
  private String fileId;
  private String ownerId; // who shared it

  @Enumerated (EnumType.STRING)
  private AccessRole accessRole;

  private Instant expiresAt;
  private boolean revoked;
  private Instant createdAt;
}
