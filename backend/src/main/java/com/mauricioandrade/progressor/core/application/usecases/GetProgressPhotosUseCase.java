package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ProgressPhotoResponse;
import com.mauricioandrade.progressor.core.application.ports.ProgressPhotoRepository;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class GetProgressPhotosUseCase {

  private final ProgressPhotoRepository repository;

  public GetProgressPhotosUseCase(ProgressPhotoRepository repository) {
    this.repository = repository;
  }

  public List<ProgressPhotoResponse> execute(UUID studentId) {
    return repository.findByStudentId(studentId).stream()
        .map(p -> new ProgressPhotoResponse(p.getId(), p.getTakenAt(), p.getDescription(),
            Base64.getEncoder().encodeToString(p.getPhotoData()),
            p.getProfessionalFeedback(), p.getProfessionalId(), p.getProfessionalName(),
            p.getProfessionalRole(), p.getStudentNotes()))
        .toList();
  }
}
