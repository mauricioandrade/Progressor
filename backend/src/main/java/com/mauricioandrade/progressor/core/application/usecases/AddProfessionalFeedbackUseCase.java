package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ProgressPhotoResponse;
import com.mauricioandrade.progressor.core.application.ports.ProgressPhotoRepository;
import com.mauricioandrade.progressor.core.domain.photo.ProgressPhoto;
import java.util.Base64;
import java.util.UUID;

public class AddProfessionalFeedbackUseCase {

  private final ProgressPhotoRepository repository;

  public AddProfessionalFeedbackUseCase(ProgressPhotoRepository repository) {
    this.repository = repository;
  }

  public ProgressPhotoResponse execute(UUID photoId, UUID professionalId, String professionalName,
      String professionalRole, String feedback) {
    ProgressPhoto photo = repository.findById(photoId)
        .orElseThrow(() -> new IllegalArgumentException("Photo not found"));
    // Only the original author may edit their feedback; a blank professionalId means no one has
    // written yet so any professional may add it for the first time.
    if (photo.getProfessionalId() != null && !photo.getProfessionalId().equals(professionalId)) {
      throw new IllegalArgumentException("Only the original author may edit this feedback");
    }
    photo.applyFeedback(professionalId, professionalName, professionalRole, feedback);
    ProgressPhoto saved = repository.save(photo);
    return new ProgressPhotoResponse(saved.getId(), saved.getTakenAt(), saved.getDescription(),
        Base64.getEncoder().encodeToString(saved.getPhotoData()),
        saved.getProfessionalFeedback(), saved.getProfessionalId(), saved.getProfessionalName(),
        saved.getProfessionalRole(), saved.getStudentNotes());
  }
}
