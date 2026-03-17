package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import com.mauricioandrade.progressor.infrastructure.persistence.converters.EncryptedByteArrayConverter;
import com.mauricioandrade.progressor.infrastructure.persistence.converters.EncryptedStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "progress_photos")
public class ProgressPhotoEntity {

  @Id
  private UUID id;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Convert(converter = EncryptedByteArrayConverter.class)
  @Column(columnDefinition = "bytea", nullable = false)
  private byte[] photoData;

  @Column(name = "taken_at", nullable = false)
  private LocalDate takenAt;

  @Column(length = 255)
  private String description;

  @Convert(converter = EncryptedStringConverter.class)
  @Column(name = "professional_feedback", columnDefinition = "TEXT")
  private String professionalFeedback;

  @Column(name = "professional_id")
  private UUID professionalId;

  @Column(name = "professional_name", length = 200)
  private String professionalName;

  @Column(name = "professional_role", length = 50)
  private String professionalRole;

  @Convert(converter = EncryptedStringConverter.class)
  @Column(name = "student_notes", columnDefinition = "TEXT")
  private String studentNotes;

  public ProgressPhotoEntity() {
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }

  public UUID getStudentId() { return studentId; }
  public void setStudentId(UUID studentId) { this.studentId = studentId; }

  public byte[] getPhotoData() { return photoData; }
  public void setPhotoData(byte[] photoData) { this.photoData = photoData; }

  public LocalDate getTakenAt() { return takenAt; }
  public void setTakenAt(LocalDate takenAt) { this.takenAt = takenAt; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public String getProfessionalFeedback() { return professionalFeedback; }
  public void setProfessionalFeedback(String professionalFeedback) {
    this.professionalFeedback = professionalFeedback;
  }

  public UUID getProfessionalId() { return professionalId; }
  public void setProfessionalId(UUID professionalId) { this.professionalId = professionalId; }

  public String getProfessionalName() { return professionalName; }
  public void setProfessionalName(String professionalName) {
    this.professionalName = professionalName;
  }

  public String getProfessionalRole() { return professionalRole; }
  public void setProfessionalRole(String professionalRole) {
    this.professionalRole = professionalRole;
  }

  public String getStudentNotes() { return studentNotes; }
  public void setStudentNotes(String studentNotes) { this.studentNotes = studentNotes; }
}
