package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ProgressPhotoResponse;
import com.mauricioandrade.progressor.core.application.ports.ProgressPhotoRepository;
import com.mauricioandrade.progressor.core.domain.photo.ProgressPhoto;
import java.util.Base64;
import java.util.UUID;

public class UpdateStudentNotesUseCase {

  private final ProgressPhotoRepository repository;

  public UpdateStudentNotesUseCase(ProgressPhotoRepository repository) {
    this.repository = repository;
  }

  public ProgressPhotoResponse execute(UUID photoId, UUID studentId, String notes) {
    ProgressPhoto photo = repository.findById(photoId)
        .orElseThrow(() -> new IllegalArgumentException("Photo not found"));
    if (!photo.getStudentId().equals(studentId)) {
      throw new IllegalArgumentException("Photo does not belong to this student");
    }
    photo.updateStudentNotes(notes);
    ProgressPhoto saved = repository.save(photo);
    return new ProgressPhotoResponse(saved.getId(), saved.getTakenAt(), saved.getDescription(),
        Base64.getEncoder().encodeToString(saved.getPhotoData()),
        saved.getProfessionalFeedback(), saved.getProfessionalId(), saved.getProfessionalName(),
        saved.getProfessionalRole(), saved.getStudentNotes());
  }
}
