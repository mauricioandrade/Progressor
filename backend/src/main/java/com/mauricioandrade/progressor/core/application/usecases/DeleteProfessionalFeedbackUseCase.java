package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ProgressPhotoResponse;
import com.mauricioandrade.progressor.core.application.ports.ProgressPhotoRepository;
import com.mauricioandrade.progressor.core.domain.photo.ProgressPhoto;
import java.util.Base64;
import java.util.UUID;

public class DeleteProfessionalFeedbackUseCase {

  private final ProgressPhotoRepository repository;

  public DeleteProfessionalFeedbackUseCase(ProgressPhotoRepository repository) {
    this.repository = repository;
  }

  public ProgressPhotoResponse execute(UUID photoId, UUID requestingProfessionalId) {
    ProgressPhoto photo = repository.findById(photoId)
        .orElseThrow(() -> new IllegalArgumentException("Photo not found"));

    if (photo.getProfessionalId() == null) {
      throw new IllegalArgumentException("This photo has no feedback to delete");
    }
    if (!photo.getProfessionalId().equals(requestingProfessionalId)) {
      throw new IllegalArgumentException("Only the original author may delete this feedback");
    }

    photo.clearFeedback();
    ProgressPhoto saved = repository.save(photo);
    return new ProgressPhotoResponse(saved.getId(), saved.getTakenAt(), saved.getDescription(),
        Base64.getEncoder().encodeToString(saved.getPhotoData()),
        null, null, null, null, saved.getStudentNotes());
  }
}
