package com.mauricioandrade.progressor.core.domain.connection;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConnectionRequest {

  private final UUID id;
  private final UUID professionalId;
  private final UUID studentId;
  private final String professionalName;
  private final ProfessionalRole professionalRole;
  private ConnectionStatus status;
  private final LocalDateTime createdAt;

  public ConnectionRequest(UUID id, UUID professionalId, UUID studentId, String professionalName,
      ProfessionalRole professionalRole) {
    if (professionalId == null) {
      throw new IllegalArgumentException("Professional ID is required");
    }
    if (studentId == null) {
      throw new IllegalArgumentException("Student ID is required");
    }
    if (professionalName == null || professionalName.isBlank()) {
      throw new IllegalArgumentException("Professional name is required");
    }
    if (professionalRole == null) {
      throw new IllegalArgumentException("Professional role is required");
    }
    this.id = id != null ? id : UUID.randomUUID();
    this.professionalId = professionalId;
    this.studentId = studentId;
    this.professionalName = professionalName;
    this.professionalRole = professionalRole;
    this.status = ConnectionStatus.PENDING;
    this.createdAt = LocalDateTime.now();
  }

  // Reconstruction constructor (from persistence)
  public ConnectionRequest(UUID id, UUID professionalId, UUID studentId, String professionalName,
      ProfessionalRole professionalRole, ConnectionStatus status, LocalDateTime createdAt) {
    this.id = id;
    this.professionalId = professionalId;
    this.studentId = studentId;
    this.professionalName = professionalName;
    this.professionalRole = professionalRole;
    this.status = status;
    this.createdAt = createdAt;
  }

  public void accept() {
    if (this.status != ConnectionStatus.PENDING) {
      throw new IllegalStateException("Only PENDING requests can be accepted");
    }
    this.status = ConnectionStatus.ACCEPTED;
  }

  public void reject() {
    if (this.status != ConnectionStatus.PENDING) {
      throw new IllegalStateException("Only PENDING requests can be rejected");
    }
    this.status = ConnectionStatus.REJECTED;
  }

  public UUID getId() {
    return id;
  }

  public UUID getProfessionalId() {
    return professionalId;
  }

  public UUID getStudentId() {
    return studentId;
  }

  public String getProfessionalName() {
    return professionalName;
  }

  public ProfessionalRole getProfessionalRole() {
    return professionalRole;
  }

  public ConnectionStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
