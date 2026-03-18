package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "connection_requests")
public class ConnectionRequestEntity {

  @Id
  private UUID id;

  private UUID professionalId;
  private UUID studentId;
  private String professionalName;
  private String professionalRole;
  private String status;
  private LocalDateTime createdAt;

  public ConnectionRequestEntity() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getProfessionalId() {
    return professionalId;
  }

  public void setProfessionalId(UUID professionalId) {
    this.professionalId = professionalId;
  }

  public UUID getStudentId() {
    return studentId;
  }

  public void setStudentId(UUID studentId) {
    this.studentId = studentId;
  }

  public String getProfessionalName() {
    return professionalName;
  }

  public void setProfessionalName(String professionalName) {
    this.professionalName = professionalName;
  }

  public String getProfessionalRole() {
    return professionalRole;
  }

  public void setProfessionalRole(String professionalRole) {
    this.professionalRole = professionalRole;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
