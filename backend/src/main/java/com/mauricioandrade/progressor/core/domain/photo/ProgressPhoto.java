package com.mauricioandrade.progressor.core.domain.photo;

import java.time.LocalDate;
import java.util.UUID;

public class ProgressPhoto {

  private final UUID id;
  private final UUID studentId;
  private final byte[] photoData;
  private final LocalDate takenAt;
  private final String description;
  private String professionalFeedback;
  private UUID professionalId;
  private String professionalName;
  private String professionalRole;
  private String studentNotes;

  public ProgressPhoto(UUID id, UUID studentId, byte[] photoData, LocalDate takenAt,
      String description) {
    this.id = id;
    this.studentId = studentId;
    this.photoData = photoData;
    this.takenAt = takenAt;
    this.description = description;
  }

  public ProgressPhoto(UUID id, UUID studentId, byte[] photoData, LocalDate takenAt,
      String description, String professionalFeedback, UUID professionalId,
      String professionalName, String professionalRole, String studentNotes) {
    this(id, studentId, photoData, takenAt, description);
    this.professionalFeedback = professionalFeedback;
    this.professionalId = professionalId;
    this.professionalName = professionalName;
    this.professionalRole = professionalRole;
    this.studentNotes = studentNotes;
  }

  public UUID getId() { return id; }
  public UUID getStudentId() { return studentId; }
  public byte[] getPhotoData() { return photoData; }
  public LocalDate getTakenAt() { return takenAt; }
  public String getDescription() { return description; }
  public String getProfessionalFeedback() { return professionalFeedback; }
  public UUID getProfessionalId() { return professionalId; }
  public String getProfessionalName() { return professionalName; }
  public String getProfessionalRole() { return professionalRole; }
  public String getStudentNotes() { return studentNotes; }

  public void applyFeedback(UUID profId, String profName, String profRole, String feedback) {
    this.professionalId = profId;
    this.professionalName = profName;
    this.professionalRole = profRole;
    this.professionalFeedback = feedback;
  }

  public void clearFeedback() {
    this.professionalId = null;
    this.professionalName = null;
    this.professionalRole = null;
    this.professionalFeedback = null;
  }

  public void updateStudentNotes(String notes) {
    this.studentNotes = notes;
  }
}
